package org.bigtows.window.ui.menu;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;

import java.awt.event.ActionListener;

/**
 * Popup menu with "Delete" item
 */
public class DeletePopupMenu extends JBPopupMenu {

    /**
     * Constructor
     *
     * @param deleteActionListener action when user pressed "Delete" button
     */
    public DeletePopupMenu(ActionListener deleteActionListener) {
        JBMenuItem delete = new JBMenuItem("Delete", AllIcons.Vcs.Remove);
        super.add(delete);

        delete.addActionListener(deleteActionListener);
    }
}
