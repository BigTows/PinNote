package org.bigtows.window.ui.pinnote.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.util.IconUtil;
import org.bigtows.window.ui.notetree.NoteTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * UI - component-action for remove note from NoteTree
 *
 * @see NoteTree
 */
final public class RemoveNote extends AnAction implements DumbAware {


    /**
     * Id of action
     */
    public final static String ACTION_ID = RemoveNote.class.getName();

    private JTabbedPane tabbedPane;

    /**
     * Constructor
     */
    public RemoveNote() {
        super("Remove selected note or task", "", IconUtil.getRemoveIcon());
    }

    public void initializeTabbedPane(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var noteTree = this.getNoteTreeFromTabbedPane();
        if (noteTree != null) {
            if (noteTree.hasFocusedTask()){
                noteTree.removeFocusedTask();
            }
            if (noteTree.hasSelectedElement()){
                noteTree.removeSelectedElement();
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var noteTree = this.getNoteTreeFromTabbedPane();
        e.getPresentation().setEnabled(noteTree != null && (noteTree.hasSelectedElement() || noteTree.hasFocusedTask()));
    }

    /**
     * Try get NoteTree component from tabbed pane
     *
     * @return noteTree component or null
     */
    @Nullable
    private NoteTree getNoteTreeFromTabbedPane() {
        if (tabbedPane == null) {
            return null;
        }
        var selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent instanceof JScrollPane) {
            selectedComponent = ((JScrollPane) selectedComponent).getViewport().getView();
        }
        if (selectedComponent instanceof NoteTree) {
            return (NoteTree) selectedComponent;
        }

        return null;
    }
}
