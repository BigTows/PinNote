package org.bigtows.service.note.notebook.evernote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.bigtows.service.note.notebook.Note;
import org.bigtows.service.note.notebook.Notebook;
import org.bigtows.service.note.notebook.Task;

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
