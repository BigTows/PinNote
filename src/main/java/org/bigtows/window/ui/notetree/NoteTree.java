package org.bigtows.window.ui.notetree;

import com.intellij.ui.treeStructure.Tree;
import org.bigtows.window.ui.notetree.listener.NoteTreeChangeListener;
import org.bigtows.window.ui.notetree.listener.NoteTreeNeedRefreshModelListener;
import org.bigtows.window.ui.notetree.tree.editor.PinNoteTreeCellEditor;
import org.bigtows.window.ui.notetree.tree.entity.Note;
import org.bigtows.window.ui.notetree.tree.entity.Task;
import org.bigtows.window.ui.notetree.tree.node.NoteTreeNode;
import org.bigtows.window.ui.notetree.tree.node.TaskTreeNode;
import org.bigtows.window.ui.notetree.tree.render.PinNoteTreeCellRender;
import org.bigtows.window.ui.notetree.utils.ExpandTreeUtils;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.ArrayList;
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
        tree.setCellRenderer(new PinNoteTreeCellRender(this::processChangeEvent));
        tree.setCellEditor(new PinNoteTreeCellEditor(this::processChangeEvent));
        tree.setEditable(true);
        tree.setRootVisible(false);
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
        var iterator = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot())).asIterator();
        List<TreePath> listOfLeaf = new ArrayList<>();

        iterator.forEachRemaining(listOfLeaf::add);
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
        mutableTreeNodes.forEach(root::add);
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
        treeNode.add(noteTreeNode);

        noteTreeNode.add(new TaskTreeNode(Task.builder()
                .build())
        );
        if (noteTreeNode.getChildCount() == 1) {
            tree.setModel(tree.getModel());
        }
        tree.updateUI();
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

}
