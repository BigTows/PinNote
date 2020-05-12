package org.bigtows.notebook;

import java.util.List;

/**
 * Notebook this is an entity that stores notes
 *
 * @param <T> any implementation of Note
 */
public interface Notebook<T extends Note<? extends Task>> {

    /**
     * Get name of notebook
     *
     * @return name of notebook
     */
    String getName();

    /**
     * Update notes
     *
     * @param notes collection of notes
     * @return updated collection
     */
    List<T> updateNotes(List<T> notes);

    /**
     * Get all notes
     *
     * @return list of notes
     */
    List<T> getAllNotes();

    /**
     * Delete note from collection notes
     *
     * @param note instance from collection
     */
    void deleteNote(T note);
}
