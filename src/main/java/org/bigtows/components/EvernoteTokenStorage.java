/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.components;

/**
 * Interface of user storage
 */
public interface EvernoteTokenStorage {

    /**
     * Save evernote token
     *
     * @param token token for access to api
     */
    void saveEvernoteToken(String token);

    /**
     * Get evernote token for access to api
     *
     * @return evernote token
     */
    String getEvernoteToken();

    /**
     * Check avaliable evernote token
     * @return {@code true} if token has in storage, else {@code false}
     */
    boolean hasEvernoteToken();

}
