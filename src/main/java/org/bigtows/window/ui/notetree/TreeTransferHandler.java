package org.bigtows.window.ui.notetree;

import org.bigtows.window.ui.notetree.tree.node.AbstractTaskTreeNode;
import org.bigtows.window.ui.notetree.tree.node.SubTaskTreeNode;
import org.bigtows.window.ui.notetree.tree.node.TaskTreeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler for transfer object between nodes
 *
 * @author Craig Wood (code bassed)
 * @link https://coderanch.com/t/346509/java/JTree-drag-drop-tree-Java
 */
public class TreeTransferHandler extends TransferHandler {

    private static final String mimeTypeDefaultMutableTreeNode = DataFlavor.javaJVMLocalObjectMimeType +
            ";class=\"" +
            DefaultMutableTreeNode[].class.getName() +
            "\"";

    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode[] nodesToRemove;

    public TreeTransferHandler() {
        try {
            nodesFlavor = new DataFlavor(mimeTypeDefaultMutableTreeNode);
            flavors[0] = nodesFlavor;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("ClassNotFound: " + e.getMessage(), e);
        }
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop() || support.getDropAction() != MOVE) {
            return false;
        }
        support.setShowDropLocation(true);
        if (!support.isDataFlavorSupported(nodesFlavor)) {
            return false;
        }
        // Do not allow a drop on the drag source selections.
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        JTree tree = (JTree) support.getComponent();
        int dropRow = tree.getRowForPath(dl.getPath());
        int[] selRows = tree.getSelectionRows();
        /*for (int i = 0; i < selRows.length; i++) {
            if (selRows[i] == dropRow) {
                return false;
            }
        }*/
        // Do not allow MOVE-action drops if a non-leaf node is
        // selected unless all of its children are also selected.
        /*int action = support.getDropAction();
        if (action == MOVE) {
            return haveCompleteNode(tree);
        }*/
        // Do not allow a non-leaf node to be copied to a level
        // which is less than its source level.
       /* TreePath dest = dl.getPath();
        DefaultMutableTreeNode target =
                (DefaultMutableTreeNode) dest.getLastPathComponent();
        TreePath path = tree.getPathForRow(selRows[0]);
        DefaultMutableTreeNode firstNode =
                (DefaultMutableTreeNode) path.getLastPathComponent();
        if (firstNode.getChildCount() > 0 &&
                target.getLevel() < firstNode.getLevel()) {
            return false;
        }*/

        return true;
    }

    private boolean haveCompleteNode(JTree tree) {
        int[] selRows = tree.getSelectionRows();
        TreePath path = tree.getPathForRow(selRows[0]);
        DefaultMutableTreeNode first =
                (DefaultMutableTreeNode) path.getLastPathComponent();
        int childCount = first.getChildCount();
        // first has children and no children are selected.
        if (childCount > 0 && selRows.length == 1)
            return false;
        // first may have children.
        for (int i = 1; i < selRows.length; i++) {
            path = tree.getPathForRow(selRows[i]);
            DefaultMutableTreeNode next =
                    (DefaultMutableTreeNode) path.getLastPathComponent();
            if (first.isNodeChild(next)) {
                // Found a child of first.
                if (childCount > selRows.length - 1) {
                    // Not all children of first are selected.
                    return false;
                }
            }
        }
        return true;
    }

    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null && paths.length == 1 && paths[0].getLastPathComponent() instanceof AbstractTaskTreeNode) {
            // Make up a node array of copies for transfer and
            // another for/of the nodes that will be removed in
            // exportDone after a successful drop.
            List<AbstractTaskTreeNode> copies = new ArrayList<>();
            List<AbstractTaskTreeNode> toRemove = new ArrayList<>();
            AbstractTaskTreeNode node = (AbstractTaskTreeNode) paths[0].getLastPathComponent();
            AbstractTaskTreeNode copy = copy(node);
            copies.add(copy);
            toRemove.add(node);
            AbstractTaskTreeNode[] nodes = copies.toArray(new AbstractTaskTreeNode[0]);
            nodesToRemove = toRemove.toArray(new AbstractTaskTreeNode[0]);
            return new NodesTransferable(nodes);
        }
        return null;
    }

    /**
     * Defensive copy used in createTransferable.
     */
    private AbstractTaskTreeNode copy(AbstractTaskTreeNode node) {
        return (AbstractTaskTreeNode) node.clone();
    }

    protected void exportDone(JComponent source, Transferable data, int action) {
        if ((action & MOVE) == MOVE) {
            JTree tree = (JTree) source;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            // Remove nodes saved in nodesToRemove in createTransferable.
            for (int i = 0; i < nodesToRemove.length; i++) {
                model.removeNodeFromParent(nodesToRemove[i]);
            }
        }
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        // Extract transfer data.
        AbstractTaskTreeNode[] nodes = null;
        try {
            Transferable t = support.getTransferable();
            nodes = (AbstractTaskTreeNode[]) t.getTransferData(nodesFlavor);
        } catch (UnsupportedFlavorException ufe) {
            System.out.println("UnsupportedFlavor: " + ufe.getMessage());
        } catch (java.io.IOException ioe) {
            System.out.println("I/O error: " + ioe.getMessage());
        }
        // Get drop location info.
        JTree.DropLocation dl =
                (JTree.DropLocation) support.getDropLocation();
        int childIndex = dl.getChildIndex();
        TreePath dest = dl.getPath();
        DefaultMutableTreeNode parent =
                (DefaultMutableTreeNode) dest.getLastPathComponent();
        if (parent instanceof TaskTreeNode) {
            for (int i = 0; i < nodes.length; i++) {
                if (!(nodes[0] instanceof SubTaskTreeNode)) {
                    nodes[0] = new SubTaskTreeNode(nodes[0].getUserObject());
                }
            }
        }
        JTree tree = (JTree) support.getComponent();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        // Configure for drop mode.
        int index = childIndex;    // DropMode.INSERT
        if (childIndex == -1) {     // DropMode.ON
            index = parent.getChildCount();
        }
        // Add data to model.
        for (int i = 0; i < nodes.length; i++) {
            model.insertNodeInto(nodes[i], parent, index++);
        }
        return true;
    }

    public class NodesTransferable implements Transferable {
        private final AbstractTaskTreeNode[] nodes;

        public NodesTransferable(AbstractTaskTreeNode[] nodes) {
            this.nodes = nodes;
        }

        @NotNull
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);
            return nodes;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }
}
