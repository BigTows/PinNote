package org.bigtows.window.ui.menu.adapter;

import com.intellij.openapi.ui.JBPopupMenu;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Adapter for popup menu, showing only after pressed right key
 */
public final class RightClickPopupMenuMouseAdapter extends ClickPopupMenuMouseAdapter {

    /**
     * Constructor
     *
     * @param popupMenu instance of popup menu
     */
    public RightClickPopupMenuMouseAdapter(JBPopupMenu popupMenu) {
        super(popupMenu);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            super.doPop(e);
        }
    }

}
