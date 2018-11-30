/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window;

import com.google.inject.Inject;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.bigtows.PinNote;
import org.bigtows.components.PinNoteNotification;
import org.bigtows.components.ThemeHelper;
import org.bigtows.components.enums.ThemeEnum;
import org.bigtows.config.settings.PinNoteSettings;
import org.bigtows.note.storage.EvernoteStorage;
import org.bigtows.note.storage.NoteStorage;
import org.bigtows.note.storage.credential.EvernoteCredential;
import org.bigtows.note.storage.parse.evernote.EvernoteStorageParserImpl;
import org.bigtows.window.component.browser.EvernoteOAuth2Browser;
import org.bigtows.window.component.note.EvernoteNoteView;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Right tool window initialed class
 */
public class RightToolWindow implements ToolWindowFactory {

    @Inject
    private PinNoteSettings pinNoteSettings;

    @Inject
    private EvernoteCredential evernoteCredential;

    @Inject
    private ThemeHelper themeHelper;

    @Inject
    private EvernoteOAuth2Browser evernoteOAuth2Browser;

    @Inject
    private EvernoteNoteView evernoteNoteView;

    /**
     * JFX Panel
     */
    private final JFXPanel fxPanel = new JFXPanel();

    private ThemeEnum themeEnum;

    /**
     * Instance notification helper
     */
    private PinNoteNotification pinNoteNotification = ServiceManager.getService(PinNoteNotification.class);

    /**
     * Instantiates a new Right tool window.
     */
    public RightToolWindow() {
        PinNote.injector.injectMembers(this);
        Platform.setImplicitExit(false);
    }


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        themeEnum = themeHelper.getTheme((ToolWindowImpl) toolWindow);
        JComponent component = toolWindow.getComponent();
        this.initEvernoteToken(project);
        component.getParent().add(fxPanel);
    }

    /**
     * Initialize evernote Token
     *
     * @param project JetBrain project
     */
    private void initEvernoteToken(Project project) {
        String token = evernoteCredential.getToken();
        if (null == token) {
            this.openBrowser(project);
        } else {
            this.initializeNoteView(project);
        }
    }

    /**
     * Initialize note view
     *
     * @param project JetBrain project
     */
    private void initializeNoteView(Project project) {
        try {
            EvernoteStorage storage = new EvernoteStorage(evernoteCredential, new EvernoteStorageParserImpl());
            openNoteView(fxPanel, storage, project);
        } catch (Exception edamSystemException) {
            //Bad token
            evernoteCredential.setToken(null);
            pinNoteNotification.errorNotification("Your token is outdated.", "Please re-authenticate on Evernote.");
            this.initEvernoteToken(project);
        }
    }

    /**
     * Open browser for auth with evernote
     *
     * @param project JetBrains project
     */
    private void openBrowser(Project project) {
        evernoteOAuth2Browser
                .onTokenSuccessProcessed((responseToken) -> {
                    evernoteCredential.setToken(responseToken.getAccessToken());
                    this.initializeNoteView(project);
                })
                .onError((exception) -> pinNoteNotification.errorNotification("Error open browser for authentication", exception))
                .openBrowser(fxPanel, pinNoteSettings);

    }


    /**
     * Open note view
     *
     * @param jfxPanel    Panel where put this component
     * @param noteStorage Storage notes
     * @param project     JetBrains Project
     */
    private void openNoteView(JFXPanel jfxPanel, NoteStorage noteStorage, Project project) {
        evernoteNoteView
                .setTheme(themeEnum)
                .onError((exception) -> {
                    pinNoteNotification.errorNotification("Error note view", exception);
                })
                .openNoteView(jfxPanel, (EvernoteStorage) noteStorage, project);

    }


}