package org.bigtows.window.ui.menu.adapter;

import com.intellij.openapi.ui.JBPopupMenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickMouseAdapter extends MouseAdapter {

    private final JBPopupMenu popupMenu;

    public ClickMouseAdapter(JBPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        doPop(e);
    }

    protected void doPop(MouseEvent e) {
        this.popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }
}
