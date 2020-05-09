package org.bigtows.notebook.local;

import com.intellij.openapi.components.ServiceManager;
import org.bigtows.notebook.Notebook;

import java.util.List;

public class LocalNotebook implements Notebook<LocalNote> {

    private final LocalNotebookStorageState storage;

    public LocalNotebook() {
        this.storage = ServiceManager.getService(LocalNotebookStorageState.class);
    }

    @Override
    public String getName() {
        return "LocalNote";
    }

    @Override
    public List<LocalNote> updateNotes(List<LocalNote> notes) {

        this.storage.getStorage().setNoteList(notes);
        this.storage.loadState(storage);
        return this.storage.getStorage().getNoteList();
    }

    @Override
    public List<LocalNote> getAllNotes() {
        return this.storage.getStorage().getNoteList();
    }

    @Override
    public void deleteNote(LocalNote note) {
        this.storage.getStorage().getNoteList().remove(note);
    }
}
