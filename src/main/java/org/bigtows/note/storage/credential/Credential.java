package org.bigtows.note.storage.credential;

/**
 * Simple interface of credential
 */
public interface Credential {

    /**
     * Get token for access to API
     *
     * @return token
     */
     String getToken();
}
