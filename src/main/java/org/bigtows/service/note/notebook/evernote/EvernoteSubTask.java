package org.bigtows.service.note.notebook.evernote;

import lombok.Builder;
import lombok.Data;
import org.bigtows.service.note.notebook.Task;

@Data
@Builder
public class EvernoteSubTask implements Task {

    private String id;

    private String name;

    private boolean checked;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }
}