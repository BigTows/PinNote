package org.bigtows.service.note.notebook.evernote;

import lombok.Builder;
import lombok.Data;
import org.bigtows.service.note.notebook.Task;
import org.bigtows.service.note.notebook.TaskWithSubTask;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class EvernoteTaskWithSubTask implements TaskWithSubTask {


    private String id;

    private String name;

    private boolean checked;

    @Builder.Default
    private List<Task> subTask = new ArrayList<>();

    @Override
    public List<Task> getSubTask() {
        return this.subTask;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isChecked() {
        return this.checked;
    }
}
