package org.bigtows.window;

import com.google.inject.Inject;
import com.intellij.openapi.options.ConfigurationException;
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
        gui.setAccountName(this.getAccountName());
        gui.setOnLogout(this::logoutProcess);
        return gui.getRoot();
    }

    private void logoutProcess() {
        ToolWindowFactory toolWindow = evernoteNoteView.getToolWindow();
        if (toolWindow instanceof RightToolWindow) {
            evernoteCredential.setToken(null);
            ((RightToolWindow) toolWindow).initEvernoteToken(evernoteNoteView.getAdapter().getProject());
            gui.setAccountName(null);
        }
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {
        gui = null;
    }


    /**
     * Get account name
     *
     * @return account name
     */
    private String getAccountName() {
        String[] splitToken = evernoteCredential.getToken().split("A=");
        if (splitToken.length != 2) {
            return null;
        }

        return splitToken[1].split(":")[0];
    }
}
