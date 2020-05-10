package org.bigtows.notebook.evernote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import org.bigtows.notebook.Note;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@ToString
public class EvernoteNote implements Note<EvernoteTask>,Cloneable {


    private String id;

    private String name;

    @Builder.Default
    private final List<EvernoteTask> allTask = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<EvernoteTask> getTasks() {
        return allTask;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
