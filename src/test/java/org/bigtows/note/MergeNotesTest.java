/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note;

import com.google.gson.Gson;
import org.bigtows.note.evernote.EvernoteNotes;
import org.bigtows.note.evernote.EvernoteTarget;
import org.bigtows.note.evernote.UniqueIdGenerator;
import org.bigtows.note.storage.util.MergeNotes;
import org.bigtows.util.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MergeNotesTest {

    private MergeNotes mergeNotes;

    private UniqueIdGenerator uniqueIdGeneratorStatic;

    private EvernoteNotes defaultClientNotes;


    private EvernoteNotes defaultServerNotes;

    @Before
    public void before() {
        mergeNotes = new MergeNotes();
        uniqueIdGeneratorStatic = data -> data[0] + data[1];

        this.defaultClientNotes = this.buildDefaultNotes(uniqueIdGeneratorStatic);
        this.defaultServerNotes = this.buildDefaultNotes(uniqueIdGeneratorStatic);
    }


    @Test
    public void testSimpleMerge() {
        EvernoteNotes synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);


        Assert.assertEquals(Resource.getJsonFile("notes/merge/testSimpleMerge"), synchronizedNotes.toString());
    }

    @Test
    public void testMergeRemoveTaskAtClient() {
        mergeNotes.setCacheNotes(this.cloneNotes(defaultClientNotes));
        defaultClientNotes.getAllTarget().get(0).getAllTask().get(0).remove();

        EvernoteNotes synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveTaskAtClient"), synchronizedNotes.toString());

    }

    @Test
    public void testMergeRemoveSubTaskAtClient() {
        mergeNotes.setCacheNotes(this.cloneNotes(defaultClientNotes));
        defaultClientNotes.getAllTarget().get(0).getAllTask().get(0).getSubTask().get(0).remove();

        EvernoteNotes synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveSubTaskAtClient"), synchronizedNotes.toString());
    }


    @Test
    public void testMergeRemoveTaskAtServer() {
        mergeNotes.setCacheNotes(this.cloneNotes(defaultClientNotes));
        defaultServerNotes.getAllTarget().get(0).getAllTask().get(0).remove();

        EvernoteNotes synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveTaskAtServer"), synchronizedNotes.toString());
    }

    @Test
    public void testMergeRemoveSubTaskAtServer() {
        mergeNotes.setCacheNotes(this.cloneNotes(defaultClientNotes));
        defaultServerNotes.getAllTarget().get(0).getAllTask().get(0).getSubTask().get(0).remove();

        EvernoteNotes synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveSubTaskAtServer"), synchronizedNotes.toString());
    }


    @Test
    public void testMergeChangeNameTask() {
        mergeNotes.setCacheNotes(this.cloneNotes(defaultClientNotes));

        defaultServerNotes.getAllTarget().get(0).getAllTask().get(0).getSubTask().get(0).editNameTask("new 1.1");
        defaultClientNotes.getAllTarget().get(0).getAllTask().get(0).editNameTask("new 1");
        EvernoteNotes synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeChangeNameTask"), synchronizedNotes.toString());
    }

    @Test
    public void testMergeNewTasksOnServer() {
        mergeNotes.setCacheNotes(this.cloneNotes(defaultClientNotes));
        EvernoteTarget target = defaultServerNotes.getAllTarget().get(0);
        target.addTask(true, "2").addSubTask("2.1");
        target.getAllTask().get(0).addSubTask(true, "1.3");
        EvernoteNotes synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeNewTasksOnServer"), synchronizedNotes.toString());
    }


    @Test
    public void testMergeNewTasksOnClient() {
        mergeNotes.setCacheNotes(this.cloneNotes(defaultClientNotes));

        EvernoteTarget target = defaultClientNotes.getAllTarget().get(0);
        target.addTask(true, "2").addSubTask("2.1");
        target.getAllTask().get(0).addSubTask(true, "1.3");
        EvernoteNotes synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeNewTasksOnClient"), synchronizedNotes.toString());
    }

    @Test
    public void testMergeNewTargetOnServer() {
        mergeNotes.setCacheNotes(this.cloneNotes(defaultClientNotes));
        defaultServerNotes.addTarget("Target 2!");
        EvernoteNotes synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeNewTargetOnServer"), synchronizedNotes.toString());
    }

    @Test
    public void testMergeRemoveTargetOnClient() {
        mergeNotes.setCacheNotes(this.cloneNotes(defaultClientNotes));
        defaultClientNotes.getAllTarget().get(0).remove();
        EvernoteNotes synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveTargetOnClient"), synchronizedNotes.toString());
    }

    @Test
    public void testMergeRemoveTargetOnServer() {
        mergeNotes.setCacheNotes(this.cloneNotes(defaultClientNotes));
        defaultServerNotes.getAllTarget().get(0).remove();
        EvernoteNotes synchronizedNotes = mergeNotes.sync(defaultClientNotes, defaultServerNotes);
        Assert.assertEquals(Resource.getJsonFile("notes/merge/testMergeRemoveTargetOnServer"), synchronizedNotes.toString());
    }

    /**
     * Build default notes
     *
     * @param uniqueIdGenerator generator unique id generator
     * @return Notes
     */
    private EvernoteNotes buildDefaultNotes(UniqueIdGenerator uniqueIdGenerator) {
        EvernoteNotes notes = new EvernoteNotes(uniqueIdGenerator);
        EvernoteTarget clientTarget = notes.addTarget("Target");
        clientTarget.setGuid("TargetGuid");
        clientTarget.addTask(false, "1").addSubTask("1.1").getTask().addSubTask(true, "1.2");
        return notes;
    }


    /**
     * Clone notes
     *
     * @param notes notes
     * @return Evernote notes
     */
    private EvernoteNotes cloneNotes(EvernoteNotes notes) {
        //TODO This Best clone EVER xD
        //Save cache
        Gson gson = new Gson();
        return gson.fromJson(notes.toString(), EvernoteNotes.class);
    }

}
