/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note;

import org.bigtows.notebook.evernote.EvernoteNote;
import org.bigtows.notebook.evernote.EvernoteSubTask;
import org.bigtows.notebook.evernote.EvernoteTask;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EvernoteNotesTest {


    private List<EvernoteNote> notes = new ArrayList<>();

    @Test
    public void createEvernoteNoteTest() {
        Assert.assertEquals("EvernoteNote(id=1, name=Test, allTask=[EvernoteTask(id=null, name=null, checked=false, subTasks=[EvernoteSubTask(id=null, name=null, checked=false)])])", EvernoteNote.builder()
                .id("1")
                .name("Test")
                .allTask(List.of(
                        EvernoteTask.builder()
                                .subTasks(
                                        List.of(
                                                EvernoteSubTask.builder()
                                                        .build()
                                        )
                                )
                                .build()
                ))
                .build().toString());
    }

    @Test
    public void createEvernoteTaskTest() {
        Assert.assertEquals(
                "EvernoteTask(id=1, name=Test, checked=true, subTasks=[EvernoteSubTask(id=asd, name=11, checked=true)])",
                EvernoteTask.builder()
                        .id("1")
                        .name("Test")
                        .checked(true)
                        .subTasks(List.of(
                                EvernoteSubTask.builder()
                                        .checked(true)
                                        .name("11")
                                        .id("asd")
                                        .build()
                        ))
                        .build().toString()
        );
    }

    @Test
    public void createEvernoteSubTaskTest() {
        Assert.assertEquals("EvernoteSubTask(id=1, name=Test, checked=true)", EvernoteSubTask.builder()
                .id("1")
                .name("Test")
                .checked(true)
                .build().toString());
    }

    @Test
    public void testSetterIdEvernoteNote() {
        var note = EvernoteNote.builder().build();
        note.setId("OPA");
        Assert.assertEquals("OPA", note.getId());
    }
}
