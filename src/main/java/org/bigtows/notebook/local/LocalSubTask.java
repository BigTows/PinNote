package org.bigtows.notebook.local;

import lombok.Builder;
import lombok.Data;
import org.bigtows.notebook.Task;

@Data
@Builder
public class LocalSubTask implements Task {

    private String name;

    private boolean checked;

    @Override
    public String getId() {
        return "none";
    }
}
