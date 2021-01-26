package org.bigtows.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import org.bigtows.window.ui.pinnote.api.ExternalCallerPinNoteAction;
import org.jetbrains.annotations.NotNull;

public class AddNewTargetForCurrentStorageAction extends AnAction {

    private final ExternalCallerPinNoteAction api;

    public AddNewTargetForCurrentStorageAction() {
        this.api = ServiceManager.getService(ExternalCallerPinNoteAction.class);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        api.actionAddTarget();
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
