package org.bigtows.service.note.notebook.evernote;

import lombok.Builder;
import org.bigtows.service.note.notebook.TaskWithSubTask;

import java.util.ArrayList;
import java.util.List;

@Builder
public class EvernoteTask implements TaskWithSubTask<EvernoteSubTask> {


    private final String id;

    private String name;

    private boolean checked;

    @Builder.Default
    private List<EvernoteSubTask> subTasks = new ArrayList<>();

    @Override
    public List<EvernoteSubTask> getSubTask() {
        return subTasks;
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

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
