/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.service.note.notebook;

/**
 * Simple interface of credential
 */
public interface NotebookAccessible {

    /**
     * Get token for access to API
     *
     * @return token
     */
     String getToken();


     boolean hasToken();
}
