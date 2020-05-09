package org.bigtows.window.ui.notetree.factory;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import lombok.SneakyThrows;
import org.bigtows.notebook.local.LocalNote;
import org.bigtows.notebook.local.LocalNotebook;
import org.bigtows.notebook.local.LocalSubTask;
import org.bigtows.notebook.local.LocalTask;
import org.bigtows.window.ui.notetree.NoteTree;
import org.bigtows.window.ui.notetree.tree.entity.Note;
import org.bigtows.window.ui.notetree.tree.entity.Task;
import org.bigtows.window.ui.notetree.tree.node.NoteTreeNode;
import org.bigtows.window.ui.notetree.tree.node.SubTaskTreeNode;
import org.bigtows.window.ui.notetree.tree.node.TaskTreeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.MutableTreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocalNoteTreeFactory {

    public static NoteTree buildNoteTreeByLocalNotebook(Project project, LocalNotebook localNotebook) {
        var noteTree = new NoteTree(buildTreeNodeByNoteBook(localNotebook.getAllNotes()));
        var timer = new EditorTimer(() -> runSync(project, localNotebook, noteTree));
        noteTree.addTreeChangeListener(timer::editing);
        noteTree.addNeedUpdateModelListener(() -> {
            timer.end();
            runSync(project, localNotebook, noteTree);
        });
        return noteTree;
    }

    private static void runSync(Project project, LocalNotebook localNotebook, NoteTree noteTree) {
        ProgressManager.getInstance().run(new com.intellij.openapi.progress.Task.Backgroundable(project, "Update local notes.") {
            @SneakyThrows
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                localNotebook.updateNotes(
                        buildListEvernoteNoteByMutableTreeNote(noteTree.getMutableTreeNodeList())
                );
                //noteTree.updateModel(buildTreeNodeByNoteBook(newNotes));
            }
        });
    }


    private static List<MutableTreeNode> buildTreeNodeByNoteBook(List<LocalNote> localNotes) {

        return localNotes.stream().map(localNote -> {
            var noteTree = new NoteTreeNode(Note.builder()
                    .name(localNote.getName())
                    .build());
            localNote.getTasks().stream().map(localTask -> {
                var taskTreeNode = new TaskTreeNode(Task.builder()
                        .text(localTask.getName())
                        .checked(localTask.isChecked())
                        .build());
                localTask.getSubTask().stream().map(localSubTask ->
                        new SubTaskTreeNode(Task.builder()
                                .text(localSubTask.getName())
                                .checked(localSubTask.isChecked())
                                .build()
                        )).forEach(taskTreeNode::add);
                return taskTreeNode;
            }).forEach(noteTree::add);
            return noteTree;
        }).collect(Collectors.toList());
    }

    private static List<LocalNote> buildListEvernoteNoteByMutableTreeNote(List<MutableTreeNode> nodes) {

        List<LocalNote> result = new ArrayList<>();

        nodes.forEach(node -> {
            if (node instanceof NoteTreeNode) {
                var userObject = ((NoteTreeNode) node).getUserObject();
                var note = LocalNote.builder()
                        .name(userObject.getName())
                        .build();
                node.children().asIterator().forEachRemaining(nodeChild -> {
                    if (nodeChild instanceof TaskTreeNode) {
                        var taskUserObject = ((TaskTreeNode) nodeChild).getUserObject();
                        var task = LocalTask.builder()
                                .name(taskUserObject.getText())
                                .checked(taskUserObject.getChecked())
                                .build();
                        ((TaskTreeNode) nodeChild).children().asIterator().forEachRemaining(subTaskNode -> {
                            if (subTaskNode instanceof SubTaskTreeNode) {
                                var subTaskUserObject = ((SubTaskTreeNode) subTaskNode).getUserObject();
                                var subTask = LocalSubTask.builder()
                                        .name(subTaskUserObject.getText())
                                        .checked(subTaskUserObject.getChecked())
                                        .build();
                                task.getSubTask().add(subTask);
                            }
                        });
                        note.getTasks().add(task);
                    }
                });
                result.add(note);
            }
        });
        return result;
    }
}
