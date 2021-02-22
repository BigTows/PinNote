package org.bigtows.window.ui.notetree.tree.transfer;

import org.bigtows.window.ui.notetree.tree.node.AbstractTaskTreeNode;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * Implement of Transferable object for PinNoteTree
 *
 * @see org.bigtows.window.ui.notetree.NoteTree
 */
public class PinNoteNodesTransferable implements Transferable {
    /**
     * Available type of data
     */
    private final DataFlavor[] dataFlavors;

    /**
     * Data for transfer
     */
    private final AbstractTaskTreeNode[] nodes;

    /**
     * Constructor
     *
     * @param dataFlavor available of data
     * @param nodes      data
     */
    public PinNoteNodesTransferable(DataFlavor dataFlavor, AbstractTaskTreeNode[] nodes) {
        this.dataFlavors = new DataFlavor[]{dataFlavor};
        this.nodes = nodes;
    }

    @NotNull
    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {
        if (!isDataFlavorSupported(flavor))
            throw new UnsupportedFlavorException(flavor);
        return nodes;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return this.dataFlavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor dataFlavor : dataFlavors) {
            if (dataFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
}
