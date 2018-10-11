/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.visual;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.bigtows.note.NoteSubTask;
import org.bigtows.note.NoteTask;
import org.bigtows.note.Task;
import org.bigtows.note.evernote.EvernoteNotes;
import org.bigtows.note.evernote.EvernoteSubTask;
import org.bigtows.note.evernote.EvernoteTarget;
import org.bigtows.note.evernote.EvernoteTask;
import org.bigtows.note.storage.EvernoteStorage;
import org.bigtows.note.visual.component.NoteCheckBox;
import org.bigtows.note.visual.component.NoteCustomComponent;
import org.bigtows.note.visual.component.TargetTreeItem;
import org.bigtows.note.visual.component.TaskTreeItem;
import org.bigtows.note.visual.enums.NoteStatus;
import org.bigtows.window.controller.NoteViewController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Evernote Notes visualization
 */
public class EvernoteVisualAdapter implements VisualAdapter<TreeView, EvernoteNotes> {

    /**
     * Evernote notes
     */
    private EvernoteNotes notes;

    /**
     * Flag indicating the state of the fields
     */
    private NoteStatus noteStatus = NoteStatus.NONE;

    /**
     * Evernote storage
     */
    private EvernoteStorage noteStorage;

    private VisualAdapterErrorHandler errorHandler;

    private Logger logger;

    /**
     * Constructor with default logger
     *
     * @param fxmlLoader  fxml loader
     * @param noteStorage Notes Storage
     */
    public EvernoteVisualAdapter(FXMLLoader fxmlLoader, EvernoteStorage noteStorage) {
        this(fxmlLoader, noteStorage, LoggerFactory.getLogger("EvernoteVisualAdapter"));
    }

    /**
     * Constructor
     *
     * @param fxmlLoader  fxml loader
     * @param noteStorage Notes Storage
     * @param logger      logget
     */
    public EvernoteVisualAdapter(FXMLLoader fxmlLoader, EvernoteStorage noteStorage, Logger logger) {
        fxmlLoader.setControllerFactory((clazz) -> new NoteViewController(this, noteStorage));
        this.noteStorage = noteStorage;
        this.logger = logger;
    }


    @Override
    public void setNotes(EvernoteNotes notes) {
        this.notes = notes;
    }

    private TreeView lastTreeView;

    @Override
    public void forceUpdate(TreeView treeView) {

        lastTreeView = treeView;
        treeView.setRoot(null);
        this.initializeTreeView(treeView);
        List<TreeItem> listTarget = new ArrayList<>();

        for (EvernoteTarget target : notes.getAllTarget()) {
            listTarget.add(this.fill(target));
        }

        treeView.getRoot().getChildren().addAll(listTarget.toArray(new TreeItem[0]));
        this.updateNoteCustomComponents(treeView);
        this.enableAutoSave();
    }

    public void update(TreeView treeView) {
        this.syncTarget(treeView);
    }


    private void syncTarget(TreeView treeView) {
        ObservableList<TreeItem> listVisualTarget = treeView.getRoot().getChildren();

        for (TreeItem visualTarget : listVisualTarget) {
            if (visualTarget instanceof TargetTreeItem && ((TargetTreeItem) visualTarget).getTarget() instanceof EvernoteTarget) {
                if (this.hasTargetTreeItemInNotes((TargetTreeItem) visualTarget, notes)) {
                    ((TargetTreeItem) visualTarget).update();
                    this.syncTask(visualTarget.getChildren(), notes.getTarget((EvernoteTarget) ((TargetTreeItem) visualTarget).getTarget()));
                } else {
                    //TODO mb Exception
                    listVisualTarget.remove(visualTarget);
                }
            } else {
                logger.error("In tree view found unsupported Target...");
            }
        }
        this.notes = notes;
    }

