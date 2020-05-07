package org.bigtows.window.ui.notetree.factory;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import lombok.SneakyThrows;
import org.bigtows.service.note.notebook.evernote.EvernoteNote;
import org.bigtows.service.note.notebook.evernote.EvernoteNotebook;
import org.bigtows.service.note.notebook.evernote.EvernoteSubTask;
import org.bigtows.service.note.notebook.evernote.EvernoteTask;
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

public class EvernoteNoteTreeFactory {

    public static NoteTree buildNoteTreeForEvernote(Project project, EvernoteNotebook evernoteNotebook) {
        var noteTree = new NoteTree(buildTreeNodeByNoteBook(evernoteNotebook.getAllNotes()));
        var timer = new EditorTimer(() -> runSync(project, evernoteNotebook, noteTree));
        noteTree.addTreeChangeListener(timer::editing);
        noteTree.addNeedUpdateModelListener(() -> {
            timer.end();
            runSync(project, evernoteNotebook, noteTree);
        });
        return noteTree;
    }

    private static void runSync(Project project, EvernoteNotebook evernoteNotebook, NoteTree noteTree) {
        ProgressManager.getInstance().run(new com.intellij.openapi.progress.Task.Backgroundable(project, "Upload evernote.") {
            @SneakyThrows
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                noteTree.lockTree();
                var newNotes = evernoteNotebook.updateNotes(
                        buildListEvernoteNoteByMutableTreeNote(noteTree.getMutableTreeNodeList())
                );
                noteTree.updateModel(
                        buildTreeNodeByNoteBook(newNotes)
                );
                noteTree.unlockTree();
            }
        });
    }

    private static List<MutableTreeNode> buildTreeNodeByNoteBook(List<EvernoteNote> evernoteNotes) {

        return evernoteNotes.stream().map(evernoteNote -> {
            var noteTree = new NoteTreeNode(Note.builder()
                    .identity(evernoteNote.getId())
                    .name(evernoteNote.getName())
                    .build());
            evernoteNote.getTasks().stream().map(evernoteTask -> {
                var taskTreeNode = new TaskTreeNode(Task.builder()
                        .identity(evernoteTask.getId())
                        .text(evernoteTask.getName())
                        .checked(evernoteTask.isChecked())
                        .build());
                evernoteTask.getSubTask().stream().map(evernoteSubTask ->
                        new SubTaskTreeNode(Task.builder()
                                .identity(evernoteSubTask.getId())
                                .text(evernoteSubTask.getName())
                                .checked(evernoteSubTask.isChecked())
                                .build()
                        )).forEach(taskTreeNode::add);
                return taskTreeNode;
            }).forEach(noteTree::add);
            return noteTree;
        }).collect(Collectors.toList());
    }

    private static List<EvernoteNote> buildListEvernoteNoteByMutableTreeNote(List<MutableTreeNode> nodes) {

        List<EvernoteNote> result = new ArrayList<>();

        nodes.forEach(node -> {
            if (node instanceof NoteTreeNode) {
                var userObject = ((NoteTreeNode) node).getUserObject();
                var note = EvernoteNote.builder()
                        .name(userObject.getName())
                        .id(userObject.getIdentity())
                        .build();
                node.children().asIterator().forEachRemaining(nodeChild -> {
                    if (nodeChild instanceof TaskTreeNode) {
                        var taskUserObject = ((TaskTreeNode) nodeChild).getUserObject();
                        var task = EvernoteTask.builder()
                                .id(taskUserObject.getIdentity())
                                .name(taskUserObject.getText())
                                .checked(taskUserObject.getChecked())
                                .build();
                        ((TaskTreeNode) nodeChild).children().asIterator().forEachRemaining(subTaskNode -> {
                            if (subTaskNode instanceof SubTaskTreeNode) {
                                var subTaskUserObject = ((SubTaskTreeNode) subTaskNode).getUserObject();
                                var subTask = EvernoteSubTask.builder()
                                        .id(subTaskUserObject.getIdentity())
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
