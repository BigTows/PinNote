/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.storage;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.google.gson.Gson;
import org.bigtows.note.evernote.EvernoteNotes;
import org.bigtows.note.evernote.EvernoteTarget;
import org.bigtows.note.storage.credential.EvernoteCredential;
import org.bigtows.note.storage.credential.evernote.ServiceType;
import org.bigtows.note.storage.event.UpdateNoteProgressEvent;
import org.bigtows.note.storage.exception.LoadNotesException;
import org.bigtows.note.storage.exception.SaveNotesException;
import org.bigtows.note.storage.exception.StorageException;
import org.bigtows.note.storage.parse.EvernoteStorageParser;
import org.bigtows.note.storage.util.MergeNotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Storage for Evernote Task
 */
public class EvernoteStorage implements NoteStorage<EvernoteNotes, EvernoteTarget> {

    /**
     * Logger of storage
     */
    private final Logger logger;

    /**
     * Note client for evernote services
     */
    private final NoteStoreClient noteStore;

    /**
     * Evernote notebook
     */
    private final Notebook notebook;

    /**
     * Parser
     */
    private final EvernoteStorageParser parser;

    private MergeNotes mergeNotes = new MergeNotes();

    private List<UpdateNoteProgressEvent> progressEvents = new ArrayList<>();

    /**
     * Instantiates a new Evernote storage.
     *
     * @param credential the credential
     * @param parser     the parser
     */
    public EvernoteStorage(EvernoteCredential credential, EvernoteStorageParser parser) {
        this(credential, parser, LoggerFactory.getLogger("Evernote Storage"));
    }

    /**
     * Instantiates a new Evernote storage.
     *
     * @param credential the credential
     * @param parser     the parser
     * @param logger     the logger
     */
    EvernoteStorage(EvernoteCredential credential, EvernoteStorageParser parser, Logger logger) {
        this.logger = logger;
        noteStore = this.initializeNoteStore(credential);
        this.notebook = this.initializeNotebook();
        this.parser = parser;
    }

