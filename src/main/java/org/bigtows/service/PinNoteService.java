package org.bigtows.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.util.ResourceUtil;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.bigtows.component.json.JsonParser;
import org.bigtows.service.note.repository.NotebookRepository;
import org.bigtows.service.settings.PinNoteSettings;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Service
public class PinNoteService {

    private final Project project;

    private final JsonParser jsonParser;

    private PinNoteSettings settings;


    public PinNoteService(Project project) {
        this.project = project;
        this.jsonParser = project.getService(JsonParser.class);
    }


    public NotebookRepository getNoteRepository() {
        return project.getService(NotebookRepository.class);
    }

    @SneakyThrows
    public PinNoteSettings getSettings() {
        if (settings == null) {
            var jsonContent = IOUtils.toString(
                    ResourceUtil.getResourceAsStream(getClass().getClassLoader(), "", "settings.json"), StandardCharsets.UTF_8);
            settings = this.jsonParser.parse(jsonContent, PinNoteSettings.class);
        }
        return settings;
    }
}
