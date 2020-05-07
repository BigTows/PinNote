package org.bigtows.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.util.ImageLoader;
import com.intellij.util.ui.JBImageIcon;
import org.bigtows.component.http.PortUtility;
import org.bigtows.component.http.SimpleHttpServer;
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
        var httpServer = project.getService(SimpleHttpServer.class);
        int port = PortUtility.getFreePort();
        httpServer.registerHandler("/evernote", httpRequest -> {
            var params = httpRequest.getParams();
            if (params.get("token") != null) {
                evernoteNotebookAccessible.setToken(params.get("token"));
                initPinNote(project, root);
                httpRequest.sendResponse(200, "Success, goto IDE");
                httpServer.stopAsync();
            }
        });
        httpServer.startAsync(port);
        final var urlEvernoteOAuth = pinNoteService.getSettings().getStorage().getEvernote().getOAuth().getUrl() + "?port=" + port;
        try {
            Desktop.getDesktop().browse(new URI(urlEvernoteOAuth));
        } catch (IOException | URISyntaxException e) {
            JOptionPane.showInputDialog(null,
                    "<html>Follow this link manually: <a href='" + urlEvernoteOAuth + "'>Link</a>",
                    "Can't open link in your browser!",
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    null,
                    urlEvernoteOAuth
            );
        }
    }

    private void initPinNote(Project project, JComponent root) {
        root.removeAll();
        var pinNoteComponent = new PinNoteComponent();

        var result = pinNoteService.getNoteRepository().getAll();

        SwingUtilities.invokeLater(() -> {
            root.add(pinNoteComponent.getRoot());

            pinNoteComponent.addNotebook(
                    result.get(0),
                    new JBImageIcon(ImageLoader.loadFromResource("/icons/evernote20x20.png")),
                    EvernoteNoteTreeFactory.buildNoteTreeForEvernote(project, (EvernoteNotebook) result.get(0))
            );
        });
    }
}
