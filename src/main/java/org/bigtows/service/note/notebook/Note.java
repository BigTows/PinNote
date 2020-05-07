package org.bigtows.service.note.notebook;

import java.util.List;

/**
 * Note of
 * @param <T>
 */
public interface Note<T extends Task> {


    String getId();

    String getName();

    void setName(String name);

    List<T> getTasks();

    void setId(String id);

}
