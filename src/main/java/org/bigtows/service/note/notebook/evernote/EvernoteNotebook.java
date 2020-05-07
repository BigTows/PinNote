package org.bigtows.service.note.notebook.evernote;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.bigtows.service.note.notebook.evernote.creadential.EvernoteNotebookAccessible;
import org.bigtows.service.note.notebook.evernote.creadential.ServiceType;
import org.bigtows.service.note.notebook.evernote.exception.LoadNotesException;
import org.bigtows.service.note.notebook.evernote.exception.SaveNotesException;
import org.bigtows.service.note.notebook.evernote.exception.StorageException;
import org.bigtows.service.note.notebook.evernote.parser.EvernoteStorageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvernoteNotebook implements org.bigtows.service.note.notebook.Notebook<EvernoteNote> {

    /**
     * Logger of storage
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Note client for evernote services
     */
    private final NoteStoreClient noteStore;

    /**
     * Evernote notebook
     */
    private final com.evernote.edam.type.Notebook notebook;

    /**
     * Parser
     */
    private final EvernoteStorageParser parser = new EvernoteStorageParser();

    private final Project project;


    /**
     * Component for merge notes
     */
    private final MergeNotes mergeNotes = new MergeNotes();

    public EvernoteNotebook(Project project) {
        this.project = project;
        var service = project.getService(EvernoteNotebookAccessible.class);

        noteStore = this.initializeNoteStore(project.getService(EvernoteNotebookAccessible.class));
        this.notebook = this.initializeNotebook();
    }


    /**
     * Initialize Evernote storage note
     *
     * @return note storage
     */
    private NoteStoreClient initializeNoteStore(EvernoteNotebookAccessible credential) {
        try {
            ClientFactory factory = new ClientFactory(
                    new EvernoteAuth(
                            credential.getServiceType() == ServiceType.PRODUCTION ? EvernoteService.PRODUCTION : EvernoteService.SANDBOX,
                            credential.getToken()
                    )
            );
            return factory.createNoteStoreClient();
        } catch (Exception e) {
            logger.error("Error initialize Evernote note storage.", e);
            throw new StorageException("Error initialize Evernote note storage.", e);
        }
    }

    /**
     * Initialize Notebook
     *
     * @return Notebook
     */
    private Notebook initializeNotebook() {

        Notebook notebook = null;

        for (Notebook notebookFromStorage : this.getNotebooks()) {
            if (notebookFromStorage.getName().equals("PinNote")) {
                notebook = notebookFromStorage;
                break;
            }
        }

        if (notebook == null) {
            notebook = this.createNotebook("PinNote");
        }

        return notebook;
    }

    /**
     * Get all Notebooks
     *
     * @return Collection Notebooks
     */
    private List<Notebook> getNotebooks() {
        try {
            return noteStore.listNotebooks();
        } catch (Exception e) {
            logger.error("Error getting list notebooks.", e);
            throw new StorageException("Error getting list notebooks.", e);
        }
    }

    /**
     * Create Notebook on Evernote
     *
     * @param name Name of notebook
     * @return Instance notebook
     */
    private Notebook createNotebook(String name) {
        Notebook notebook = new Notebook();
        notebook.setName(name);
        try {
            return noteStore.createNotebook(notebook);
        } catch (Exception e) {
            logger.error("Error create notebook, with name {}.", name, e);
            throw new StorageException("Error create notebook.", e);
        }
    }

    @Override
    public List<EvernoteNote> updateNotes(List<EvernoteNote> notes) {
        List<Note> rawNotes = this.tryGetAllRawNotes();
        List<EvernoteNote> syncedNotes = mergeNotes.sync(notes, this.prepareRawNotesToEvernoteNotes(rawNotes));
        var res = rawNotes.stream().filter(note -> syncedNotes.stream().filter(syncedNote -> syncedNote.getId().equals(note.getGuid())).findFirst().isEmpty())
                .collect(Collectors.toList());
        res.forEach(re -> this.deleteNoteByGuid(re.getGuid()));
        this.uploadEvernoteNotes(syncedNotes, rawNotes);
        mergeNotes.setCacheNotes(this.cloneNotes(notes));
        return syncedNotes;
    }

    /**
     * Upload notes to server
     *
     * @param evernoteNotes Evernote notes for upload to server
     * @param serverNotes   Server notes
     */
    private void uploadEvernoteNotes(List<EvernoteNote> evernoteNotes, List<Note> serverNotes) {

        for (EvernoteNote target : evernoteNotes) {
            String guid = target.getId();
            Note note;
            if (null != guid && !guid.equals("")) {
                note = serverNotes.stream().filter(rawNote -> rawNote.getGuid().equals(guid)).findFirst().orElse(null);
            } else {
                logger.info("{} note created by user, and try upload to server.", target.getName());
                //This NoteTarget created on Client
                note = this.tryCreateTarget(target);
                target.setId(note.getGuid());
            }
            if (note == null) {
                continue;
            }
            note.setContent(parser.parseTarget(target));
            try {
                noteStore.updateNote(note);
            } catch (Exception e) {
                logger.error("Error save notes: {}", e.getMessage(), e);
                throw new SaveNotesException("Error save notes.", e);
            }
        }
    }

    @Override
    public void deleteNote(EvernoteNote target) {
        this.deleteNoteByGuid(target.getId());
    }

    private void deleteNoteByGuid(String guid) {
        try {
            noteStore.deleteNote(guid);
        } catch (Exception e) {
            logger.error("Error delete target {}()", guid, e);
            throw new StorageException("Error delete target: " + guid, e);
        }
    }

    @Override
    public List<EvernoteNote> getAllNotes() {
        var notes = this.loadAllNotesFromServer();
        mergeNotes.setCacheNotes(notes);
        return notes;
    }


    /**
     * Added target to notes
     *
     * @param notes      Evernote notes
     * @param nameTarget target
     * @return self notes
     */
    public List<EvernoteNote> addTarget(List<EvernoteNote> notes, String nameTarget) {

        EvernoteNote target = EvernoteNote.builder()
                .name(nameTarget)
                .build();

        notes.add(target);
        Note note = this.tryCreateTarget(target);
        try {
            noteStore.updateNote(note);
        } catch (Exception e) {
            e.printStackTrace();
        }
        target.setId(note.getGuid());
        return notes;
    }

    /**
     * Generate EvernoteNotes by server note
     *
     * @return Evernote notes
     */
    private List<EvernoteNote> loadAllNotesFromServer() {
        List<EvernoteNote> evernoteNotes = new ArrayList<>();
        for (Note serverNote : this.tryGetAllRawNotes()) {
            String content;
            try {
                content = noteStore.getNoteContent(serverNote.getGuid());
            } catch (Exception e) {
                logger.error("Error load note.", e);
                throw new LoadNotesException("Error loading notes may be the fault of the server side.", e);
            }
            var note = this.createTargetAndAddedToNotesv2(content, serverNote);
            if (note != null) {
                evernoteNotes.add(note);
            }
        }
        return evernoteNotes;
    }

    /**
     * Create EvernoteNotes from raw Notes
     *
     * @param rawNotes Notes raw from Server
     * @return Collection target's
     */
    private List<EvernoteNote> prepareRawNotesToEvernoteNotes(List<Note> rawNotes) {
        List<EvernoteNote> result = new ArrayList<>();
        for (Note note : rawNotes) {
            String content;
            try {
                content = noteStore.getNoteContent(note.getGuid());
            } catch (Exception e) {
                logger.error("Error load content for Note: {}.", e.getMessage(), e);
                throw new LoadNotesException("Error loading content for note: " + note.getTitle(), e);
            }
            result.add(this.createTargetAndAddedToNotesv2(content, note));
        }
        return result;
    }


    /**
     * Create and Added target to EvernoteNote
     *
     * @param content Content note
     * @param rawNote Note raw from Server
     * @return
     */
    private EvernoteNote createTargetAndAddedToNotesv2(String content, Note rawNote) {
        EvernoteNote target = parser.parseTarget(this, content);
        if (target != null) {
            target.setName(rawNote.getTitle());
            target.setId(rawNote.getGuid());
            logger.info("Target: {}, load success.", rawNote.getTitle());
            return target;
        } else {
            logger.error("With note: {}, same problems.");
        }
        return null;
    }

    /**
     * Return raw notes Evernote.
     *
     * @return List raw notes
     */
    private List<Note> tryGetAllRawNotes() {
        NoteFilter filter = new NoteFilter();
        List<Note> notes = new ArrayList<>();
        filter.setNotebookGuid(notebook.getGuid());
        try {
            int offset = 0;
            while (true) {
                int sizeAfter = notes.size();
                notes.addAll(noteStore.findNotes(filter, offset, 20).getNotes());
                if (sizeAfter == notes.size()) {
                    break;
                }
                offset += 20;
            }
            return notes;
        } catch (Exception e) {
            logger.error("Error load notes: {}", e.getMessage(), e);
            throw new LoadNotesException("Error load notes", e);
        }
    }

    /**
     * Try create target on server
     *
     * @param target Evernote Target
     * @return Server note
     */
    private Note tryCreateTarget(EvernoteNote target) {
        NoteFilter filter = new NoteFilter();
        filter.setNotebookGuid(notebook.getGuid());
        NoteList noteList;
        try {
            noteList = noteStore.findNotes(filter, 0, 100);
        } catch (Exception e) {
            logger.error("Error load notes.", e);
            throw new LoadNotesException("Error load notes.", e);
        }
        //If service has this target
        for (Note note : noteList.getNotes()) {
            if (note.getTitle().equals(target.getName())) {
                return note;
            }
        }

        Note note = new Note();
        note.setTitle(target.getName());
        note.setContent(this.parser.parseTarget(target));
        note.setNotebookGuid(notebook.getGuid());

        try {
            return noteStore.createNote(note);
        } catch (Exception e) {
            logger.error("Error save notes.", e);
            throw new SaveNotesException("Error save notes.", e);
        }
    }

    /**
     * Clone Evernote Notes
     *
     * @param notes Master notes
     * @return Cloned notes
     */
    private List<EvernoteNote> cloneNotes(List<EvernoteNote> notes) {
        List<EvernoteNote> clonedNotes = new ArrayList<>();
        notes.forEach(note -> {
            var clonedNote = EvernoteNote.builder()
                    .id(note.getId())
                    .name(note.getName())
                    .build();

            note.getTasks().forEach(evernoteTask -> {
                var clonedTask = EvernoteTask.builder()
                        .id(evernoteTask.getId())
                        .name(evernoteTask.getName())
                        .checked(evernoteTask.isChecked())
                        .build();
                evernoteTask.getSubTask().forEach(evernoteSubTask -> {
                    var clonedSubTask = EvernoteSubTask.builder()
                            .id(evernoteSubTask.getId())
                            .name(evernoteSubTask.getName())
                            .checked(evernoteSubTask.isChecked())
                            .build();
                    clonedTask.getSubTask().add(clonedSubTask);
                });
                clonedNote.getTasks().add(clonedTask);
            });
            clonedNotes.add(clonedNote);
        });
        return clonedNotes;
    }

    @Override
    public String getName() {
        return "Evernote";
    }
}
