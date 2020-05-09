package org.bigtows.notebook.repository;

import org.bigtows.notebook.Notebook;

import java.util.List;

public interface NotebookRepository {

    List<Notebook<?>> getAll();
}
