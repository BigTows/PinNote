/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note;

import java.util.List;

public interface NoteTask<T, S extends NoteSubTask> extends Task {

    S addSubTask(String nameSubTask);


    S addSubTask(boolean isCompleted, String nameSubTask);

    List<S> getSubTask();

    T getTarget();

}
