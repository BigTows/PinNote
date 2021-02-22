package org.bigtows.window.ui.notetree.tree.event;

/**
 * User shortcut for create new task's
 */
public interface UserAction {

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
     * Set cursor at previous task
     */
    void selectPreviousTask();

    /**
     * Set cursor at next task
     */
    void selectNextTask();

    /**
     * Called when user start editing some tasks
     */
    void onEditing();
}
