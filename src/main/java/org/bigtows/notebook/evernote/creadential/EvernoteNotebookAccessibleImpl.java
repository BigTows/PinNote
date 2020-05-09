/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.notebook.evernote.creadential;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.bigtows.component.property.PropertyStorage;

/**
 * The type Evernote credential.
 */
public class EvernoteNotebookAccessibleImpl implements EvernoteNotebookAccessible {

    private static final String tokenPath = "PinNote.Evernote.token";

    private final PropertyStorage propertyStorage;
    /**
     * Token for access to server
     */
    private String token;

    /**
     * Evernote service type
     */
    private ServiceType serviceType;


    public EvernoteNotebookAccessibleImpl(Project project) {
        this.propertyStorage = ServiceManager.getService(PropertyStorage.class);
        this.token = propertyStorage.getString(tokenPath);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.PRODUCTION;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.propertyStorage.setString(tokenPath, token);
        this.token = token;
    }

    @Override
    public boolean hasToken() {
        return this.token == null;
    }
}
