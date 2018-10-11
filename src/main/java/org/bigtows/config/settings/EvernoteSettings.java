package org.bigtows.config.settings;

/**
 * Settings for evernote
 */
public class EvernoteSettings {
    private String oAuth2Server = "localhost/evernoteOAuth2";


    public EvernoteSettings() {

    }

    public EvernoteSettings(String hostOAuth2) {
        this.oAuth2Server = hostOAuth2;
    }

    public String getOAuth2Server() {
        return oAuth2Server;
    }
}
