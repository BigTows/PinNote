package org.bigtows.notebook;

import java.util.List;

public interface TaskWithSubTask<T extends Task> extends Task {

    List<T> getSubTask();
}