    /**
     * Initialize Evernote storage note
     *
     * @return note storage
     */
    private NoteStoreClient initializeNoteStore(EvernoteCredential credential) {
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
    public EvernoteNotes updateNotes(EvernoteNotes notes) {
        List<Note> rawNotes = this.tryGetAllRawNotes();
        EvernoteNotes syncedNotes = mergeNotes.sync(notes, this.prepareRawNotesToEvernoteNotes(rawNotes));
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
    private void uploadEvernoteNotes(EvernoteNotes evernoteNotes, List<Note> serverNotes) {
        //Setup progress
        double progress = 0;
        this.changeProgress(progress);
        double partProgress = 1.0 / evernoteNotes.getAllTarget().size();

        for (EvernoteTarget target : evernoteNotes.getAllTarget()) {
            String guid = target.getGuid();
            Note note;
            if (null != guid) {
                note = serverNotes.stream().filter(rawNote -> rawNote.getGuid().equals(guid)).findFirst().orElse(null);
            } else {
                logger.info("{} note created by user, and try upload to server.", target.getName());
                //This NoteTarget created on Client
                note = this.tryCreateTarget(target);
            }
            if (note == null) {
                continue;
            }
            note.setContent(parser.parseTarget(target));
            try {
                this.changeProgress("Upload " + note.getTitle() + " note.", progress);
                noteStore.updateNote(note);
                progress += partProgress;
            } catch (Exception e) {
                logger.error("Error save notes: {}", e.getMessage(), e);
                throw new SaveNotesException("Error save notes.", e);
            }
        }
    }

    @Override
    public void deleteTarget(EvernoteTarget target) {
        new Thread(() -> {
            try {
                noteStore.deleteNote(target.getGuid());
            } catch (Exception e) {
                logger.error("Error delete target {}({})", target.getName(), target.getGuid(), e);
                throw new StorageException("Error delete target: " + target.getName(), e);
            }
        }).start();
    }

    @Override
    public EvernoteNotes getAllNotes() {
        EvernoteNotes notes = this.loadAllNotesFromServer();
        mergeNotes.setCacheNotes(this.cloneNotes(notes));
        return notes;
    }

    @Override
    public void subscribeUpdateNoteProgress(UpdateNoteProgressEvent event) {
        this.progressEvents.add(event);
    }

    /**
     * Added target to notes
     *
     * @param notes      Evernote notes
     * @param nameTarget target
     * @return self notes
     */
    public EvernoteNotes addTarget(EvernoteNotes notes, String nameTarget) {
        EvernoteTarget target = notes.addTarget(nameTarget);
        Note note = this.tryCreateTarget(target);
        try {
            noteStore.updateNote(note);
        } catch (Exception e) {
            e.printStackTrace();
        }
        target.setGuid(note.getGuid());
        return notes;
    }

    /**
     * Generate EvernoteNotes by server note
     *
     * @return Evernote notes
     */
    private EvernoteNotes loadAllNotesFromServer() {
        EvernoteNotes notes = new EvernoteNotes();
        for (Note note : this.tryGetAllRawNotes()) {
            String content;
            try {
                content = noteStore.getNoteContent(note.getGuid());
            } catch (Exception e) {
                logger.error("Error load note.", e);
                throw new LoadNotesException("Error loading notes may be the fault of the server side.", e);
            }
            this.createTargetAndAddedToNotes(notes, content, note);
        }
        return notes;
    }

    /**
     * Create EvernoteNotes from raw Notes
     *
     * @param rawNotes Notes raw from Server
     * @return Collection target's
     */
    private EvernoteNotes prepareRawNotesToEvernoteNotes(List<Note> rawNotes) {
        EvernoteNotes notes = new EvernoteNotes();
        for (Note note : rawNotes) {
            String content;
            try {
                content = noteStore.getNoteContent(note.getGuid());
            } catch (Exception e) {
                logger.error("Error load content for Note: {}.", e.getMessage(), e);
                throw new LoadNotesException("Error loading content for note: " + note.getTitle(), e);
            }
            this.createTargetAndAddedToNotes(notes, content, note);
        }
        return notes;
    }

    /**
     * Create and Added target to EvernoteNote
     *
     * @param evernoteNotes Collection target's
     * @param content       Content note
     * @param rawNote       Note raw from Server
     */
    private void createTargetAndAddedToNotes(EvernoteNotes evernoteNotes, String content, Note rawNote) {
        EvernoteTarget target = parser.parseTarget(evernoteNotes, content);
        if (target != null) {
            target.setNameTarget(rawNote.getTitle());
            target.setGuid(rawNote.getGuid());
            evernoteNotes.addTarget(target);
            logger.info("Target: {}, load success.", rawNote.getTitle());
        } else {
            logger.error("With note: {}, same problems.");
        }
    }

    /**
     * Return raw notes Evernote.
     *
     * @return List raw notes
     */
    private List<Note> tryGetAllRawNotes() {
        double progress = 0;
        this.changeProgress(progress);
        NoteFilter filter = new NoteFilter();
        List<Note> notes = new ArrayList<>();
        filter.setNotebookGuid(notebook.getGuid());
        try {
            int offset = 0;
            while (true) {
                int sizeAfter = notes.size();
                notes.addAll(noteStore.findNotes(filter, offset, 20).getNotes());
                progress += 0.1;
                if (sizeAfter == notes.size()) {
                    break;
                }
                offset += 20;
                this.changeProgress("Load notes form server.", progress);
            }
            this.changeProgress(1);
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
    private Note tryCreateTarget(EvernoteTarget target) {
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
    private EvernoteNotes cloneNotes(EvernoteNotes notes) {
        //Save cache. This Best clone EVER xD
        Gson gson = new Gson();
        return gson.fromJson(notes.toString(), EvernoteNotes.class);
    }

    /**
     * Change progress and title
     *
     * @param title    title of progress bar
     * @param progress value of progress
     */
    private void changeProgress(String title, double progress) {
        this.progressEvents.forEach(event -> event.onChangeProgress(title, progress));
    }

    /**
     * Change progress
     *
     * @param progress value of progress
     */
    private void changeProgress(double progress) {
        this.progressEvents.forEach(event -> event.onChangeProgress(null, progress));
    }

}
