package org.bigtows.config.settings;

public class PinNoteSettings {

    /**
     * Pin Note running in debug mode
     */
    private boolean debugMode = false;

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
