/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.component.note;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowFactory;
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
@Singleton
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

    /**
     * Tool window
     */
    private ToolWindowFactory toolWindow;

    /**
     * Visual adapter
     */
    private EvernoteVisualAdapter adapter;

    /**
     * Instantiates a new Evernote note view.
     *
     * @param themeHelper the theme helper
     */
    @Inject
    public EvernoteNoteView(ThemeHelper themeHelper) {
        this.themeHelper = themeHelper;
    }

    /**
     * Set tool window
     *
     * @param toolWindow tool window
     * @return self tool window
     */
    public EvernoteNoteView setToolWindow(ToolWindowFactory toolWindow) {
        this.toolWindow = toolWindow;
        return this;
    }

    /**
     * Gets tool window.
     *
     * @return the tool window
     */
    public ToolWindowFactory getToolWindow() {
        return toolWindow;
    }

    /**
     * Set theme for component
     *
     * @param theme theme
     * @return self theme
     */
    public EvernoteNoteView setTheme(ThemeEnum theme) {
        this.theme = theme;
        return this;
    }

    /**
     * Subscribe on error
     *
     * @param noteViewErrorEvent on error handler
     * @return self evernote note view
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
     * Get evernote visual adapter
     *
     * @return adapter adapter
     */
    public EvernoteVisualAdapter getAdapter() {
        return adapter;
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
        this.adapter = new EvernoteVisualAdapter(fxmlLoader, noteStorage, project);
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
