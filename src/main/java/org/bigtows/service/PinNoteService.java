package org.bigtows.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ResourceUtil;
import lombok.SneakyThrows;
import org.bigtows.component.json.JsonParser;
import org.bigtows.notebook.repository.NotebookRepository;
import org.bigtows.service.settings.PinNoteSettings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
public class PinNoteService {

    private final Project project;

    private final JsonParser jsonParser;
    private final PinNoteState state;

    private PinNoteSettings settings;


    public PinNoteService(Project project) {
        this.project = project;
        this.jsonParser = project.getService(JsonParser.class);
        this.state = ServiceManager.getService(PinNoteState.class);
    }


    public NotebookRepository getNotebookRepository() {
        return project.getService(NotebookRepository.class);
    }

    @SneakyThrows
    public PinNoteSettings getSettings() {
        if (settings == null) {
            var jsonContent = new BufferedReader(new InputStreamReader(
                    ResourceUtil.getResourceAsStream(getClass().getClassLoader(), "", "settings.json")
            )).lines().collect(Collectors.joining("\n"));

            settings = this.jsonParser.parse(jsonContent, PinNoteSettings.class);
        }
        return settings;
    }

    public PinNoteState getState() {
        return state;
    }
}
