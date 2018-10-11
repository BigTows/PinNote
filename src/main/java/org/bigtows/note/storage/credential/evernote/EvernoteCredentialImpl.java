package org.bigtows.note.storage.credential.evernote;

import org.bigtows.components.UserStorage;
import org.bigtows.config.settings.PinNoteSettings;
import org.bigtows.note.storage.credential.EvernoteCredential;

public class EvernoteCredentialImpl implements EvernoteCredential {

    private final UserStorage userStorage;
    /**
     * Token for access to server
     */
    private String token;

    /**
     * Evernote service type
     */
    private ServiceType serviceType;

    /**
     * @param pinNoteSettings
     * @param userStorage
     */
    public EvernoteCredentialImpl(PinNoteSettings pinNoteSettings, UserStorage userStorage) {
        this.userStorage = userStorage;
        this.token = userStorage.getEvernoteToken();
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


    public void setToken(String token) {
        userStorage.saveEvernoteToken(token);
        this.token = token;
    }
}
