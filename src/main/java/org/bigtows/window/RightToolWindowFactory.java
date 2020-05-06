package org.bigtows.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.util.ImageLoader;
import com.intellij.util.ui.JBImageIcon;
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
        if (serviceAccessible.hasToken()) {
            this.initEvernoteToken(project, toolWindow.getComponent(), serviceAccessible);
        } else {
            try {
                this.initPinNote(project, toolWindow.getComponent());
            } catch (Throwable e) {
                this.initEvernoteToken(project, toolWindow.getComponent(), serviceAccessible);
            }
        }
    }

    private void initEvernoteToken(Project project, JComponent root, EvernoteNotebookAccessible evernoteNotebookAccessible) {
        var server = new TokenServer();
        int port = server.getPort();
        server.setEvernoteToken((token) -> {
            evernoteNotebookAccessible.setToken(token);
            initPinNote(project, root);
            server.stop();
        });
        server.startAsync();
        try {
            Desktop.getDesktop().browse(new URI("https://pinnote.bigtows.org/?port=" + port));
        } catch (IOException | URISyntaxException e) {
            JOptionPane.showInputDialog(null,
                    "<html>Follow this link manually: <a href='https://pinnote.bigtows.org/?port=" + port + "'>Link</a>",
                    "Can't open link in your browser!",
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    null,
                    "https://pinnote.bigtows.org/?port=" + port
            );
        }
    }

    private void initPinNote(Project project, JComponent root) {
        root.removeAll();
        var pinNoteComponent = new PinNoteComponent();

        var result = pinNoteService.getNoteRepository().getAll();

        SwingUtilities.invokeLater(()->{
            root.add(pinNoteComponent.getRoot());

            pinNoteComponent.addNotebook(
                    result.get(0),
                    new JBImageIcon(ImageLoader.loadFromResource("/icons/evernote20x20.png")),
                    EvernoteNoteTreeFactory.buildNoteTreeForEvernote(project, (EvernoteNotebook) result.get(0))
            );
        });
    }
}
