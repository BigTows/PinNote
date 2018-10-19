/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.bigtows.PinNote;
import org.bigtows.components.PinNoteNotification;
import org.bigtows.components.ThemeManager;
import org.bigtows.components.enums.ThemeEnum;
import org.bigtows.config.settings.PinNoteSettings;
import org.bigtows.note.storage.EvernoteStorage;
import org.bigtows.note.storage.NoteStorage;
import org.bigtows.note.storage.credential.EvernoteCredential;
import org.bigtows.note.storage.parse.evernote.EvernoteStorageParserImpl;
import org.bigtows.note.visual.EvernoteVisualAdapter;
import org.bigtows.window.controller.CallBackWebViewController;
import org.bigtows.window.controller.ResponseToken;
import org.bigtows.window.controller.WebViewController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;

/**
 * Right tool window initialed class
 */
public class RightToolWindow implements ToolWindowFactory {

    private final JFXPanel fxPanel = new JFXPanel();

    private ThemeManager themeManager;
    private ThemeEnum themeEnum;


    private Task.Backgroundable backgroundable;
    @Inject
    private PinNoteSettings pinNoteSettings;

    @Inject
    private EvernoteCredential evernoteCredential;

    /**
     * Instance notification helper
     */
    private PinNoteNotification pinNoteNotification = ServiceManager.getService(PinNoteNotification.class);

    public RightToolWindow() {
        PinNote.injector.injectMembers(this);
    }

    private Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        themeManager = project.getComponent(ThemeManager.class);
        themeEnum = themeManager.getTheme((ToolWindowImpl) toolWindow);
        JComponent component = toolWindow.getComponent();
        this.initEvernoteToken(fxPanel);
        component.getParent().add(fxPanel);
    }

    /**
     * Init evernote Token
     *
     * @param fxPanel panel
     */
    private void initEvernoteToken(JFXPanel fxPanel) {
        String token = evernoteCredential.getToken();
        if (null == token) {
            this.openBrowser(fxPanel, (content -> {
                evernoteCredential.setToken(new Gson().fromJson(content, ResponseToken.class).getAccessToken());
                this.initEvernoteToken(fxPanel);
            }));
        } else {
            try {
                EvernoteStorage storage = new EvernoteStorage(
                        evernoteCredential,
                        new EvernoteStorageParserImpl()
                );
                /*storage.subscribeUpdateNoteProgress(progress -> {
                    ProgressManager.getInstance().run(new Task.Backgroundable(project, "Update") {
                        @Override
                        public void run(@NotNull com.intellij.openapi.progress.ProgressIndicator indicator) {
                            indicator.setIndeterminate(true);
                            indicator.setText("This is how you update the indicator");
                            indicator.setFraction(progress);  // halfway done
                        }
                    });
                });*/


                openNoteView(fxPanel, storage);
            } catch (Exception edamSystemException) {
                //Bad token
                evernoteCredential.setToken(null);
                pinNoteNotification.errorNotification("Your token is outdated.", "Please re-authenticate on Evernote.");
                this.initEvernoteToken(fxPanel);
            }
        }
    }

    private void openBrowser(JFXPanel jfxPanel, CallBackWebViewController callback) {

        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setClassLoader(getClass().getClassLoader()); // set the plugin's class loader
                loader.setLocation(getClass().getResource("/fxml/WebView.fxml"));
                loader.setControllerFactory((clazz) ->
                        new WebViewController(
                                pinNoteSettings,
                                callback
                        )
                );
                Parent root = loader.load();
                Scene scene = new Scene(root);
                jfxPanel.setScene(scene);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void openNoteView(JFXPanel jfxPanel, NoteStorage noteStorage) {
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                new EvernoteVisualAdapter(loader, (EvernoteStorage) noteStorage, this.project);
                loader.setClassLoader(getClass().getClassLoader()); // set the plugin's class loader
                loader.setLocation(getClass().getResource("/fxml/NoteView.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/fxml/style/" + themeManager.getNameResource(themeEnum)).toString());
                jfxPanel.setScene(scene);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}