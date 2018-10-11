/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note;

import org.bigtows.note.exception.NoteException;

import java.util.List;

/**
 * Interface of Notes
 *
 * @param <T> Target
 */
public interface Notes<T extends NoteTarget> {


    /**
     * Added Target to Notes
     *
     * @param name Name of target
     * @return instance target
     */
    public T addTarget(String name) throws NoteException;


    public T addTarget(T target);

    public List<T> getAllTarget();

}
