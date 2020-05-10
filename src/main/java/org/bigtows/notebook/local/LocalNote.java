package org.bigtows.notebook.local;

import lombok.Builder;
import lombok.Data;
import org.bigtows.notebook.Note;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class LocalNote implements Note<LocalTask> {

    private String name;

    private final List<LocalTask> tasks = new ArrayList<>();

    @Override
    public String getId() {
        return "none";
    }



    @Override
    public void setId(String id) {

    }
}
