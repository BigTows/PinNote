package org.bigtows.window.ui.notetree.tree.transfer;

import org.bigtows.window.ui.notetree.tree.entity.Task;
import org.bigtows.window.ui.notetree.tree.event.TreeChanged;
import org.bigtows.window.ui.notetree.tree.node.AbstractTaskTreeNode;
import org.bigtows.window.ui.notetree.tree.node.NoteTreeNode;
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
import java.io.IOException;

/**
 * Handler for transfer object between nodes
 *
 * @author Craig Wood (code bassed)
 * @link https://coderanch.com/t/346509/java/JTree-drag-drop-tree-Java
 */
public class TreeTransferHandler extends TransferHandler {

    /**
     * Mime type for transfer data
     */
    private static final String mimeTypeDefaultMutableTreeNode = DataFlavor.javaJVMLocalObjectMimeType +
            ";class=\"" +
            DefaultMutableTreeNode[].class.getName() +
            "\"";

    /**
     * Meta data about nodes
     */
    private final DataFlavor nodesFlavor;

    /**
     * Callback when tree change
     */
    private final TreeChanged callback;

    /**
     * Buffered node for remove
     */
    private DefaultMutableTreeNode nodeToRemove;

    /**
     * Constructor
     *
     * @param callback event of tree change
     */
    public TreeTransferHandler(TreeChanged callback) {
        this.callback = callback;
        try {
            nodesFlavor = new DataFlavor(mimeTypeDefaultMutableTreeNode);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("ClassNotFound: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop() || support.getDropAction() != MOVE) {
            return false;
        }
        support.setShowDropLocation(true);
        if (!support.isDataFlavorSupported(nodesFlavor)) {
            return false;
        }
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        Object dropTargetObject = dl.getPath().getLastPathComponent();

        return dropTargetObject instanceof AbstractTaskTreeNode || dropTargetObject instanceof NoteTreeNode;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null && paths.length == 1 && paths[0].getLastPathComponent() instanceof AbstractTaskTreeNode) {
            AbstractTaskTreeNode node = (AbstractTaskTreeNode) paths[0].getLastPathComponent();
            this.nodeToRemove = node;
            return new PinNoteNodesTransferable(this.nodesFlavor, new AbstractTaskTreeNode[]{node.clone()});
        }
        return null;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if ((action & MOVE) == MOVE) {
            JTree tree = (JTree) source;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.removeNodeFromParent(nodeToRemove);

            this.fillEmptyTaskAtEmptyTarget(tree);
            this.debugModel(model);
            tree.updateUI();
            callback.onChange();
        }
    }

    /**
     * Adding empty task for each empty target
     *
     * @param tree source
     */
    private void fillEmptyTaskAtEmptyTarget(JTree tree) {
        var root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        var hasNewTask = false;
        for (int i = 0; i < root.getChildCount(); i++) {
            var note = (DefaultMutableTreeNode) root.getChildAt(i);
            if (note.getChildCount() == 0) {
                hasNewTask = true;
                note.add(new TaskTreeNode(Task.builder().build()));
            }
        }

        if (hasNewTask) {
            tree.updateUI();
        }

    }


    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        var nodes = this.getTransferData(support);


        JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
        int childIndex = dropLocation.getChildIndex();
        TreePath dest = dropLocation.getPath();

        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dest.getLastPathComponent();
        if (parent instanceof TaskTreeNode) {
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i] instanceof TaskTreeNode && nodes[i].getChildCount() > 0) {
                    return false;
                } else if (!(nodes[i] instanceof SubTaskTreeNode)) {
                    nodes[i] = new SubTaskTreeNode(nodes[i].getUserObject());
                }
            }
        } else if (parent instanceof NoteTreeNode) {
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i] instanceof SubTaskTreeNode) {
                    nodes[i] = new TaskTreeNode(nodes[i].getUserObject());
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


        for (AbstractTaskTreeNode node : nodes) {
            model.insertNodeInto(node, parent, index++);
        }

        return true;
    }

    /**
     * Get data from transfer object
     *
     * @param transferSupport transfer object
     * @return data
     */
    @NotNull
    private AbstractTaskTreeNode[] getTransferData(TransferHandler.TransferSupport transferSupport) {
        AbstractTaskTreeNode[] nodes;
        try {
            Transferable t = transferSupport.getTransferable();
            nodes = (AbstractTaskTreeNode[]) t.getTransferData(nodesFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            throw new RuntimeException("Can't extract data from transfer", e);
        }
        return nodes;
    }

    /**
     * Debugger for model  TODO Remove
     *
     * @param model model
     */
    private void debugModel(DefaultTreeModel model) {
        System.out.println("----");
        var root = (DefaultMutableTreeNode) model.getRoot();

        for (int i = 0; i < root.getChildCount(); i++) {
            var note = (DefaultMutableTreeNode) root.getChildAt(i);
            System.out.println(note.toString());
            for (int j = 0; j < note.getChildCount(); j++) {
                var task = note.getChildAt(j);
                System.out.println("    " + task.getClass().getSimpleName() + ":" + task.toString());
                for (int k = 0; k < task.getChildCount(); k++) {
                    var subTask = task.getChildAt(k);
                    System.out.println("        " + subTask.getClass().getSimpleName() + ":" + subTask.toString());
                }
            }
        }
    }

}
