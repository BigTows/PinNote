package org.bigtows.service.note.notebook;

import java.util.List;

public interface TaskWithSubTask<T extends Task> extends Task {

    List<T> getSubTask();
}
