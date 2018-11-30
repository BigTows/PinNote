/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.component.browser.event;

import org.bigtows.window.controller.ResponseToken;

public interface EvernoteOAuth2BrowserTokenSuccessProcessedEvent {

    /**
     * On
     * @param responseToken
     */
    void onTokenSuccessProcessed (ResponseToken responseToken);
    
}
