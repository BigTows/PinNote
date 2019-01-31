/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window;

import com.google.inject.Inject;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.bigtows.PinNote;
import org.bigtows.note.storage.credential.EvernoteCredential;
import org.bigtows.window.component.form.SettingsWindowForm;
import org.bigtows.window.component.note.EvernoteNoteView;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SettingsWindow implements SearchableConfigurable {


    @Inject
    private EvernoteCredential evernoteCredential;

    @Inject
    private EvernoteNoteView evernoteNoteView;

    private SettingsWindowForm gui;


    public SettingsWindow() {
        PinNote.injector.injectMembers(this);
    }


    @Nls
    @Override
    public String getDisplayName() {
        return "PinNote";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "preference.PinNote";
    }

    @NotNull
    @Override
    public String getId() {
        return "preference.PinNote";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        gui = new SettingsWindowForm();
        gui.setClientIsLogged(evernoteCredential.getToken() != null);
        gui.setOnLogout(this::logoutProcess);
        return gui.getRoot();
    }

    /**
     * Logout process
     */
    private void logoutProcess() {
        ToolWindowFactory toolWindow = evernoteNoteView.getToolWindow();
        if (toolWindow instanceof RightToolWindow) {
            evernoteCredential.setToken(null);
            ((RightToolWindow) toolWindow).initEvernoteToken(evernoteNoteView.getAdapter().getProject());
            gui.setClientIsLogged(false);
        }
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {
        gui = null;
    }

}
