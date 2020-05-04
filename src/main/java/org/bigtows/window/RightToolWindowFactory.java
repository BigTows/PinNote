package org.bigtows.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.bigtows.component.server.token.TokenServer;
import org.bigtows.service.PinNoteService;
import org.bigtows.service.note.notebook.evernote.EvernoteNotebook;
import org.bigtows.service.note.notebook.evernote.creadential.EvernoteNotebookAccessible;
import org.bigtows.window.ui.notetree.factory.EvernoteNoteTreeFactory;
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
        //serviceAccessible.setToken(null);
        if (serviceAccessible.hasToken()) {
            this.initEvernoteToken(toolWindow.getComponent(), serviceAccessible);
        } else {
            this.initPinNote(toolWindow.getComponent());
        }
    }

    private void initEvernoteToken(JComponent root, EvernoteNotebookAccessible evernoteNotebookAccessible) {
        var server = new TokenServer();
        int port = server.getPort();
        server.setEvernoteToken((token) -> {
            evernoteNotebookAccessible.setToken(token);
            initPinNote(root);
            server.stop();
        });
        server.startAsync();
        try {
            Desktop.getDesktop().browse(new URI("http://194.87.103.208/?port=" + port));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void initPinNote(JComponent root) {
        var result = pinNoteService.getNoteRepository().getAll();
        root.add(EvernoteNoteTreeFactory.buildNoteTreeForEvernote((EvernoteNotebook) result.get(0)));
    }
}
