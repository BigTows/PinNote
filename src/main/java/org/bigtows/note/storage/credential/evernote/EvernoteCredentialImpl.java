package org.bigtows.note.storage.credential.evernote;

import org.bigtows.components.EvernoteTokenStorage;
import org.bigtows.config.settings.PinNoteSettings;
import org.bigtows.note.storage.credential.EvernoteCredential;

/**
 * The type Evernote credential.
 */
public class EvernoteCredentialImpl implements EvernoteCredential {

    private final EvernoteTokenStorage evernoteTokenStorage;
    /**
     * Token for access to server
     */
    private String token;

    /**
     * Evernote service type
     */
    private ServiceType serviceType;


    /**
     * Instantiates a new Evernote credential.
     *
     * @param pinNoteSettings      the pin note settings
     * @param evernoteTokenStorage the evernote token storage
     */
    public EvernoteCredentialImpl(PinNoteSettings pinNoteSettings, EvernoteTokenStorage evernoteTokenStorage) {
        this.evernoteTokenStorage = evernoteTokenStorage;
        this.token = evernoteTokenStorage.getEvernoteToken();
        this.serviceType = pinNoteSettings.isDebugMode() ? ServiceType.SANDBOX : ServiceType.PRODUCTION;
    }

    @Override
    public ServiceType getServiceType() {
        return serviceType;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        evernoteTokenStorage.saveEvernoteToken(token);
        this.token = token;
    }
}
