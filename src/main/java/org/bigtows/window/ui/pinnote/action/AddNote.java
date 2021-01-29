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
     * Id of action
     */
    public final static String ACTION_ID = AddNote.class.getName();

    /**
     * Instance of tabbed pane
     */
    private JTabbedPane tabbedPane;

    /**
     * Constructor
     */
    public AddNote() {
        super("Create new note", "", IconUtil.getAddIcon());
    }

    /**
     * Initialization tabbed pane for action
     *
     * @param tabbedPane instance of target tabbed pane
     */
    public void initializeTabbedPane(JTabbedPane tabbedPane) {
        if (this.tabbedPane != null) {
            throw new RuntimeException("Tabbed pane already exists");
        }
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void actionPerformed(@Nullable AnActionEvent e) {
        if (tabbedPane == null) {
            return;
        }
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
                if (nameOfTarget != null) {
                    nameOfTarget = nameOfTarget.trim();
                }
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