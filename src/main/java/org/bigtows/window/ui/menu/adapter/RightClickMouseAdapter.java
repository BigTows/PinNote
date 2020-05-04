package org.bigtows.window.ui.menu.adapter;

import com.intellij.openapi.ui.JBPopupMenu;

import java.awt.event.MouseEvent;

public class RightClickMouseAdapter extends ClickMouseAdapter {


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
