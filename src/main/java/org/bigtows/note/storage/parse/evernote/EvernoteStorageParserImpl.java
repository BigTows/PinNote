/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.storage.parse.evernote;

import org.bigtows.note.evernote.EvernoteNotes;
import org.bigtows.note.evernote.EvernoteSubTask;
import org.bigtows.note.evernote.EvernoteTarget;
import org.bigtows.note.evernote.EvernoteTask;
import org.bigtows.note.storage.parse.EvernoteStorageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EvernoteStorageParserImpl implements EvernoteStorageParser {

    private final Logger logger = Logger.getLogger("[PinNote] Evernote parser");


    @Override
    public EvernoteTarget parseTarget(EvernoteNotes notes, String content) {
        EvernoteTarget noteTarget = new EvernoteTarget(notes, "REPLACE_PLEASE");
        try {
            List<TaskContext> contexts = this.parseHtml(Jsoup.parse(content));
            for (TaskContext context : contexts) {
                EvernoteTask task;
                if (context.hasUniqueId()) {
                    task = noteTarget.addTask(context.getUniqueId(), context.isCompleted(), context.getName().trim());
                } else {
                    task = noteTarget.addTask(context.isCompleted(), context.getName().trim());
                }
                for (SubTaskContext subTaskContext : context.getSubTaskContexts()) {
                    if (subTaskContext.hasUniqueId()) {
                        task.addSubTask(subTaskContext.getUniqueId(), subTaskContext.isCompleted(), subTaskContext.getName().trim());
                    } else {
                        task.addSubTask(subTaskContext.isCompleted(), subTaskContext.getName().trim());
                    }

                }
            }
        } catch (Exception e) {
            logger.warning("Parse Failed: " + e.getMessage());
            noteTarget = null;
        }
        return noteTarget;
    }

    @Override
    public String parseTarget(EvernoteTarget target) {
        StringBuilder builder = new StringBuilder();

        builder.append("<ul>");

        for (EvernoteTask task : target.getAllTask()) {

            builder.append("<li title=\"").append(task.getUniqueId()).append("\">");
            if (task.isCompleted()) {
                builder.append("<en-todo checked=\"true\"/>").append(task.getNameTask()).append("\n");
            } else {
                builder.append("<en-todo/>").append(task.getNameTask()).append("\n");
            }


            if (task.getSubTask().size() > 0) {
                builder.append("<ul>");

                for (EvernoteSubTask subTask : task.getSubTask()) {

                    builder.append("<li title=\"").append(subTask.getUniqueId()).append("\">");
                    if (subTask.isCompleted()) {
                        builder.append("<en-todo checked=\"true\"/>").append(subTask.getNameTask()).append("\n");
                    } else {
                        builder.append("<en-todo/>").append(subTask.getNameTask()).append("\n");
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
                    this.prepareSubTask((Element) taskNodeChildren, context);
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
}
