package org.bigtows.service.note.repository;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.bigtows.service.note.notebook.evernote.EvernoteNotebook;
import org.bigtows.service.note.notebook.Notebook;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleNotebookRepository implements NotebookRepository {


    private final List<Notebook> defaultNotebooks = new ArrayList<>();

    private SimpleNotebookRepository(Project project) {
        defaultNotebooks.add(
                project.getService(EvernoteNotebook.class)
        );
    }

    @Override
    public List<Notebook> getAll() {
        return defaultNotebooks;
    }
}
