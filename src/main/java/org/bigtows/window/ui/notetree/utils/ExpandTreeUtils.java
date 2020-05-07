package org.bigtows.window.ui.notetree.utils;

import org.bigtows.window.ui.notetree.tree.node.NoteTreeNode;
import org.bigtows.window.ui.notetree.tree.node.TaskTreeNode;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.List;

/**
 * Utils method's for expand elements of JTree
 *
 * @see JTree
 */
public final class ExpandTreeUtils {

    /**
     * Expand selected leaf
     *
     * @param tree       source
     * @param listOfLeaf needed leaf
     */
    public static void expandLeaf(JTree tree, List<TreePath> listOfLeaf) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandLeafList(tree, new TreePath(root), listOfLeaf);
    }

    private static void expandLeafList(JTree tree, TreePath path, List<TreePath> listOfLeaf) {
        TreeNode node = (TreeNode) path.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            Enumeration<?> enumeration = node.children();
            while (enumeration.hasMoreElements()) {
                TreeNode n = (TreeNode) enumeration.nextElement();
                TreePath p = path.pathByAddingChild(n);

                expandLeafList(tree, p, listOfLeaf);
            }
        }

        if (isNeededLeaf(listOfLeaf, path)) {
            tree.expandPath(path);
        }
    }

    private static boolean isNeededLeaf(List<TreePath> listOfLeaf, TreePath currentPath) {
        var lastElementPath = currentPath.getLastPathComponent();
        for (TreePath treePath : listOfLeaf) {
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
}
