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
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import com.google.gson.Gson;
import org.bigtows.note.NoteTarget;
import org.bigtows.note.evernote.EvernoteNotes;
import org.bigtows.note.evernote.EvernoteTarget;
import org.bigtows.note.storage.credential.EvernoteCredential;
import org.bigtows.note.storage.credential.evernote.ServiceType;
import org.bigtows.note.storage.exception.LoadNotesException;
import org.bigtows.note.storage.exception.SaveNotesException;
import org.bigtows.note.storage.exception.StorageException;
import org.bigtows.note.storage.parse.EvernoteStorageParser;
import org.bigtows.note.storage.util.MergeNotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EvernoteStorage implements NoteStorage<EvernoteNotes, EvernoteTarget> {

    private final Logger logger;

    private final NoteStoreClient noteStore;

    private final Notebook notebook;

    private final EvernoteStorageParser parser;

    private MergeNotes mergeNotes = new MergeNotes();

    private Executor executor = Executors.newSingleThreadExecutor();


    public EvernoteStorage(EvernoteCredential credential, EvernoteStorageParser parser) {
        this(credential, parser, LoggerFactory.getLogger("Evernote Storage"));
    }

    public EvernoteStorage(EvernoteCredential credential, EvernoteStorageParser parser, Logger logger) {
        noteStore = this.initializeNoteStore(credential);
        this.logger = logger;
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
        EvernoteNotes syncedNotes = mergeNotes.sync(notes, this.loadAllNotesFromServer());
        for (EvernoteTarget target : syncedNotes.getAllTarget()) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String guid = target.getGuid();
            Note note;
            if (null == guid) {
                //This NoteTarget created on Client
                note = this.tryCreateTarget(target);
            } else {
                try {
                    note = noteStore.getNote(guid, false, false, false, false);
                } catch (Exception e) {
                    logger.error("Error load note.", e);
                    throw new LoadNotesException("Error load note.", e);
                }
            }
            note.setContent(parser.parseTarget(target));
            try {
                noteStore.updateNote(note);
            } catch (Exception e) {
                logger.error("Error save notes.", e);
                throw new SaveNotesException("Error save notes.", e);
            }
        }
        mergeNotes.setCacheNotes(this.cloneNotes(notes));
        return syncedNotes;
    }

    @Override
    public void deleteTarget(EvernoteTarget target) {

    }

    @Override
    public EvernoteNotes getAllNotes() {
        EvernoteNotes notes = this.loadAllNotesFromServer();
        mergeNotes.setCacheNotes(this.cloneNotes(notes));
        return notes;
    }

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

    private EvernoteNotes loadAllNotesFromServer() {
        EvernoteNotes notes = new EvernoteNotes();
        for (Note note : this.tryGetAllTarget()) {
            String content;
            try {
                content = noteStore.getNoteContent(note.getGuid());
                Thread.sleep(40);
            } catch (Exception e) {
                logger.error("Error load note.", e);
                throw new LoadNotesException("Error load note.", e);
            }
            EvernoteTarget target = parser.parseTarget(notes, content);
            if (target != null) {
                target.setNameTarget(note.getTitle());
                target.setGuid(note.getGuid());
                notes.addTarget(target);
                logger.info("Target: {}, load success.", note.getTitle());
            }
        }
        return notes;
    }


    private List<Note> tryGetAllTarget() {
        NoteFilter filter = new NoteFilter();
        filter.setNotebookGuid(notebook.getGuid());

        //Hmmm why 10000?!??!?!?
        try {
            return noteStore.findNotes(filter, 0, 10000).getNotes();
        } catch (Exception e) {
            logger.error("Error load notes", e);
            throw new LoadNotesException("Error load notes", e);
        }
    }


    private Note tryCreateTarget(NoteTarget target) {
        NoteFilter filter = new NoteFilter();
        filter.setNotebookGuid(notebook.getGuid());
        NoteList noteList;
        try {
            noteList = noteStore.findNotes(filter, 0, 100);
        } catch (Exception e) {
            logger.error("Error load notes.", e);
            throw new LoadNotesException("Error load notes.", e);
        }
        for (Note note : noteList.getNotes()) {
            if (note.getTitle().equals(target.getName())) {
                return note;
            }
        }

        Note note = new Note();
        note.setTitle(target.getName());
        note.setContent(this.getDefaultContent());
        note.setNotebookGuid(notebook.getGuid());

        try {
            return noteStore.createNote(note);
        } catch (Exception e) {
            logger.error("Error save notes.", e);
            throw new SaveNotesException("Error save notes.", e);
        }
    }


    private EvernoteNotes cloneNotes(EvernoteNotes notes) {
        //TODO This Best clone EVER xD
        //Save cache
        Gson gson = new Gson();
        return gson.fromJson(notes.toString(), EvernoteNotes.class);
    }


    private String getDefaultContent(String... data) {
        StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">"
                + "<en-note>");

        for (String content : data) {
            builder.append(content);
        }
        return builder.append("</en-note>").toString();
    }

}
