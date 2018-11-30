/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.component.browser.callback;

import org.bigtows.note.storage.credential.EvernoteCredential;
import org.bigtows.window.controller.ResponseToken;

public interface EvernoteOAuth2BrowserCallback {

    /**
     * On
     * @param responseToken
     */
    void onToken (ResponseToken responseToken);

    void onError(Exception e);
}
