package org.bigtows.service.note.notebook;

import java.util.List;

public interface Note<T extends Task> {


    String getId();

    String getName();

    void setName(String name);

    List<T> getTasks();

    Notebook getNotebook();

    void setId(String id);

}