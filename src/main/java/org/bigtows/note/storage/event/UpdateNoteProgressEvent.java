/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.storage.event;

import javax.annotation.Nullable;

/**
 * Interface of Update note in storage
 */
public interface UpdateNoteProgressEvent {
    /**
     * Then progress has been change
     *
     * @param title    Title of operation, can bee null
     * @param progress Progress of operation
     */
    void onChangeProgress(@Nullable String title, double progress);
}
