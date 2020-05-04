package org.bigtows.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.bigtows.component.server.token.TokenServer;
import org.bigtows.service.PinNoteService;
import org.bigtows.service.note.notebook.evernote.EvernoteNotebook;
import org.bigtows.service.note.notebook.evernote.creadential.EvernoteNotebookAccessible;
import org.bigtows.window.ui.notetree.factory.EvernoteNoteTreeFactory;
import org.bigtows.window.ui.pinnote.PinNoteComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class RightToolWindowFactory implements ToolWindowFactory {

    private PinNoteService pinNoteService;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        pinNoteService = project.getService(PinNoteService.class);

        var serviceAccessible = project.getService(EvernoteNotebookAccessible.class);
        serviceAccessible.setToken(null);
        if (serviceAccessible.hasToken()) {
            this.initEvernoteToken(project, toolWindow.getComponent(), serviceAccessible);
        } else {
            this.initPinNote(project, toolWindow.getComponent());
        }
    }

    private void initEvernoteToken(Project project, JComponent root, EvernoteNotebookAccessible evernoteNotebookAccessible) {
        var server = new TokenServer();
        int port = server.getPort();
        server.setEvernoteToken((token) -> {
            evernoteNotebookAccessible.setToken(token);
            initPinNote(project, root);
            //TODO is shit
            //TODO PinNote in new.
            SwingUtilities.invokeLater(server::stop);
        });
        server.startAsync();
        try {
            Desktop.getDesktop().browse(new URI("https://pinnote.bigtows.org/?port=" + port));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void initPinNote(Project project, JComponent root) {
        var pinNoteComponent = new PinNoteComponent();
        var result = pinNoteService.getNoteRepository().getAll();
        root.add(pinNoteComponent.getRoot());

        pinNoteComponent.addNotebook(
                result.get(0),
                EvernoteNoteTreeFactory.buildNoteTreeForEvernote(project, (EvernoteNotebook) result.get(0))
        );
    }
}
