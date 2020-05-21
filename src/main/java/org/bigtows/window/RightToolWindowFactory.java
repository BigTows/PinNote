package org.bigtows.window;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.bigtows.notebook.evernote.EvernoteNotebook;
import org.bigtows.notebook.local.LocalNotebook;
import org.bigtows.service.PinNoteEventManager;
import org.bigtows.service.PinNoteService;
import org.bigtows.utils.PinNoteIcon;
import org.bigtows.window.ui.notetree.NoteTree;
import org.bigtows.window.ui.notetree.factory.EvernoteNoteTreeFactory;
import org.bigtows.window.ui.notetree.factory.LocalNoteTreeFactory;
import org.bigtows.window.ui.pinnote.PinNoteComponent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Factory of ToolWindow for plugin PinNote.
 */
final public class RightToolWindowFactory implements ToolWindowFactory {

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Service of PinNote plugin.
     */
    private PinNoteService pinNoteService;


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        pinNoteService = project.getService(PinNoteService.class);
        var pinNoteComponent = new PinNoteComponent();
        ServiceManager.getService(PinNoteEventManager.class)
                .registerSourceUpdateEvent(() -> this.renderNotebookLater(project, pinNoteComponent));
        toolWindow.getComponent().add(pinNoteComponent.getRoot());
        this.renderNotebookLater(project, pinNoteComponent);
    }

    private void renderNotebookLater(Project project, PinNoteComponent pinNoteComponent) {
        var allNotebook = pinNoteService.getNotebookRepository().getAll();
        pinNoteComponent.removeAllNotebook();
        allNotebook.forEach(notebook -> {
            Icon icon = null;
            NoteTree noteTree = null;

            if (notebook instanceof EvernoteNotebook) {
                icon = PinNoteIcon.TAB_EVERNOTE_ICON;
                noteTree = EvernoteNoteTreeFactory.buildNoteTreeForEvernote(project, (EvernoteNotebook) notebook);
            } else if (notebook instanceof LocalNotebook) {
                icon = PinNoteIcon.TAB_LOCAL_NOTE_ICON;
                noteTree = LocalNoteTreeFactory.buildNoteTreeByLocalNotebook(project, (LocalNotebook) notebook);
            }

            if (icon != null && noteTree != null) {
                renderNotebookLater(pinNoteComponent, notebook.getName(), icon, noteTree);
            } else {
                logger.warn("Not found builder for notebook: {} type", notebook.getClass());
            }

        });
    }

    /**
     * Add notebook into PinNote component, later
     *
     * @param pinNoteComponent component
     * @param name             of notebook
     * @param icon             icon for Tab
     * @param noteTree         data (notes)
     * @see PinNoteComponent
     */
    private void renderNotebookLater(PinNoteComponent pinNoteComponent, String name, Icon icon, NoteTree noteTree) {
        SwingUtilities.invokeLater(() -> pinNoteComponent.addNotebook(name, icon, noteTree));
    }

}
