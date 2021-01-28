package org.bigtows.window.ui.notetree.tree.event;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Key adapter with support multi key
 */
public abstract class MultiKeyAdapter extends KeyAdapter {

    /**
     * Buffer of key extended codes
     */
    private static final Set<Integer> extendedKeyCodeBuffer = new HashSet<>();

    /**
     * Buffer of key codes
     */
    private static final Set<Integer> keyCodeBuffer = new HashSet<>();

    /**
     * Buffer of key characters
     */
    private static final Set<Character> keyCharBuffer = new HashSet<>();


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

    /**
     * Invoked when a key has been pressed.
     */
    public abstract void keyPressed();

    /**
     * Check keys in buffer
     *
     * @param keys required keys
     * @return {@code true} if required keys exists in buffer else {@code false}
     */
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
