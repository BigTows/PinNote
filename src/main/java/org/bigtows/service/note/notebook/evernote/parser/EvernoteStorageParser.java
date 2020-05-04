package org.bigtows.service.note.notebook.evernote.parser;

import com.google.common.hash.Hashing;
import org.bigtows.service.note.notebook.evernote.EvernoteNote;
import org.bigtows.service.note.notebook.evernote.EvernoteNotebook;
import org.bigtows.service.note.notebook.evernote.EvernoteSubTask;
import org.bigtows.service.note.notebook.evernote.EvernoteTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class EvernoteStorageParser {

    private final Logger logger = Logger.getLogger("[PinNote] Evernote parser");


    public EvernoteNote parseTarget(EvernoteNotebook notebook, String content) {
        EvernoteNote noteTarget = EvernoteNote.builder()
                .name("newNote")
                .build();
        try {
            List<TaskContext> contexts = this.parseHtml(Jsoup.parse(content));
            for (TaskContext context : contexts) {
                String id = context.hasUniqueId() ? context.getUniqueId() :
                        this.getUniqueId(context.getName(), String.valueOf(context.isCompleted()));

                EvernoteTask task = EvernoteTask.builder()
                        .id(id)
                        .name(context.getName())
                        .checked(context.isCompleted())
                        .build();


                for (SubTaskContext subTaskContext : context.getSubTaskContexts()) {
                    String idSubTask = subTaskContext.hasUniqueId() ? subTaskContext.getUniqueId()
                            : this.getUniqueId(subTaskContext.getName(), String.valueOf(subTaskContext.isCompleted()));
                    EvernoteSubTask subTask = EvernoteSubTask.builder()
                            .id(idSubTask)
                            .name(subTaskContext.getName())
                            .checked(subTaskContext.isCompleted())
                            .build();
                    task.getSubTask().add(subTask);
                }
                noteTarget.getTasks().add(task);
            }
        } catch (Exception e) {
            logger.warning("Parse Failed: " + e.getMessage());
            noteTarget = null;
        }
        return noteTarget;
    }

    public String parseTarget(EvernoteNote note) {
        StringBuilder builder = new StringBuilder();

        builder.append("<ul>");

        for (EvernoteTask task : note.getTasks()) {

            builder.append("<li title=\"").append(task.getId()).append("\">");
            if (task.isChecked()) {
                builder.append("<en-todo checked=\"true\"/>").append(task.getName()).append("\n");
            } else {
                builder.append("<en-todo/>").append(task.getName()).append("\n");
            }


            if (task.getSubTask().size() > 0) {
                builder.append("<ul>");

                for (EvernoteSubTask subTask : task.getSubTask()) {

                    builder.append("<li title=\"").append(subTask.getId()).append("\">");
                    if (subTask.isChecked()) {
                        builder.append("<en-todo checked=\"true\"/>").append(subTask.getName()).append("\n");
                    } else {
                        builder.append("<en-todo/>").append(subTask.getName()).append("\n");
                    }
                    builder.append("</li>");
                }


                builder.append("</ul>");
            }
            builder.append("</li>");
        }


        builder.append("</ul>");

        return this.getDefaultContent(builder.toString());
    }

    /**
     * Parse html
     *
     * @param parse document
     * @return list Context
     */
    private List<TaskContext> parseHtml(Document parse) {

        List<TaskContext> taskContextList = new ArrayList<>();
        Element element = parse.body().child(0);
        if (element.childNodeSize() == 0) {
            return taskContextList;
        }
        element = element.child(0);
        TaskContext context = null;
        for (Node taskNode : element.childNodes()) {
            String uniqueId = taskNode.attr("title");
            if (((Element) taskNode.childNodes().get(0)).tag().getName().equals("div")) {
                taskNode = taskNode.childNodes().get(0);
            }
            for (Node taskNodeChildren : taskNode.childNodes()) {
                if (taskNodeChildren instanceof Element &&
                        ((Element) taskNodeChildren).tag().toString().equals("en-todo")) {
                    context = new TaskContext();
                    context.setUniqueId(uniqueId);
                    if (taskNodeChildren.attr("checked").equals("true")) {
                        context.setCompleted(true);
                    } else {
                        context.setCompleted(false);
                    }
                } else if (taskNodeChildren instanceof TextNode) {
                    if (context.getName() == null) {
                        context.setName(taskNodeChildren.toString().trim());
                    }
                } else if (taskNodeChildren instanceof Element) {
                    Elements elements = ((Element) taskNodeChildren).getElementsByTag("span");
                    if (elements.size() == 1) {
                        //is element with span - text
                        if (context.getName() == null) {
                            context.setName(elements.get(0).text().trim());
                        }
                    } else {
                        this.prepareSubTask((Element) taskNodeChildren, context);
                    }
                }
            }
            if (taskNode.childNodes().size() != 0) {
                taskContextList.add(context);
            }
        }

        return taskContextList;
    }

    /**
     * Prepare sub task Elements
     *
     * @param subTaskElement elements
     * @param taskContext    context
     */
    private void prepareSubTask(Element subTaskElement, TaskContext taskContext) {
        for (Node subTaskNode : subTaskElement.childNodes()) {
            SubTaskContext subTaskContext = new SubTaskContext();
            String uniqueId = subTaskNode.attr("title");
            for (Node subTaskNodeChildren : subTaskNode.childNodes()) {
                if (subTaskNodeChildren instanceof Element && ((Element) subTaskNodeChildren).tag().toString().equals("en-todo")) {
                    subTaskContext.setUniqueId(uniqueId);
                    if (subTaskNodeChildren.attr("checked").equals("true")) {
                        subTaskContext.setCompleted(true);
                    } else {
                        subTaskContext.setCompleted(false);
                    }
                } else if (subTaskNodeChildren instanceof TextNode) {
                    if (subTaskContext.getName() == null) {
                        subTaskContext.setName(subTaskNodeChildren.toString().trim());
                    }
                }
            }
            if (subTaskNode.childNodes().size() != 0) {
                taskContext.addSubTaskContexts(subTaskContext);
            }
        }
    }

    private String getDefaultContent(String... data) {
        StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">"
                + "<en-note>");

        for (String content : data) {
            builder.append(content);
        }
        return builder.append("</en-note>").toString();
    }


    protected String getUniqueId(String... resources) {

        StringBuilder uniqueId = new StringBuilder(String.valueOf(System.currentTimeMillis()));

        for (String data : resources) {
            uniqueId.append(data);
        }
        uniqueId.append(new Random().nextInt(100));

        return this.getHashByStringBuilder(uniqueId);
    }


    private String getHashByStringBuilder(StringBuilder builder) {
        return Hashing.sha256().hashString(
                builder.toString(), Charset.defaultCharset()
        ).toString();
    }
}