    private void syncTask(ObservableList<TreeItem> treeView, EvernoteTarget evernoteTarget) {
        int indexTreeView = 0;
        boolean byPassSync = false;
        TreeItem treeItem;
        for (EvernoteTask task : evernoteTarget.getAllTask()) {

            if (indexTreeView >= treeView.size()) {
                treeItem = null;
            } else {
                treeItem = treeView.get(indexTreeView);
            }

            if (!byPassSync && treeItem == null) {
                byPassSync = true;
            }
            if (byPassSync) {
                treeView.add(this.createVisualTaskByTask(task));
            } else {
                if (treeItem instanceof TaskTreeItem && ((TaskTreeItem) treeItem).getTask() instanceof EvernoteTask) {
                    EvernoteTask visualTask = ((EvernoteTask) ((TaskTreeItem) treeItem).getTask());
                    if (visualTask.getUniqueId().equals(task.getUniqueId())) {
                        ((TaskTreeItem) treeItem).update();
                        this.syncSubTask(treeItem.getChildren(), task);
                    } else {
                        treeView.remove(treeItem);
                        TreeItem newTreeItem = this.createVisualTaskByTask(task);
                        treeView.add(indexTreeView, newTreeItem);
                        for (EvernoteSubTask subTask : task.getSubTask()) {
                            newTreeItem.getChildren().add(this.createVisualTaskByTask(subTask));
                        }
                    }
                }
            }
            indexTreeView++;
        }

        List<Integer> listOfRemoves = new ArrayList<>();
        if (treeView.size() != indexTreeView) {
            for (int i = indexTreeView; i < treeView.size(); i++) {
                listOfRemoves.add(i);
            }
        }
        this.removeFromList(treeView, listOfRemoves, 0);
    }


    private void removeFromList(List subject, List<Integer> indexForRemove, Integer numberOfLoop) {
        if (indexForRemove.size() == 0) {
            return;
        }
        int index = indexForRemove.get(0);
        indexForRemove.remove(0);
        if (numberOfLoop == null) {
            numberOfLoop = 0;
        }
        index -= numberOfLoop;
        subject.remove(index);
        this.removeFromList(subject, indexForRemove, numberOfLoop + 1);
    }


    private void syncSubTask(ObservableList<TreeItem> treeView, EvernoteTask evernoteTask) {
        int indexTreeView = 0;
        boolean byPassSync = false;
        TreeItem treeItem;
        for (EvernoteSubTask subTask : evernoteTask.getSubTask()) {

            if (indexTreeView >= treeView.size()) {
                treeItem = null;
            } else {
                treeItem = treeView.get(indexTreeView);
            }

            if (!byPassSync && treeItem == null) {
                byPassSync = true;
            }
            if (byPassSync) {
                treeView.add(this.createVisualTaskByTask(subTask));
            } else {
                if (treeItem instanceof TaskTreeItem && ((TaskTreeItem) treeItem).getTask() instanceof EvernoteSubTask) {
                    EvernoteSubTask visualTask = ((EvernoteSubTask) ((TaskTreeItem) treeItem).getTask());
                    if (visualTask.getUniqueId().equals(subTask.getUniqueId())) {
                        ((TaskTreeItem) treeItem).update();
                    } else {
                        treeView.remove(treeItem);
                        TreeItem newTreeItem = this.createVisualTaskByTask(subTask);
                        treeView.add(indexTreeView, newTreeItem);
                    }
                }
            }
            indexTreeView++;
        }

        List<Integer> listOfRemoves = new ArrayList<>();
        if (treeView.size() != indexTreeView) {
            for (int i = indexTreeView; i < treeView.size(); i++) {
                listOfRemoves.add(i);
            }
        }
        this.removeFromList(treeView, listOfRemoves, 0);



/*        int indexTreeView = 0;
        boolean byPassSync = false;
        for (EvernoteSubTask subTask : evernoteTask.getSubTask()) {
            TreeItem treeItem = treeView.get(indexTreeView);
            if (!byPassSync && treeItem == null) {
                byPassSync = true;
            }
            if (byPassSync) {
                treeView.add(this.createVisualTaskByTask(subTask));
            } else {
                if (treeItem instanceof TaskTreeItem && ((TaskTreeItem) treeItem).getTask() instanceof EvernoteSubTask) {
                    EvernoteSubTask visualTask = ((EvernoteSubTask) ((TaskTreeItem) treeItem).getTask());
                    if (visualTask.getUniqueId().equals(subTask.getUniqueId())) {
                        ((TaskTreeItem) treeItem).update();
                    } else {
                        treeView.remove(treeItem);
                    }
                }
            }
            indexTreeView++;
        }
        List<Integer> listOfRemoves = new ArrayList<>();
        if (treeView.size() != indexTreeView) {
            for (int i = indexTreeView; i < treeView.size(); i++) {
                listOfRemoves.add(i);
            }
        }
        this.removeFromList(treeView, listOfRemoves, 0);*/
    }


