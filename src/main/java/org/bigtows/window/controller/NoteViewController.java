/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.controller;


import com.intellij.openapi.components.ServiceManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import org.bigtows.components.PinNoteNotification;
import org.bigtows.note.Notes;
import org.bigtows.note.evernote.EvernoteNotes;
import org.bigtows.note.storage.EvernoteStorage;
import org.bigtows.note.storage.NoteStorage;
import org.bigtows.note.visual.VisualAdapter;
import org.bigtows.window.controller.exception.ControllerException;
import org.bigtows.window.dialog.AddTargetDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Controller for Note View
 */
public class NoteViewController {

    /**
     * Visual adapter
     */
    private final VisualAdapter visualAdapter;

    @FXML
    public TreeView notesView;

    @FXML
    public Button addTarget;

    /**
     * Logger
     */
    private Logger logger;

    /**
     * Notes
     */
    private Notes notes;

    private EvernoteStorage evernoteStorage;

    /**
     * Instance notification helper
     */
    private PinNoteNotification pinNoteNotification = ServiceManager.getService(PinNoteNotification.class);

    /**
     * Constructor
     *
     * @param visualAdapter Visual perspective notes
     * @param noteStorage   storage notes
     */
    public NoteViewController(VisualAdapter visualAdapter, NoteStorage noteStorage) {
        this(visualAdapter, noteStorage, LoggerFactory.getLogger("NoteViewController"));
    }

    private NoteViewController(VisualAdapter visualAdapter, NoteStorage noteStorage, Logger logger) {
        this.logger = logger;
        this.evernoteStorage = (EvernoteStorage) noteStorage;
        this.notes = this.downloadNotes(visualAdapter, noteStorage);
        this.visualAdapter = visualAdapter;
    }

    /**
     * Download notes, from storage
     *
     * @param visualAdapter Visual adapter
     * @param noteStorage   note storage
     */
    private Notes downloadNotes(VisualAdapter visualAdapter, NoteStorage noteStorage) {
        try {
            Notes notes = noteStorage.getAllNotes();
            visualAdapter.setNotes(notes);
            return notes;
        } catch (Exception e) {
            pinNoteNotification.errorNotification("Error get all notes, from the storage.", e);
            logger.error("Error get all notes, from the storage: {}. Info: ", noteStorage.getClass(), e);
            throw new ControllerException("Storage error.", e);
        }
    }

    /**
     * Initialize form
     */
    public void initialize() {
        addTarget.setOnMouseClicked(this::onClickButtonAddNewTargetEvent);
        visualAdapter.forceUpdate(notesView);
        visualAdapter.setErrorHandler((subjectError, additional) -> {
            String content = null;
            if (additional.length > 0) {
                content = additional[0];
            }
            pinNoteNotification.errorNotification(subjectError, content);
        });
    }


    /**
     * Process click event of button ("AddNewTarget")
     *
     * @param mouseEvent Mouse Event
     */
    private void onClickButtonAddNewTargetEvent(MouseEvent mouseEvent) {
        //Because..... SWING
        SwingUtilities.invokeLater(() -> {
            AddTargetDialog dialog = new AddTargetDialog(true);
            if (dialog.showAndGet()) {
                addNewTarget(dialog.getNameTarget());
            }
        });
    }

    /**
     * Added new target
     *
     * @param nameTarget Name of target
     */
    private void addNewTarget(String nameTarget) {
        this.notes = evernoteStorage.addTarget((EvernoteNotes) notes, nameTarget);
        visualAdapter.setNotes(notes);
        Platform.runLater(() -> {
            visualAdapter.forceUpdate(notesView);
        });

    }


}
