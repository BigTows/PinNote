package org.bigtows.window.ui.pinnote.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.IconUtil;
import org.bigtows.window.ui.notetree.NoteTree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Action for NoteTree, create note
 *
 * @see NoteTree
 */
public class AddNote extends AnAction {

    /**
     * Instance of tabbed pane
     */
    private final JTabbedPane tabbedPane;

    /**
     * Constructor
     *
     * @param tabbedPane instance of tabbed pane
     */
    public AddNote(@NotNull JTabbedPane tabbedPane) {
        super(IconUtil.getAddIcon());
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent instanceof JScrollPane) {
            selectedComponent = ((JScrollPane) selectedComponent).getViewport().getView();
        }
        if (selectedComponent instanceof NoteTree) {
            var noteTree = (NoteTree) selectedComponent;
            var nameTab = ((JLabel) tabbedPane.getTabComponentAt(tabbedPane.getSelectedIndex())).getText();
            noteTree.addNewNote(JOptionPane.showInputDialog("<html>Create new Target for " + nameTab + "<br>Enter name of target"));
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent instanceof JScrollPane) {
            selectedComponent = ((JScrollPane) selectedComponent).getViewport().getView();
        }
        e.getPresentation().setEnabled(selectedComponent instanceof NoteTree);
    }
}