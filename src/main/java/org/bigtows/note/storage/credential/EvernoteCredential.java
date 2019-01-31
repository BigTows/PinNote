/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.storage.credential;

import org.bigtows.note.storage.credential.evernote.ServiceType;

public interface EvernoteCredential extends Credential {

    /**
     * Evernote has 2 servers.
     * <p>
     * SANDBOX, PRODUCTION
     * </p>
     *
     * @return Service type
     */
    ServiceType getServiceType();

    /**
     * Setup token
     *
     * @param token Key auth
     */
    void setToken(String token);
}
