package org.bigtows.service.note.repository;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.bigtows.service.PinNoteService;
import org.bigtows.service.PinNoteState;
import org.bigtows.service.note.notebook.Notebook;
import org.bigtows.service.note.notebook.evernote.EvernoteNotebook;
import org.bigtows.service.state.EvernoteState;
import org.bigtows.service.state.StatusConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            }
        }
        return Optional.empty();
    }


    @Override
    public List<Notebook<?>> getAll() {
        final List<Notebook<?>> defaultNotebooks = new ArrayList<>();
        this.buildEvernoteNotebook(
                this.pinNoteState.getEvernoteState()
        ).ifPresent(defaultNotebooks::add);

        return defaultNotebooks;
    }
}
