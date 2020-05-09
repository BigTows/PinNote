package org.bigtows.window;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.bigtows.service.PinNoteEventManager;
import org.bigtows.service.PinNoteService;
import org.bigtows.notebook.evernote.EvernoteNotebook;
import org.bigtows.utils.PinNoteIcon;
import org.bigtows.window.ui.notetree.factory.EvernoteNoteTreeFactory;
import org.bigtows.window.ui.pinnote.PinNoteComponent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class RightToolWindowFactory implements ToolWindowFactory {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private PinNoteService pinNoteService;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        pinNoteService = project.getService(PinNoteService.class);
        var pinNoteComponent = new PinNoteComponent();
        ServiceManager.getService(PinNoteEventManager.class)
                .registerSourceUpdateEvent(() -> this.initNotebook(project, pinNoteComponent));
        toolWindow.getComponent().add(pinNoteComponent.getRoot());
        this.initNotebook(project, pinNoteComponent);
    }

    private void initNotebook(Project project, PinNoteComponent pinNoteComponent) {
        var allNotebook = pinNoteService.getNotebookRepository().getAll();
        pinNoteComponent.removeAllNotebook();
        allNotebook.forEach(notebook -> {
            if (notebook instanceof EvernoteNotebook) {
                SwingUtilities.invokeLater(() -> {
                    pinNoteComponent.addNotebook(notebook,
                            PinNoteIcon.TAB_EVERNOTE_ICON,
                            EvernoteNoteTreeFactory.buildNoteTreeForEvernote(project, (EvernoteNotebook) notebook)
                    );
                });
            } else {
                logger.warn("Not found builder for notebook: {} type", notebook.getClass());
            }
        });
    }

}
