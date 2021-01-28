package org.bigtows.window.ui.notetree.tree.event;

/**
 * User shortcut for create new task's
 */
public interface UserShortcutPressed {

    /**
     * Create task.
     *
     * @param isRoot new task for root or current location. It's can be subtask or just task
     */
    void newTask(boolean isRoot);


    /**
     * Create sub task child for task
     */
    void newSubTask();

    /**
     * Delete current task
     */
    void delete();

    /**
     * Set cursor at previous task
     */
    void selectPreviousTask();

    /**
     * Set cursor at next task
     */
    void selectNextTask();
}
