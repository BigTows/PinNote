package org.bigtows.window.ui.pinnote.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.bigtows.window.ui.notetree.NoteTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ForceRefreshNoteAction extends AnAction {


    private final JTabbedPane tabbedPane;

    public ForceRefreshNoteAction(JTabbedPane notebookTabbedPane) {
        super(AllIcons.Actions.Refresh);
        this.tabbedPane = notebookTabbedPane;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var noteTree = this.tryGetCurrentNoteTreeFromTabbedPane(this.tabbedPane);
        e.getPresentation().setEnabled(noteTree != null && !noteTree.isLocked());
    }

    @Nullable
    private NoteTree tryGetCurrentNoteTreeFromTabbedPane(JTabbedPane tabbedPane) {
        var selected = tabbedPane.getSelectedComponent();
        if (selected instanceof NoteTree) {
            return (NoteTree) selected;
        }
        return null;
    }
}
