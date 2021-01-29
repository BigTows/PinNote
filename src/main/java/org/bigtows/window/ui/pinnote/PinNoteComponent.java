package org.bigtows.window.ui.pinnote;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.Constraints;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.bigtows.notebook.Notebook;
import org.bigtows.window.ui.border.BottomBorder;
import org.bigtows.window.ui.notetree.NoteTree;
import org.bigtows.window.ui.pinnote.action.AddNote;
import org.bigtows.window.ui.pinnote.action.ForceRefreshNoteAction;
import org.bigtows.window.ui.pinnote.action.OpenSettings;
import org.bigtows.window.ui.pinnote.action.RemoveNote;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Swing component for render notebook
 *
 * @see Notebook
 */
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
        setupStorage = new JButton("Welcome, set source, for start using.");
        setupStorage.setIcon(AllIcons.General.Settings);
        setupStorage.addActionListener(this::openSettings);
    }

    private void openSettings(ActionEvent actionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(null, "PinNote");
    }

    private BorderLayoutPanel createActionToolBar() {
        final DefaultActionGroup group = new DefaultActionGroup();
        var addAction = (AddNote) ActionManager.getInstance().getAction(AddNote.ACTION_ID);
        addAction.initializeTabbedPane(notebookTabbedPane);
        group.add(addAction);

        var removeAction = (RemoveNote) ActionManager.getInstance().getAction(RemoveNote.ACTION_ID);
        removeAction.initializeTabbedPane(notebookTabbedPane);
        group.add(removeAction);
        group.addSeparator();
        group.add(new ForceRefreshNoteAction(notebookTabbedPane));
        group.addSeparator();
        group.add(new OpenSettings(), Constraints.LAST);
        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("PinNoteToolbar", group, true);
        var panel = JBUI.Panels.simplePanel(actionToolBar.getComponent());
        panel.setBorder(new BottomBorder(JBUI.CurrentTheme.ToolWindow.borderColor()));
        return panel;
    }

    /**
     * Add new notebook
     *
     * @param name     name for Tab
     * @param icon     icon for Tab
     * @param noteTree source data (notes)
     */
    public void addNotebook(String name, Icon icon, NoteTree noteTree) {
        notebookTabbedPane.addTab(
                name,
                icon,
                this.createScrollPaneWithNoteTree(noteTree)
        );
        setupStorage.setVisible(false);
    }

    /**
     * Remove all tabs/notebook
     */
    public void removeAllNotebook() {
        notebookTabbedPane.removeAll();
        setupStorage.setVisible(true);
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

