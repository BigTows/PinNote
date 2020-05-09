package org.bigtows.notebook.repository;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import org.bigtows.notebook.Notebook;
import org.bigtows.notebook.evernote.EvernoteNotebook;
import org.bigtows.notebook.local.LocalNotebook;
import org.bigtows.service.PinNoteService;
import org.bigtows.service.PinNoteState;
import org.bigtows.service.state.EvernoteState;
import org.bigtows.service.state.LocalNotebookState;
import org.bigtows.service.state.StatusConnection;
import org.bigtows.utils.PinNoteIcon;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleNotebookRepository implements NotebookRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Project project;
    private final PinNoteState pinNoteState;

    private SimpleNotebookRepository(Project project) {
        this.project = project;
        this.pinNoteState = project.getService(PinNoteService.class).getState();
    }


    private Optional<EvernoteNotebook> buildEvernoteNotebook(EvernoteState evernoteState) {

        if (evernoteState.isEnable()) {
            try {
                var evernoteNotebook = new EvernoteNotebook(evernoteState);
                evernoteState.setStatusConnection(StatusConnection.CONNECTED);
                return Optional.of(evernoteNotebook);
            } catch (Throwable e) {
                logger.warn("Can't connect {}", EvernoteNotebook.class);
                evernoteState.setStatusConnection(StatusConnection.HAS_PROBLEM);
                Notification notification = new Notification(
                        "Repository.cannotLoad.Evernote",
                        "Can't load Evernote notes.",
                        "Your session may be outdated",
                        NotificationType.ERROR
                );
                notification.setIcon(PinNoteIcon.NOTIFICATION_PIN_NOTE);
                notification.addAction(new NotificationAction("Update a session") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                        ShowSettingsUtil.getInstance().showSettingsDialog(null, "PinNote");
                    }
                });
                SwingUtilities.invokeLater(() -> notification.notify(project));
            }
        }
        return Optional.empty();
    }

    private Optional<LocalNotebook> buildLocalNotebook(LocalNotebookState localNotebookState) {
        if (localNotebookState.isEnable()) {
            return Optional.of(new LocalNotebook());
        }
        return Optional.empty();
    }


    @Override
    public List<Notebook<?>> getAll() {
        final List<Notebook<?>> defaultNotebooks = new ArrayList<>();
        this.buildEvernoteNotebook(
                this.pinNoteState.getEvernoteState()
        ).ifPresent(defaultNotebooks::add);

        this.buildLocalNotebook(this.pinNoteState.getLocalNotebookState())
                .ifPresent(defaultNotebooks::add);

        return defaultNotebooks;
    }
}
