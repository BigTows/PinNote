package org.bigtows.window.ui.notetree.tree.component;

import com.intellij.icons.AllIcons;
import org.bigtows.window.ui.notetree.tree.node.NoteTreeNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TargetPanel extends JPanel {

    private final NoteTreeNode noteTreeNode;

    public TargetPanel(NoteTreeNode value) {
        this.noteTreeNode = value;

        setLayout(new BorderLayout());
        add(new JLabel(this.noteTreeNode.getUserObject().getName()), BorderLayout.WEST);

        var deleteLabel = new JLabel(AllIcons.Actions.DeleteTag);
      //  add(deleteLabel, BorderLayout.EAST);
        var self = this;
        deleteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                var s = self;
                return;
            }
        });
    }


}
