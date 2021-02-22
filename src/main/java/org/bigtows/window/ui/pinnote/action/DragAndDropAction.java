package org.bigtows.window.ui.pinnote.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Toggleable;
import com.intellij.openapi.project.DumbAware;
import org.bigtows.window.ui.notetree.NoteTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Action for switch drag and drop mode
 */
public final class DragAndDropAction extends AnAction implements Toggleable, DumbAware {

    /**
     * Tabbed pane
     */
    private final JTabbedPane notebookTabbedPane;

    /**
     * Current status
     */
    private boolean isEnable = false;

    public DragAndDropAction(JTabbedPane notebookTabbedPane) {
        super("Enable drag and drop", "", AllIcons.Ide.UpDown);
        this.notebookTabbedPane = notebookTabbedPane;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        isEnable = !isEnable;
        var noteTree = this.tryGetCurrentNoteTreeFromTabbedPane(notebookTabbedPane);
        if (noteTree != null) {
            noteTree.setDragEnable(isEnable);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (isEnable) {
            e.getPresentation().setText("Disable drag and drop");
        } else {
            e.getPresentation().setText("Enable drag and drop");
        }
        Toggleable.setSelected(e.getPresentation(), isEnable);
    }


    @Nullable
    private NoteTree tryGetCurrentNoteTreeFromTabbedPane(JTabbedPane tabbedPane) {
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
