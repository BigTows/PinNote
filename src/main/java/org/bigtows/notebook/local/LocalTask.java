package org.bigtows.notebook.local;

import lombok.Builder;
import lombok.Data;
import org.bigtows.notebook.TaskWithSubTask;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class LocalTask implements TaskWithSubTask<LocalSubTask> {

    /**
     * Name of task
     */
    private String name;

    /**
     * Status of complete task
     */
    private boolean checked;

    /**
     * Subtask of this task
     */
    private final List<LocalSubTask> subTask = new ArrayList<>();

    @Override
    public String getId() {
        return "none";
    }
}
