/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.visual;

import org.bigtows.note.Notes;

/**
 * Interface for visualisation Note
 *
 * @param <NODE>
 * @param <NOTES>
 */
public interface VisualAdapter<NODE, NOTES extends Notes> {

    /**
     * Setup notes for visual
     *
     * @param notes Notes
     */
    void setNotes(NOTES notes);

    /**
     * Force update Notes in NODE
     *
     * @param node node, element on display
     */
    void forceUpdate(NODE node);

    /**
     * Update notes
     */
    void updateNotes(NODE node);

    /**
     * Setup error handler
     *
     * @param errorHandler error handler callback
     */
    void setErrorHandler(VisualAdapterErrorHandler errorHandler);

}
