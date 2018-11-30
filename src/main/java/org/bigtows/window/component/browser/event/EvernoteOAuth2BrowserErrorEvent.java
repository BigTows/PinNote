/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.component.browser.event;

/**
 * Event of error
 */
public interface EvernoteOAuth2BrowserErrorEvent {
    /**
     * Event call when component execute error's
     *
     * @param exception exception
     */
    void onError(Exception exception);

}
