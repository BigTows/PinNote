package org.bigtows.window.ui.notetree.tree.render;

import org.bigtows.window.ui.notetree.tree.component.TargetPanel;
import org.bigtows.window.ui.notetree.tree.event.TreeChanged;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PinNoteTreeCellRender extends DefaultTreeCellRenderer {
    private final TreeChanged treeChanged;

    public PinNoteTreeCellRender(TreeChanged treeChanged) {
        this.treeChanged = treeChanged;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        var component =  tree.getCellEditor().getTreeCellEditorComponent(tree, value, selected, expanded, leaf, row);

        if (component instanceof TargetPanel){
            component.setBackground(Color.CYAN);
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                }
            });
        }

        return component;
    }
}
