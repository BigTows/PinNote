package org.bigtows.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.bigtows.component.json.JsonParser;
import org.bigtows.service.note.repository.NotebookRepository;

@Service
public class PinNoteService {

    private final Project project;

    private final JsonParser jsonParser;


    public PinNoteService(Project project) {
        this.project = project;
        this.jsonParser = project.getService(JsonParser.class);
    }


    public NotebookRepository getNoteRepository() {
        return project.getService(NotebookRepository.class);
    }
}
