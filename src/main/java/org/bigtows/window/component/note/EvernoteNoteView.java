/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.component.note;

import com.google.inject.Inject;
import com.intellij.openapi.project.Project;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.bigtows.components.ThemeHelper;
import org.bigtows.components.enums.ThemeEnum;
import org.bigtows.note.storage.EvernoteStorage;
import org.bigtows.note.visual.EvernoteVisualAdapter;
import org.bigtows.window.component.note.event.EvernoteNoteViewErrorEvent;

import java.io.IOException;

/**
 * Component of Evernote NoteView
 */
public class EvernoteNoteView {

    /**
     * Theme manager
     */
    private final ThemeHelper themeHelper;

    /**
     * Theme for component
     */
    private ThemeEnum theme;

    /**
     * Callback on error
     */
    private EvernoteNoteViewErrorEvent noteViewErrorEvent;

    @Inject
    public EvernoteNoteView(ThemeHelper themeHelper) {
        this.themeHelper = themeHelper;
    }

    /**
     * Set theme for component
     *
     * @param theme theme
     * @return self
     */
    public EvernoteNoteView setTheme(ThemeEnum theme) {
        this.theme = theme;
        return this;
    }

    /**
     * Subscribe on error
     *
     * @param noteViewErrorEvent on error handler
     * @return self
     */
    public EvernoteNoteView onError(EvernoteNoteViewErrorEvent noteViewErrorEvent) {
        this.noteViewErrorEvent = noteViewErrorEvent;
        return this;
    }

    /**
     * Open note view
     *
     * @param jfxPanel    JFX panel
     * @param noteStorage note storage
     * @param project     JetBrain project
     */
    public void openNoteView(JFXPanel jfxPanel, EvernoteStorage noteStorage, Project project) {
        Platform.runLater(() -> {
            try {
                Scene scene = this.buildScene(noteStorage, project);
                jfxPanel.setScene(scene);
            } catch (IOException e) {
                noteViewErrorEvent.onError(e);
            }
        });
    }

    /**
     * Build scene
     *
     * @param noteStorage note storage
     * @param project     JetBrain project
     * @return scene
     * @throws IOException when can't load some files.
     */
    private Scene buildScene(EvernoteStorage noteStorage, Project project) throws IOException {
        FXMLLoader fxmlLoader = this.buildFXMLLoader();
        new EvernoteVisualAdapter(fxmlLoader, noteStorage, project);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/fxml/style/" + themeHelper.getNameResource(theme)).toString());
        return scene;
    }

    /**
     * Build FXML loader
     *
     * @return FXML loader
     */
    private FXMLLoader buildFXMLLoader() {
        FXMLLoader loader = new FXMLLoader();
        loader.setClassLoader(getClass().getClassLoader());
        loader.setLocation(getClass().getResource("/fxml/NoteView.fxml"));
        return loader;
    }
}
