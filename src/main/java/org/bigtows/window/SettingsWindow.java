package org.bigtows.window;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.ImageLoader;
import org.bigtows.window.ui.pinnote.PinNoteSettingsComponent;
import org.bigtows.window.ui.pinnote.settings.NotebookSource;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SettingsWindow implements Configurable {
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        var panel = new PinNoteSettingsComponent();
        panel.addedSources(buildSources());

        return panel.getRoot();
    }


    private List<NotebookSource> buildSources() {
        List<NotebookSource> sources = new ArrayList<>();


        sources.add(NotebookSource.builder()
                .image(ImageLoader.loadFromResource("/icons/evernote50x50.png"))
                .name("Evernote")
                .description("<html>Evernote is an app designed for note taking, organizing, task management, and archiving.<br> It is developed by the Evernote Corporation, headquartered in Redwood City, California.<br> The app allows users to create notes, which can be text, drawings, photographs, or saved web content. Notes are stored in notebooks and can be tagged, annotated, edited, searched, given attachments, and exported.")
                .status(true)
                .build());

        sources.add(NotebookSource.builder()
                .image(ImageLoader.loadFromResource("/icons/evernote50x50.png"))
                .name("Local storage")
                .description("Locally storage for your notes")
                .status(true)
                .build());
        return sources;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {

    }
}
