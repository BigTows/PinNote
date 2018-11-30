/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.components;

import com.intellij.ide.util.PropertiesComponent;

/**
 * Implements user storage. Based on JetBrains "PropertiesComponent"
 */
public class SimpleEvernoteTokenStorage implements EvernoteTokenStorage {

    /**
     * Instance of JetBrains component for access to property storage.
     */
    private final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();


    @Override
    public void saveEvernoteToken(String token) {
        propertiesComponent.setValue("Evernote.Token", token);
    }

    @Override
    public String getEvernoteToken() {
        return propertiesComponent.getValue("Evernote.Token");
    }

    @Override
    public boolean hasEvernoteToken() {
        return propertiesComponent.getValue("Evernote.Token") != null;
    }
}
