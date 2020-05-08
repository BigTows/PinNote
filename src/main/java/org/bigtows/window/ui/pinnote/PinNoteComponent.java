package org.bigtows.window.ui.pinnote;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.bigtows.service.note.notebook.Notebook;
import org.bigtows.window.ui.border.BottomBorder;
import org.bigtows.window.ui.notetree.NoteTree;
import org.bigtows.window.ui.pinnote.action.AddNote;
import org.bigtows.window.ui.pinnote.action.ForceRefreshNoteAction;
import org.bigtows.window.ui.pinnote.action.OpenSettings;
import org.bigtows.window.ui.pinnote.action.RemoveNote;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PinNoteComponent {
    private JPanel root;
    private JTabbedPane notebookTabbedPane;
    private JPanel toolBarPanel;
    private JButton setupStorage;


    public JPanel getRoot() {
        return root;
    }

    private void createUIComponents() {
        notebookTabbedPane = new JBTabbedPane();
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        toolBarPanel.add(this.createActionToolBar());
        setupStorage = new JButton("Open settings for setup.");
        setupStorage.addActionListener(this::openSettings);
    }

    private void openSettings(ActionEvent actionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(null, "PinNote");
    }

    private BorderLayoutPanel createActionToolBar() {
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(new AddNote(notebookTabbedPane));
        group.add(new RemoveNote(notebookTabbedPane));
        group.add(new OpenSettings());
        group.addSeparator();
        group.add(new ForceRefreshNoteAction(notebookTabbedPane));
        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("PinNoteToolbar", group, true);
        var panel = JBUI.Panels.simplePanel(actionToolBar.getComponent());
        panel.setBorder(new BottomBorder(JBUI.CurrentTheme.ToolWindow.borderColor()));
        return panel;
    }

    public void addNotebook(Notebook<?> notebook, Icon icon, NoteTree noteTree) {
        notebookTabbedPane.addTab(
                notebook.getName(),
                icon,
                this.createScrollPaneWithNoteTree(noteTree)
        );
        root.remove(setupStorage);
    }

    public void removeAllNotebook(){
        notebookTabbedPane.removeAll();
    }

    /**
     * Creating wrapper via Scroll pane for NoteTree
     *
     * @param noteTree instance of NoteTree
     * @return Scroll pane with NoteTree
     */
    private JScrollPane createScrollPaneWithNoteTree(NoteTree noteTree) {
        var scroll = new JBScrollPane(noteTree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }
}

