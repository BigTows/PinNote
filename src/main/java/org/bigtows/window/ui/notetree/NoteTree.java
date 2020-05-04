package org.bigtows.window.ui.notetree;

import com.intellij.ui.treeStructure.Tree;
import org.bigtows.window.ui.notetree.event.OnNoteTreeChange;
import org.bigtows.window.ui.notetree.tree.editor.PinNoteTreeCellEditor;
import org.bigtows.window.ui.notetree.tree.entity.Note;
import org.bigtows.window.ui.notetree.tree.entity.Task;
import org.bigtows.window.ui.notetree.tree.node.NoteTreeNode;
import org.bigtows.window.ui.notetree.tree.node.TaskTreeNode;
import org.bigtows.window.ui.notetree.tree.render.PinNoteTreeCellRender;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NoteTree extends JPanel {

    private final Tree tree;

    private List<OnNoteTreeChange> events = new ArrayList<>();

    public NoteTree(List<MutableTreeNode> data) {
        tree = new Tree(this.buildTreeModelByListTreeNode(data));
        tree.setCellRenderer(new PinNoteTreeCellRender(this::processChangeEvent));
        tree.setCellEditor(new PinNoteTreeCellEditor());
        tree.setEditable(true);
        tree.setRootVisible(false);
        setLayout(new BorderLayout());
        add(tree, BorderLayout.CENTER);
    }

    public void registerEvent(OnNoteTreeChange onNoteTreeChange) {
        events.add(onNoteTreeChange);
    }

    public void updateModel(List<MutableTreeNode> data) {

        var iterator = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot())).asIterator();
        List<TreePath> context = new ArrayList<>();

        iterator.forEachRemaining(context::add);
        SwingUtilities.invokeLater(() -> {
            tree.setModel(this.buildTreeModelByListTreeNode(data));
            expandTree(tree, context);
        });
    }

    private void expandTree(JTree tree, List<TreePath> context) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root), context);
    }

    private void expandAll(JTree tree, TreePath path, List<TreePath> context) {
        TreeNode node = (TreeNode) path.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            Enumeration enumeration = node.children();
            while (enumeration.hasMoreElements()) {
                TreeNode n = (TreeNode) enumeration.nextElement();
                TreePath p = path.pathByAddingChild(n);

                expandAll(tree, p, context);
            }
        }

        if (this.findContext(context, path)) {
            tree.expandPath(path);
        }
    }

    private boolean findContext(List<TreePath> context, TreePath currentPath) {
        var lastElementPath = currentPath.getLastPathComponent();
        for (TreePath treePath : context) {
            var treePathLast = treePath.getLastPathComponent();

            if (lastElementPath instanceof NoteTreeNode && treePathLast instanceof NoteTreeNode) {
                if (((NoteTreeNode) lastElementPath).getUserObject().getIdentity().equals(
                        ((NoteTreeNode) treePathLast).getUserObject().getIdentity())) {
                    return true;
                }
            } else if (lastElementPath instanceof TaskTreeNode && treePathLast instanceof TaskTreeNode) {
                if (((TaskTreeNode) lastElementPath).getUserObject().getIdentity().equals(
                        ((TaskTreeNode) treePathLast).getUserObject().getIdentity())) {
                    return true;
                }
            }
        }
        return false;
    }


    private TreeModel buildTreeModelByListTreeNode(List<MutableTreeNode> mutableTreeNodes) {
        var root = new DefaultMutableTreeNode("OPA");
        mutableTreeNodes.forEach(root::add);
        return new DefaultTreeModel(root, false);
    }

    private void processChangeEvent() {
        events.forEach(OnNoteTreeChange::event);
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
        tree.updateUI();
    }

    public void lockTree() {
        SwingUtilities.invokeLater(() -> tree.setEditable(false));
    }

    public void unlockTree() {
        SwingUtilities.invokeLater(() -> tree.setEditable(true));
    }

}
