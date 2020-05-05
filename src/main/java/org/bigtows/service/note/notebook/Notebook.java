package org.bigtows.service.note.notebook;

import java.util.List;

public interface Notebook<T extends Note<? extends Task>> {

    public String getName();


    List<T> updateNotes(List<T> notes);

    List<T> getAllNotes();

    void deleteNote(T note);
}
