package org.bigtows.window.ui.pinnote;

import com.intellij.ui.components.JBTabbedPane;
import org.bigtows.service.note.notebook.Notebook;
import org.bigtows.window.ui.notetree.NoteTree;

import javax.swing.*;

public class PinNoteComponent {
    private JPanel root;
    private JTabbedPane notebookTabbedPane;
    private JButton newTargetButton;


    public JPanel getRoot() {
        return root;
    }

    private void createUIComponents() {
        notebookTabbedPane = new JBTabbedPane();
        notebookTabbedPane.removeAll();
        newTargetButton = new JButton("New target");
        newTargetButton.addActionListener(e -> {
            //on click
            var noteTree = (NoteTree) notebookTabbedPane.getSelectedComponent();
            noteTree.addNewNote(JOptionPane.showInputDialog("Press name of list"));
        });
    }

    public void addNotebook(Notebook<?> notebook, NoteTree noteTree) {
        notebookTabbedPane.add(notebook.getName(), noteTree);
    }
}
