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
 * Action for NoteTree, create note
 *
 * @see NoteTree
 */
final public class AddNote extends AnAction implements DumbAware {

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
        super("Create new note", "", IconUtil.getAddIcon());
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void actionPerformed(@Nullable AnActionEvent e) {
        var selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent instanceof JScrollPane) {
            selectedComponent = ((JScrollPane) selectedComponent).getViewport().getView();
        }
        if (selectedComponent instanceof NoteTree) {
            var noteTree = (NoteTree) selectedComponent;
            var nameTab = ((JLabel) tabbedPane.getTabComponentAt(tabbedPane.getSelectedIndex())).getText();
            String nameOfTarget;
            do {
                nameOfTarget = JOptionPane.showInputDialog("<html>Create new Target for " + nameTab + "<br>Enter name of target");
            } while (nameOfTarget != null && (nameOfTarget.length() == 0 || nameOfTarget.length() > 100));
            if (nameOfTarget != null) {
                noteTree.addNewNote(nameOfTarget);
            }
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