/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.config.settings;

public class PinNoteSettings {

    /**
     * Pin Note running in debug mode
     */
    private boolean debugMode;

    private EvernoteSettings evernoteSettings;


    public PinNoteSettings(boolean isDebug, EvernoteSettings settings) {
        debugMode = isDebug;
        evernoteSettings = settings;
    }

    /**
     * Application in debug Mode
     *
     * @return is Debug mode
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Get Evernote settings
     *
     * @return evernote settings
     */
    public EvernoteSettings getEvernoteSettings() {
        return evernoteSettings;
    }
}
