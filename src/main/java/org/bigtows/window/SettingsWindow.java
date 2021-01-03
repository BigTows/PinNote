package org.bigtows.window;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import lombok.SneakyThrows;
import org.bigtows.component.http.SimpleHttpServer;
import org.bigtows.service.PinNoteEventManager;
import org.bigtows.service.PinNoteState;
import org.bigtows.service.settings.PinNoteSettings;
import org.bigtows.service.state.StatusConnection;
import org.bigtows.utils.PinNoteIcon;
import org.bigtows.window.ui.pinnote.PinNoteSettingsComponent;
import org.bigtows.window.ui.pinnote.settings.NotebookSource;
import org.bigtows.window.ui.pinnote.settings.StatusSource;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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


    @SneakyThrows
    private List<NotebookSource> buildSources() {
        var pinNoteSettings = ServiceManager.getService(PinNoteSettings.class);
        var state = ServiceManager.getService(PinNoteState.class);
        var eventManager = ServiceManager.getService(PinNoteEventManager.class);
        List<NotebookSource> sources = new ArrayList<>();
        sources.add(NotebookSource.builder()
                .icon(PinNoteIcon.SETTINGS_EVERNOTE_ICON)
                .name("Evernote®")
                .description("<html>Saving your notes in the Evernote® service, there is a possibility of synchronization. This type of storage requires an Internet connection.")
                .status(
                        state.getEvernoteState().isEnable() ?
                                state.getEvernoteState().getStatusConnection() == StatusConnection.CONNECTED ?
                                        StatusSource.ENABLED : state.getEvernoteState().getStatusConnection() == StatusConnection.HAS_PROBLEM
                                        ? StatusSource.HAS_PROBLEM : StatusSource.DISABLED : StatusSource.DISABLED
                )
                .action((panel, currentStatus) -> {
                    if (currentStatus == StatusSource.ENABLED) {
                        state.getEvernoteState().setToken(null);
                        state.getEvernoteState().setStatusConnection(StatusConnection.DISABLED);
                        state.getEvernoteState().setEnable(false);
                        panel.updateSourceStatus(StatusSource.DISABLED);
                        eventManager.callSourceUpdate();
                    } else {
                        var httpServer = ServiceManager.getService(SimpleHttpServer.class);
                        httpServer.registerHandler("/evernote", httpRequest -> {
                            var params = httpRequest.getParams();
                            if (params.get("token") != null) {
                                state.getEvernoteState().setToken(params.get("token"));
                                state.getEvernoteState().setEnable(true);
                                state.getEvernoteState().setStatusConnection(StatusConnection.CONNECTED);
                                state.loadState(state);
                                panel.updateSourceStatus(StatusSource.ENABLED);
                                eventManager.callSourceUpdate();
                                httpRequest.sendResponse(200, "Success, goto IDE");
                            }
                        });
                        httpServer.startAsync();
                        final var urlEvernoteOAuth = pinNoteSettings.getStorage().getEvernote().getOAuth().getUrl() + "?port=" + httpServer.getPort();
                        try {
                            Desktop.getDesktop().browse(new URI(urlEvernoteOAuth));
                        } catch (Exception e) {
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
                })
                .build());

        sources.add(NotebookSource.builder()
                .icon(PinNoteIcon.SETTINGS_LOCAL_NOTE_ICON)
                .name("Local storage")
                .description("<html>Notes are stored on your computer, the fastest and easiest way to store notes")
                .status(state.getLocalNotebookState().isEnable() ? StatusSource.ENABLED : StatusSource.DISABLED)
                .action(((panel, currentSource) -> {
                    if (currentSource == StatusSource.ENABLED) {
                        state.getLocalNotebookState().setEnable(false);
                        panel.updateSourceStatus(StatusSource.DISABLED);
                    } else {
                        state.getLocalNotebookState().setEnable(true);
                        panel.updateSourceStatus(StatusSource.ENABLED);
                    }
                    eventManager.callSourceUpdate();
                }))
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
