/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.storage;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import org.bigtows.note.NoteTarget;
import org.bigtows.note.Notes;
import org.bigtows.note.storage.event.UpdateNoteProgressEvent;

public interface NoteStorage<N extends Notes, T extends NoteTarget> {


    public N updateNotes(N notes) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException;

    public void deleteTarget(T target);

    public N getAllNotes() throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException;

    public void subscribeUpdateNoteProgress(UpdateNoteProgressEvent event);

}
