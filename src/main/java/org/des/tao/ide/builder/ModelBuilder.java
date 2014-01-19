package org.des.tao.ide.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.des.tao.ide.components.Edge;
import org.des.tao.ide.components.Event;
import org.des.tao.ide.editors.EventEditor;
import org.des.tao.ide.resources.Templates;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class ModelBuilder {
    private String modelName;
    private Set<Event> events;
    private List<Map<String, Object>> variableList;
    private Table<Event,Event,List<Edge>> adjacencyList;

    public ModelBuilder(String modelName,
                        List<Map<String, Object>> variableList,
                        Set<Event> events,
                        Table<Event, Event, List<Edge>> adjacencyList) {
        this.modelName = modelName;
        this.variableList = variableList;
        this.events = events;
        this.adjacencyList = adjacencyList;
    }

    private void generateCode(Writer modelCodeWriter) throws IOException, TemplateException {
        Map<String, Object> modelProperties = Maps.newHashMap();
        List<Map<String, Object>> eventList = Lists.newLinkedList();
        modelProperties.put("modelName", modelName);
        modelProperties.put("events", eventList);
        modelProperties.put("variables", variableList);

        for (Event event : events) {
            EventBuilder eventBuilder = new EventBuilder();
            Event.EventState eventState = event.getEventState();
            Event.Function eventFunctionMatcher =
                    new Event.Function(eventState.getFunctionBody());
            eventFunctionMatcher.process();

            eventBuilder.setName(eventState.getName());
            eventBuilder.setParameters(eventFunctionMatcher.getParameters());
            eventBuilder.setBody(eventFunctionMatcher.getBody());

            for (Map.Entry<Event, List<Edge>>
                    targetEntry : adjacencyList.row(event).entrySet()) {
                List<Edge> edges = targetEntry.getValue();

                for (Edge edge : edges) {
                    eventBuilder.addEdge(edge);
                }
            }

            eventList.add(eventBuilder.asMap());
        }

        Templates templatesInstance = Templates.getTemplatesInstance();
        Configuration templateConfiguration = templatesInstance.getConfiguration();
        Template modelTemplate = templateConfiguration.getTemplate("Simulation.java.ftl");

        modelTemplate.process(modelProperties, modelCodeWriter);
    }

    public File exportCode() throws IOException, TemplateException {
        String fileName = modelName + ".java";
        File codeFile = new File("tmp/src", fileName);
        FileWriter fileWriter = new FileWriter(codeFile);

        generateCode(fileWriter);
        fileWriter.close();

        return codeFile;
    }

}
