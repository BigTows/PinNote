package org.bigtows.window.ui.pinnote.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.IconUtil;
import org.bigtows.window.ui.notetree.NoteTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RemoveNote extends AnAction {

    private final JTabbedPane tabbedPane;

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
