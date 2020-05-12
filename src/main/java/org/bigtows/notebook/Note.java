package org.bigtows.notebook;

import java.util.List;

/**
 * Note this is an entity that stores issues
 *
 * @param <T> any implementation of Task
 */
public interface Note<T extends Task> {

    /**
     * Get id note
     *
     * @return id of note
     */
    String getId();

    /**
     * Get name
     *
     * @return name of note
     */
    String getName();

    /**
     * Set name
     *
     * @param name of note
     */
    void setName(String name);

    List<T> getTasks();

    void setId(String id);

}
