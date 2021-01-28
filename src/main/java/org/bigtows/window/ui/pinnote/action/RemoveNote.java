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
final public class RemoveNote extends AnAction  implements DumbAware {

    private final JTabbedPane tabbedPane;

    /**
     * Constructor
     *
     * @param tabbedPane pane
     */
    public RemoveNote(JTabbedPane tabbedPane) {
        super("Remove selected note or task", "", IconUtil.getRemoveIcon());
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var noteTree = this.getNoteTreeFromTabbedPane();
        if (noteTree != null) {
            noteTree.removeSelectedElement();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var noteTree = this.getNoteTreeFromTabbedPane();
        e.getPresentation().setEnabled(noteTree != null && noteTree.hasSelectedElement());
    }

    /**
     * Try get NoteTree component from tabbed pane
     *
     * @return noteTree component or null
     */
    @Nullable
    private NoteTree getNoteTreeFromTabbedPane() {
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
