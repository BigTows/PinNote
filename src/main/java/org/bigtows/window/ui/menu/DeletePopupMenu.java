package org.bigtows.window.ui.menu;

import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;

import java.awt.event.ActionListener;

public class DeletePopupMenu extends JBPopupMenu {
    private final JBMenuItem delete;

    public DeletePopupMenu(ActionListener actionListener) {
        this.delete = new JBMenuItem("Delete");
        super.add(this.delete);

        this.delete.addActionListener(actionListener);
    }
}
