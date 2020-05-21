package org.bigtows.window.ui.pinnote.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.bigtows.window.ui.notetree.NoteTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

final public class ForceRefreshNoteAction extends AnAction {


    private final JTabbedPane tabbedPane;

    public ForceRefreshNoteAction(JTabbedPane notebookTabbedPane) {
        super("Force update notes","",AllIcons.Actions.Refresh);
        this.tabbedPane = notebookTabbedPane;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var noteTree = this.tryGetCurrentNoteTreeFromTabbedPane(this.tabbedPane);
        assert noteTree != null;
        e.getPresentation().setEnabled(false);
        noteTree.needUpdateModel();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var noteTree = this.tryGetCurrentNoteTreeFromTabbedPane(this.tabbedPane);
        e.getPresentation().setEnabled(noteTree != null && !noteTree.isLocked());
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
