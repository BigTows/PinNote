package org.bigtows.window.ui.notetree;

import com.intellij.ui.treeStructure.Tree;
import org.bigtows.window.ui.notetree.listener.NoteTreeChangeListener;
import org.bigtows.window.ui.notetree.listener.NoteTreeNeedRefreshModelListener;
import org.bigtows.window.ui.notetree.tree.PinNoteTreeCellEditor;
import org.bigtows.window.ui.notetree.tree.PinNoteTreeCellRender;
import org.bigtows.window.ui.notetree.tree.entity.Note;
import org.bigtows.window.ui.notetree.tree.entity.Task;
import org.bigtows.window.ui.notetree.tree.node.AbstractTaskTreeNode;
import org.bigtows.window.ui.notetree.tree.node.NoteTreeNode;
import org.bigtows.window.ui.notetree.tree.node.TaskTreeNode;
import org.bigtows.window.ui.notetree.utils.ExpandTreeUtils;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NoteTree extends JPanel {

    private final Tree tree;

    /**
     * Collection of listeners about tree changes
     */
    private final List<NoteTreeChangeListener> treeChangeListeners = new ArrayList<>();
    private final List<NoteTreeNeedRefreshModelListener> needUpdateModelListeners = new ArrayList<>();

    public NoteTree(List<MutableTreeNode> data) {
        tree = new Tree(this.buildTreeModelByListTreeNode(data));
        tree.setCellEditor(new PinNoteTreeCellEditor(this::processChangeEvent));
        tree.setCellRenderer(new PinNoteTreeCellRender(this::processChangeEvent));
        tree.setEditable(true);
        tree.setRootVisible(false);
        tree.setSelectionModel(new DefaultTreeSelectionModel());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setDragEnabled(false);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.setTransferHandler(new TreeTransferHandler());
        setLayout(new BorderLayout());
        add(tree, BorderLayout.CENTER);
    }

    /**
     * Call all subscribers about changed tree
     */
    private void processChangeEvent() {
        treeChangeListeners.forEach(NoteTreeChangeListener::treeChanged);
    }

    /**
     * Subscribe for tree change event
     *
     * @param noteTreeChangeListener instance of listener
     */
    public void addTreeChangeListener(NoteTreeChangeListener noteTreeChangeListener) {
        treeChangeListeners.add(noteTreeChangeListener);
    }

    public void addNeedUpdateModelListener(NoteTreeNeedRefreshModelListener noteTreeNeedRefreshModelListener) {
        needUpdateModelListeners.add(noteTreeNeedRefreshModelListener);
    }

    public void updateModel(List<MutableTreeNode> data) {
        List<TreePath> listOfLeaf = this.getExpandedNodeTree();
        SwingUtilities.invokeLater(() -> {
            tree.setModel(this.buildTreeModelByListTreeNode(data));
            ExpandTreeUtils.expandLeaf(tree, listOfLeaf);
            tree.updateUI();
        });
    }

    /**
     * Get collection of expanded node tree
     *
     * @return collection of expanded node tree
     */
    private List<TreePath> getExpandedNodeTree() {
        Enumeration<TreePath> enumeration = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
        List<TreePath> listOfLeaf = new ArrayList<>();
        if (enumeration != null) {
            var iterator = enumeration.asIterator();
            iterator.forEachRemaining(listOfLeaf::add);
        }
        return listOfLeaf;
    }

    /**
     * Build model and fill mutable tree nodes
     *
     * @param mutableTreeNodes data for model
     * @return Tree model
     */
    private TreeModel buildTreeModelByListTreeNode(List<MutableTreeNode> mutableTreeNodes) {
        var root = new DefaultMutableTreeNode("OPA");
        mutableTreeNodes.forEach(mutableTreeNode -> {
            if (mutableTreeNode.getChildCount() == 0) {
                mutableTreeNode.insert(new TaskTreeNode(Task.builder().build()), 0);
            }
            root.add(mutableTreeNode);
        });
        return new DefaultTreeModel(root, false);
    }


    public List<MutableTreeNode> getMutableTreeNodeList() {
        var treeNodeList = new ArrayList<MutableTreeNode>();
        var children = ((DefaultMutableTreeNode) (tree.getModel().getRoot())).children().asIterator();
        children.forEachRemaining(element -> {
            treeNodeList.add((MutableTreeNode) element);
        });
        return treeNodeList;
    }

    public void addNewNote(String name) {
        var treeNode = ((DefaultMutableTreeNode) tree.getModel().getRoot());
        var noteTreeNode = new NoteTreeNode(
                Note.builder()
                        .name(name)
                        .build()
        );
        List<TreePath> listOfLeaf = this.getExpandedNodeTree();
        treeNode.insert(noteTreeNode, 0);

        noteTreeNode.add(new TaskTreeNode(Task.builder()
                .build())
        );
        if (noteTreeNode.getChildCount() == 1) {
            tree.setModel(tree.getModel());
        }
        this.processChangeEvent();
        SwingUtilities.invokeLater(() -> {
            ExpandTreeUtils.expandLeaf(tree, listOfLeaf);
            tree.updateUI();
            var treePath = new TreePath(((AbstractTaskTreeNode) noteTreeNode.getChildAt(0)).getPath());
            tree.expandPath(treePath);
            tree.startEditingAtPath(treePath);
        });
    }

    /**
     * Block the tree for editing
     */
    public void lockTree() {
        SwingUtilities.invokeLater(() -> tree.setEditable(false));
    }

    /**
     * Check tree is locked
     *
     * @return {@code true} if tree locked for editing else {@code false}
     */
    public boolean isLocked() {
        return !tree.isEditable();
    }

    /**
     * Unblock the tree for editing
     */
    public void unlockTree() {
        SwingUtilities.invokeLater(() -> tree.setEditable(true));
    }

    public boolean hasSelectedElement() {
        return tree.getLastSelectedPathComponent() != null;
    }

    public void removeSelectedElement() {
        var selectedPath = tree.getLastSelectedPathComponent();
        if (selectedPath instanceof DefaultMutableTreeNode) {
            var mutableTreeNode = ((DefaultMutableTreeNode) selectedPath);
            var parentMutableTreeNote = mutableTreeNode.getParent();
            mutableTreeNode.removeFromParent();
            if (parentMutableTreeNote instanceof NoteTreeNode && parentMutableTreeNote.getChildCount() == 0) {
                ((NoteTreeNode) parentMutableTreeNote).add(
                        new TaskTreeNode(Task.builder().build())
                );
            }
            this.processChangeEvent();
            tree.updateUI();
        }
    }

    public void needUpdateModel() {
        this.needUpdateModelListeners.forEach(NoteTreeNeedRefreshModelListener::refresh);
    }


    public boolean hasFocusedTask() {
        var path = tree.getEditingPath();
        return path != null && path.getLastPathComponent() instanceof AbstractTaskTreeNode;
    }

    public void removeFocusedTask() {

        var value = ((AbstractTaskTreeNode) tree.getEditingPath().getLastPathComponent());

        var parent = value.getParent();
        int indexValue = parent.getIndex(value);
        value.removeFromParent();
        if (parent instanceof NoteTreeNode && parent.getChildCount() == 0) {
            ((NoteTreeNode) parent).add(new TaskTreeNode(Task.builder().build()));
        }
        tree.updateUI();
        processChangeEvent();


        //Set cursor at nearby position
        TreePath cursorPath;
        if (parent instanceof TaskTreeNode && parent.getChildCount() == 0) {
            cursorPath = new TreePath(((TaskTreeNode) parent).getPath());
        } else {
            cursorPath = new TreePath(((AbstractTaskTreeNode) parent.getChildAt(
                    parent.getChildCount() - 1 < indexValue ? indexValue - 1 : indexValue
            )).getPath());
        }

        SwingUtilities.invokeLater(() -> tree.startEditingAtPath(cursorPath));
    }

    /**
     * Enable or disable drag and drop mode
     *
     * @param isEnable status of mode
     */
    public void setDragEnable(boolean isEnable) {
        tree.setDragEnabled(isEnable);
    }
}
