package org.bigtows.window.ui.pinnote;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import org.bigtows.service.note.notebook.Notebook;
import org.bigtows.window.ui.border.BottomBorder;
import org.bigtows.window.ui.notetree.NoteTree;
import org.bigtows.window.ui.pinnote.action.AddNote;
import org.bigtows.window.ui.pinnote.action.ForceRefreshNoteAction;

import javax.swing.*;
import java.awt.*;

public class PinNoteComponent {
    private JPanel root;
    private JTabbedPane notebookTabbedPane;
    private JPanel toolBarPanel;


    public JPanel getRoot() {
        return root;
    }

    private void createUIComponents() {
        notebookTabbedPane = new JBTabbedPane();
        notebookTabbedPane.removeAll();
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(new AddNote(notebookTabbedPane));
        //group.add(new ForceRefreshNoteAction(notebookTabbedPane));
        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("PinNoteToolbar", group, true);
        var panel = JBUI.Panels.simplePanel(actionToolBar.getComponent());
        panel.setBorder(new BottomBorder(JBUI.CurrentTheme.ToolWindow.borderColor()));
        toolBarPanel.add(panel);
    }

    public void addNotebook(Notebook<?> notebook, Icon icon, NoteTree noteTree) {
        notebookTabbedPane.addTab(notebook.getName(), icon, noteTree);
    }
}

