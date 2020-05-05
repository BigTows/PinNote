package org.bigtows.service.note.repository;

import org.bigtows.service.note.notebook.Notebook;

import java.util.List;

public interface NotebookRepository {

    List<Notebook> getAll();
}
