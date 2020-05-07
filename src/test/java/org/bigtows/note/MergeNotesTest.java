/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note;

import com.google.gson.Gson;
import org.bigtows.service.note.notebook.evernote.EvernoteNote;
import org.bigtows.service.note.notebook.evernote.EvernoteSubTask;
import org.bigtows.service.note.notebook.evernote.EvernoteTask;
import org.bigtows.service.note.notebook.evernote.MergeNotes;
import org.bigtows.util.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MergeNotesTest {

    private MergeNotes mergeNotes;


    private List<EvernoteNote> defaultClientNotes;


    private List<EvernoteNote> defaultServerNotes;

    @Before
    public void before() {
        mergeNotes = new MergeNotes();

        this.defaultClientNotes = this.buildDefaultNotes();
        this.defaultServerNotes = this.buildDefaultNotes();
    }


    @Test
    public void testSimpleMerge() {
        List<EvernoteNote> synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);

        Assert.assertEquals(Resource.getJsonFile("notes/merge/testSimpleMerge"),
                this.listNotesToString(synchronizedNotes)
        );
    }

    @Test
    public void testMergeRemoveTaskAtClient() {
        mergeNotes.setCacheNotes(this.buildDefaultNotes());
        defaultClientNotes.get(0).getTasks().remove(0);

        List<EvernoteNote> synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveTaskAtClient"),
                this.listNotesToString(synchronizedNotes)
        );

    }

    @Test
    public void testMergeRemoveSubTaskAtClient() {
        mergeNotes.setCacheNotes(this.buildDefaultNotes());
        defaultClientNotes.get(0).getTasks().get(0).getSubTask().remove(0);

        List<EvernoteNote> synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveSubTaskAtClient"),
                this.listNotesToString(synchronizedNotes)
        );
    }


    @Test
    public void testMergeRemoveTaskAtServer() {
        mergeNotes.setCacheNotes(this.buildDefaultNotes());
        defaultServerNotes.get(0).getTasks().remove(0);

        List<EvernoteNote> synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveTaskAtServer"),
                this.listNotesToString(synchronizedNotes)
        );
    }

    @Test
    public void testMergeRemoveSubTaskAtServer() {
        mergeNotes.setCacheNotes(this.buildDefaultNotes());
        defaultServerNotes.get(0).getTasks().get(0).getSubTask().remove(0);

        List<EvernoteNote> synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveSubTaskAtServer"),
                this.listNotesToString(synchronizedNotes)
        );
    }


    @Test
    public void testMergeChangeNameTask() {
        mergeNotes.setCacheNotes(this.buildDefaultNotes());

        defaultServerNotes.get(0).getTasks().get(0).getSubTask().get(0).setName("new 1.1");
        defaultClientNotes.get(0).getTasks().get(0).setName("new 1");
        List<EvernoteNote> synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeChangeNameTask"),
                this.listNotesToString(synchronizedNotes)
        );
    }

    @Test
    public void testMergeNewTasksOnServer() {
        mergeNotes.setCacheNotes(this.buildDefaultNotes());
        EvernoteNote target = defaultServerNotes.get(0);

        var task = EvernoteTask.builder()
                .name("2")
                .checked(true)
                .id("task-2")
                .build();
        target.getTasks().add(task);
        task.getSubTask().add(EvernoteSubTask.builder()
                .checked(false)
                .name("2.1")
                .id("sub-task-2.1")
                .build()
        );

        target.getTasks().get(0).getSubTask().add(
                EvernoteSubTask.builder()
                        .checked(true)
                        .name("1.3")
                        .id("sub-task-1.3")
                        .build()
        );
        List<EvernoteNote> synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeNewTasksOnServer"),
                this.listNotesToString(synchronizedNotes)
        );
    }


    @Test
    public void testMergeNewTasksOnClient() {
        mergeNotes.setCacheNotes(this.buildDefaultNotes());

        EvernoteNote target = defaultClientNotes.get(0);
        target.getTasks().add(
                EvernoteTask.builder()
                        .checked(true)
                        .name("2")
                        .id("task-2")
                        .subTasks(List.of(
                                EvernoteSubTask.builder()
                                        .name("2.1")
                                        .id("sub-task-2.1")
                                        .build()
                        ))
                        .build()
        );
        target.getTasks().get(0).getSubTask().add(
                EvernoteSubTask.builder()
                        .name("1.3")
                        .id("sub-task-1.3")
                        .checked(true)
                        .build()
        );
        List<EvernoteNote> synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeNewTasksOnClient"),
                this.listNotesToString(synchronizedNotes)
        );
    }

    @Test
    public void testMergeNewTargetOnServer() {
        mergeNotes.setCacheNotes(this.buildDefaultNotes());
        defaultServerNotes.add(
                EvernoteNote.builder()
                        .name("Target 2!")
                        .id("Target 2!")
                        .build()
        );
        List<EvernoteNote> synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeNewTargetOnServer"),
                this.listNotesToString(synchronizedNotes)
        );
    }

    @Test
    public void testMergeRemoveTargetOnClient() {
        mergeNotes.setCacheNotes(this.buildDefaultNotes());
        defaultClientNotes.remove(0);
        List<EvernoteNote> synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveTargetOnClient"),
                synchronizedNotes.toString()
        );
    }

    @Test
    public void testMergeRemoveTargetOnServer() {
        mergeNotes.setCacheNotes(this.buildDefaultNotes());
        defaultServerNotes.remove(0);
        List<EvernoteNote> synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveTargetOnServer"),
                synchronizedNotes.toString()
        );
    }

    @Test
    public void test(){
        mergeNotes.setCacheNotes(this.buildDefaultNotes());
        var notes = EvernoteNote.builder()
                 .name("test")
                .build();
        //defaultClientNotes.add(n)
    }

    /**
     * Build default notes
     *
     * @return Notes
     */
    private List<EvernoteNote> buildDefaultNotes() {
        List<EvernoteNote> notes = new ArrayList<>();
        List<EvernoteTask> task = new ArrayList<>();
        List<EvernoteSubTask> subTasks = new ArrayList<>();
        subTasks.add(EvernoteSubTask.builder()
                .name("1.1")
                .id("sub-task-1.1")
                .checked(false)
                .build()
        );
        subTasks.add(EvernoteSubTask.builder()
                .name("1.2")
                .id("sub-task-1.2")
                .checked(true)
                .build()
        );
        task.add(
                EvernoteTask.builder()
                        .name("1")
                        .id("task-1")
                        .checked(false)
                        .subTasks(subTasks)
                        .build()
        );
        notes.add(EvernoteNote.builder()
                .name("Target")
                .id("TargetGuid")
                .allTask(task)
                .build()
        );
        return notes;
    }

    /**
     * List to string Json
     *
     * @param evernoteNotes source
     * @return string Json
     */
    private String listNotesToString(List<EvernoteNote> evernoteNotes) {
        var gson = new Gson();
        StringBuilder stringBuilder = new StringBuilder();

        evernoteNotes.forEach(evernoteNote -> stringBuilder.append(gson.toJson(evernoteNote)));
        return stringBuilder.toString();
    }

}
