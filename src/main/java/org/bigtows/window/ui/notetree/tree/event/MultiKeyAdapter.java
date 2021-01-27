package org.bigtows.window.ui.notetree.tree.event;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public abstract class MultiKeyAdapter extends KeyAdapter {


    private final Set<Integer> extendedKeyCodeBuffer = new HashSet<>();
    private final Set<Integer> keyCodeBuffer = new HashSet<>();
    private final Set<Character> keyCharBuffer = new HashSet<>();


    @Override
    public synchronized final void keyPressed(KeyEvent e) {
        extendedKeyCodeBuffer.add(e.getExtendedKeyCode());
        keyCodeBuffer.add(e.getKeyCode());
        keyCharBuffer.add(e.getKeyChar());
        this.keyPressed();
    }




    @Override
    public synchronized final void keyReleased(KeyEvent e) {
        extendedKeyCodeBuffer.remove(e.getExtendedKeyCode());
        keyCodeBuffer.remove(e.getKeyCode());
        keyCharBuffer.remove(e.getKeyChar());
    }

    public abstract void keyPressed();


    public boolean hasKeys(Integer... keys) {
        for (Integer key : keys) {
            if (!(extendedKeyCodeBuffer.contains(key)
                    || keyCodeBuffer.contains(key)
                    || keyCharBuffer.contains(key)
            )) {
                return false;
            }
        }

        return true;
    }
}
