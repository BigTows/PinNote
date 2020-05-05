package org.bigtows.window.ui.menu.adapter;

import com.intellij.openapi.ui.JBPopupMenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Adapter for popup menu, showing only after pressed any mouse key
 */
public class ClickMouseAdapter extends MouseAdapter {

    /**
     * Instance of popup menu
     */
    private final JBPopupMenu popupMenu;

    /**
     * Constructor
     *
     * @param popupMenu popup menu
     */
    public ClickMouseAdapter(JBPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        doPop(e);
    }

    /**
     * Show popup menu
     *
     * @param event mouse event
     */
    protected void doPop(MouseEvent event) {
        this.popupMenu.show(event.getComponent(), event.getX(), event.getY());
    }
}
