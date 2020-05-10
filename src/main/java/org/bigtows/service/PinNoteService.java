package org.bigtows.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.bigtows.notebook.repository.NotebookRepository;
import org.bigtows.service.settings.PinNoteSettings;

@Service
public class PinNoteService {

    private final Project project;

    private final PinNoteState state;

    private PinNoteSettings settings;


    public PinNoteService(Project project) {
        this.project = project;
        this.state = ServiceManager.getService(PinNoteState.class);
    }


    public NotebookRepository getNotebookRepository() {
        return project.getService(NotebookRepository.class);
    }

    public PinNoteSettings getSettings() {
        if (this.settings == null) {
            this.settings = ServiceManager.getService(PinNoteSettings.class);
        }
        return this.settings;
    }

    public PinNoteState getState() {
        return state;
    }
}
