package org.bigtows.window.ui.menu;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;

import java.awt.event.ActionListener;

/**
 * Popup menu with "Delete" item
 */
public class DeletePopupMenu extends JBPopupMenu {
    private final JBMenuItem delete;

    public DeletePopupMenu(ActionListener actionListener) {
        this.delete = new JBMenuItem("Delete", AllIcons.Vcs.Remove);
        super.add(this.delete);

        this.delete.addActionListener(actionListener);
    }
}
