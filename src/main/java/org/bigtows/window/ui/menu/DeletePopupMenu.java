package org.bigtows.window.ui.menu;

import javax.swing.*;
import java.awt.event.ActionListener;

public class DeletePopupMenu extends JPopupMenu {
    private final JMenuItem delete;

    public DeletePopupMenu(ActionListener actionListener) {
        this.delete = new JMenuItem("Delete");
        super.add(this.delete);

        this.delete.addActionListener(actionListener);
    }
}
