package org.bigtows.window.ui.menu.adapter;

import com.intellij.openapi.ui.JBPopupMenu;

import java.awt.event.MouseEvent;

/**
 * Adapter for popup menu, showing only after pressed right key
 */
public final class RightClickMouseAdapter extends ClickMouseAdapter {

    /**
     * Constructor
     *
     * @param popupMenu instance of popup menu
     */
    public RightClickMouseAdapter(JBPopupMenu popupMenu) {
        super(popupMenu);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            super.doPop(e);
        }
    }

}