    private boolean hasTargetTreeItemInNotes(TargetTreeItem targetTreeItem, EvernoteNotes notes) {
        for (EvernoteTarget target : notes.getAllTarget()) {
            if (targetTreeItem.getTarget() instanceof EvernoteTarget) {
                if (target.getGuid().equals(((EvernoteTarget) targetTreeItem.getTarget()).getGuid())) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void updateNotes(TreeView treeView) {
        if (lastTreeView != null) {
            lastTreeView = treeView;
            this.updateNoteCustomComponents(lastTreeView);
        }
    }

    private void updateNoteCustomComponents(TreeView treeView) {
        this.updateNoteCustomComponents(treeView.getRoot().getChildren());
    }

    private void updateNoteCustomComponents(ObservableList<TreeItem> treeItemObservableList) {
        for (Object object : treeItemObservableList) {
            if (object instanceof TreeItem && object instanceof NoteCustomComponent) {
                ((NoteCustomComponent) object).update();
                if (((TreeItem) object).getChildren() != null) {
                    this.updateNoteCustomComponents(((TreeItem) object).getChildren());
                }
            }
        }
    }

    @Override
    public void setErrorHandler(VisualAdapterErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Initialized tree view
     *
     * @param treeView Tree view
     */
    private void initializeTreeView(TreeView treeView) {
        treeView.setRoot(new TreeItem(new TextField("%ROOT%")));
        treeView.setShowRoot(false);
    }


    private TreeItem fill(EvernoteTarget target) {
        TreeItem targetItem = new TargetTreeItem(target);

        for (EvernoteTask task : target.getAllTask()) {
            targetItem.getChildren().add(this.getVisualTask(task));
        }
        if (target.getAllTask().size() == 0) {
            EvernoteTask task = target.addTask("");
            targetItem.getChildren().add(this.getVisualTask(task));
        }
        return targetItem;
    }


    private TreeItem getVisualTask(EvernoteTask noteTask) {
        TreeItem<TextField> task = this.createVisualTaskByTask(noteTask);

        for (EvernoteSubTask subTaskNote : noteTask.getSubTask()) {
            task.getChildren().add(this.createVisualTaskByTask(subTaskNote));
        }
        return task;
    }


    private void onKeyPressNoteTask(KeyEvent keyEvent, TreeItem<TextField> node, EvernoteTask noteTask) {

        TreeItem<TextField> focusedTextField = null;

        if (KeyCode.TAB == keyEvent.getCode()) {
            //Added new sub Task
            node.setExpanded(true);
            if (!hasEmptyFieldAtPointNode(node)) {
                Task task = noteTask.addSubTask("");
                TreeItem<TextField> treeItem = this.createVisualTaskByTask(task);
                node.getChildren().add(
                        treeItem
                );
                focusedTextField = treeItem;
            } else {
                focusedTextField = this.getTextFieldEmptyAtPointNode(node);
            }
        } else if (KeyCode.ENTER == keyEvent.getCode()) {
            //Added new Task
            if (!hasEmptyFieldAtPointNode(node.getParent())) {
                Task task = noteTask.getTarget().addTask("");
                TreeItem<TextField> treeItem = this.createVisualTaskByTask(task);
                node.getParent().getChildren().add(
                        treeItem
                );
                focusedTextField = treeItem;
            } else {
                focusedTextField = this.getTextFieldEmptyAtPointNode(node.getParent());
            }
        } else {
            logger.debug("TextField status changed");
            noteStatus = NoteStatus.EDITING;
        }

        if (null != focusedTextField) {
            this.setFocusOnTextField(focusedTextField);
        }
        if (keyEvent.getText().length() == 1 && !keyEvent.getText().equals(" ")) {
            noteTask.editNameTask(node.getValue().getText() + keyEvent.getText());
        }

    }

    private void onKeyPressNoteTask(KeyEvent keyEvent, TreeItem<TextField> node, EvernoteSubTask noteSubTask) {
        if (KeyCode.ENTER == keyEvent.getCode()) {
            if (!hasEmptyFieldAtPointNode(node)) {
                Task task = noteSubTask.getTask().addSubTask("");
                TreeItem<TextField> treeItem = this.createVisualTaskByTask(task);
                node.getParent().getChildren().add(treeItem);
                this.setFocusOnTextField(treeItem);
            } else {
                this.setFocusOnTextField(this.getTextFieldEmptyAtPointNode(node));
            }
        } else {
            logger.debug("TextField status changed");
            noteStatus = NoteStatus.EDITING;
        }
        if (keyEvent.getText().length() == 1 && !keyEvent.getText().equals(" ")) {
            noteSubTask.editNameTask(node.getValue().getText() + keyEvent.getText());
        }

    }

    /**
     * Has after treeItem empty textField
     *
     * @param node node in tree view
     * @return {@code true} if exists
     */
    private boolean hasEmptyFieldAtPointNode(TreeItem node) {
        for (Object treeItem : node.getChildren()) {
            if (treeItem instanceof TreeItem && ((TreeItem) treeItem).getValue() instanceof TextField) {
                TextField textField = (TextField) ((TreeItem) treeItem).getValue();
                if (textField.getText().equals("")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return text field with empty content
     *
     * @param node node in tree view
     * @return null if textField not exists, else TextField
     */
    private TreeItem<TextField> getTextFieldEmptyAtPointNode(TreeItem node) {
        for (Object treeItem : node.getChildren()) {
            if (treeItem instanceof TreeItem && ((TreeItem) treeItem).getValue() instanceof TextField) {
                TextField textField = (TextField) ((TreeItem) treeItem).getValue();
                if (textField.getText().equals("")) {
                    return (TreeItem<TextField>) treeItem;
                }
            }
        }
        return null;
    }

    /**
     * Set focus for TreeItem
     *
     * @param treeItem
     */
    private void setFocusOnTextField(TreeItem<TextField> treeItem) {
        new Thread(() -> {
            try {
                Thread.sleep(55);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            treeItem.getValue().requestFocus();
        }).start();
    }

    /**
     * Event on check box action
     *
     * @param treeItem treeItem in TreeView
     * @param task
     */
    private void onCheckBoxAction(TreeItem<TextField> treeItem, Task task) {
        if (treeItem.getGraphic() instanceof NoteCheckBox) {
            NoteCheckBox checkBox = (NoteCheckBox) treeItem.getGraphic();
            boolean isSelected = checkBox.isSelected();
            task.setCompleted(isSelected);
            if (task instanceof NoteTask) {
                for (Object subTask : ((NoteTask) task).getSubTask()) {
                    if (subTask instanceof NoteSubTask) {
                        ((NoteSubTask) subTask).setCompleted(isSelected);
                        //TODO move to NoteTask
                    }
                }
            }
            checkBox.changeSelectedStatus(isSelected);
            this.filSelected(treeItem.getChildren(), isSelected);
        }
        logger.debug("Checkbox status changed");
        noteStatus = NoteStatus.EDITING;
    }

    private void filSelected(ObservableList<TreeItem<TextField>> list, boolean isSelected) {
        for (TreeItem treeItem : list) {
            if (treeItem.getGraphic() instanceof NoteCheckBox) {
                ((NoteCheckBox) treeItem.getGraphic()).changeSelectedStatus(isSelected);
            }
            if (treeItem.getChildren().size() > 0) {
                this.filSelected(treeItem.getChildren(), isSelected);
            }
        }
    }

    /**
     * Enable autoSave timer
     */
    private void enableAutoSave() {
        Thread thread = new Thread(() -> {
            boolean needUpload = false;
            while (true) {
                try {
                    Thread.yield();
                    Thread.sleep(2500);
                    if (noteStatus == NoteStatus.EDITING) {
                        noteStatus = NoteStatus.NONE;
                        needUpload = true;
                    } else if (needUpload) {
                        this.uploadNotes();
                        needUpload = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

            }
        });
        thread.start();
    }

    /**
     * Upload notes on client
     */
    private void uploadNotes() {
        try {
            logger.info("Try upload notes.");
            noteStorage.updateNotes(notes);
            Platform.runLater(() -> update(lastTreeView));
        } catch (Exception e) {
            errorHandler.onError("Upload notes error", e.getMessage());
            logger.error("Error upload notes. ", e);

        }
    }


    /**
     * Get abstract visual task
     *
     * @param task name task
     * @return visualized task
     */
    private TreeItem<TextField> createVisualTaskByTask(Task task) {
        TaskTreeItem treeItem = new TaskTreeItem(task);
        treeItem.setOnActionCheckBox(event -> this.onCheckBoxAction(treeItem, task));

        if (task instanceof EvernoteTask) {
            treeItem.setOnActionTextField(keyEvent -> this.onKeyPressNoteTask(keyEvent, treeItem, (EvernoteTask) task));
        }

        if (task instanceof EvernoteSubTask) {
            treeItem.setOnActionTextField(keyEvent -> this.onKeyPressNoteTask(keyEvent, treeItem, (EvernoteSubTask) task));
        }
        return treeItem;
    }

}
