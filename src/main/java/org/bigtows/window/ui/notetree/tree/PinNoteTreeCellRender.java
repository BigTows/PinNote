package org.bigtows.window.ui.notetree.tree;

import org.bigtows.window.ui.notetree.tree.event.TreeChanged;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class PinNoteTreeCellRender extends DefaultTreeCellRenderer {
    private final TreeChanged treeChanged;

    public PinNoteTreeCellRender(TreeChanged treeChanged) {
        this.treeChanged = treeChanged;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        return tree.getCellEditor().getTreeCellEditorComponent(tree, value, selected, expanded, leaf, row);
    }
}
