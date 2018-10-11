/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note;

import org.bigtows.note.evernote.*;
import org.bigtows.util.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EvernoteNotesTest {


    private EvernoteNotes notes;

    private UniqueIdGenerator uniqueIdGeneratorStatic;

    @Before
    public void setup() {
        uniqueIdGeneratorStatic = data -> data[0] + data[1];
        this.notes = new EvernoteNotes(uniqueIdGeneratorStatic);
    }

    @Test
    public void testBuildJson() {
        notes.addTarget("Example target")
                .addTask("1")
                .addSubTask("1.1")
                .getTask().addSubTask("1.2")
                .getTask().getTarget().addTask("2").setCompleted(true);

        Assert.assertEquals(Resource.getJsonFile("notes/Notes_toString"), notes.toString());
    }

    @Test
    public void testMethodTask() {
        EvernoteTarget target = notes.addTarget("Test target");
        EvernoteTask task = target.addTask("Example task");

        Assert.assertEquals("Example task", task.getNameTask());
        Assert.assertEquals(0, task.getSubTask().size());
        Assert.assertFalse(task.isCompleted());
        Assert.assertEquals(target, task.getTarget());

        task.editNameTask("Example task, edited");
        task.setCompleted(true);

        Assert.assertEquals("Example task, edited", task.getNameTask());
        Assert.assertTrue(task.isCompleted());
    }

    @Test
    public void testMethodSubTask() {
        EvernoteTarget target = notes.addTarget("Test target");
        EvernoteTask task = target.addTask("Example task");
        EvernoteSubTask subTask = task.addSubTask("Example subTask");

        Assert.assertEquals("Example subTask", subTask.getNameTask());
        Assert.assertEquals(task, subTask.getTask());
        Assert.assertFalse(subTask.isCompleted());

        subTask.editNameTask("Example subTask, edited");
        subTask.setCompleted(true);

        Assert.assertEquals("Example subTask, edited", subTask.getNameTask());
        Assert.assertTrue(subTask.isCompleted());
    }

    @Test
    public void testAddedTaskWithStatus() {
        EvernoteTarget target = notes.addTarget("Test target");
        EvernoteTask task = target.addTask(true, "Example task");

        Assert.assertEquals("Example task", task.getNameTask());
        Assert.assertTrue(task.isCompleted());

    }

    @Test
    public void testAddedSubTaskWithStatus() {
        EvernoteTarget target = notes.addTarget("Test target");
        EvernoteSubTask subTask = target.addTask(true, "Example task")
                .addSubTask(true, "Example S");

        Assert.assertEquals("Example S", subTask.getNameTask());
        Assert.assertTrue(subTask.isCompleted());

    }

    @Test
    public void tesMethodNotes() {
        EvernoteNotes notes = new EvernoteNotes(uniqueIdGeneratorStatic);

        EvernoteTarget target = notes.addTarget("Test");

        Assert.assertEquals("Test", target.getName());
        Assert.assertEquals(1, notes.getAllTarget().size());
        Assert.assertEquals(target, notes.getAllTarget().get(0));

    }

    @Test
    public void tesMethodTarget() {
        EvernoteNotes notes = new EvernoteNotes(uniqueIdGeneratorStatic);

        EvernoteTarget target = notes.addTarget("Test");
        target.setGuid("d72dfad0-7d58-41b5-b2c9-4ca434abd543");
        target.setNameTarget("New testSimpleParse");

        Assert.assertEquals("New testSimpleParse", target.getName());
        Assert.assertEquals("d72dfad0-7d58-41b5-b2c9-4ca434abd543", target.getGuid());

        Assert.assertEquals(0, target.getAllTask().size());

        NoteTask task = target.addTask("new Task");
        Assert.assertEquals(1, target.getAllTask().size());
        Assert.assertEquals(task, target.getAllTask().get(0));


        Assert.assertEquals(Resource.getJsonFile("notes/Target_toString"), target.toString());

    }


    @Test
    public void simpleTestCreateTargetStructure() {
        EvernoteNotes notes = new EvernoteNotes(uniqueIdGeneratorStatic);

        EvernoteTarget clientTarget = notes.addTarget("Test");
        clientTarget.addTask(true, "Example").addSubTask("ExampleSubTask");

        Assert.assertNull(clientTarget.getGuid());
        Assert.assertEquals("Test", clientTarget.getName());
        Assert.assertEquals(1, clientTarget.getAllTask().size());
        Assert.assertEquals("Example", clientTarget.getAllTask().get(0).getNameTask());
        Assert.assertTrue(clientTarget.getAllTask().get(0).isCompleted());
        Assert.assertEquals(1, clientTarget.getAllTask().get(0).getSubTask().size());
        Assert.assertEquals("ExampleSubTask", clientTarget.getAllTask().get(0).getSubTask().get(0).getNameTask());
        Assert.assertFalse(clientTarget.getAllTask().get(0).getSubTask().get(0).isCompleted());

        Assert.assertEquals(Resource.getJsonFile("notes/simpleTestCreateTargetStructure.JSON"), clientTarget.toString());
    }
}
