package org.des.tao.ide.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.des.tao.ide.components.Edge;
import org.des.tao.ide.components.Event;
import org.des.tao.ide.editors.EdgeEditor;

import java.util.List;
import java.util.Map;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class EventBuilder {
    private Map<String, Object> eventMap;
    private List<Map<String, Object>> edgeList;

    public EventBuilder() {
        this.eventMap = Maps.newHashMap();
        this.edgeList = Lists.newLinkedList();
        this.eventMap.put("edges", this.edgeList);
    }

    public void setName(String name) {
        eventMap.put("name", name);
    }

    public void setParameters(List<Map<String, Object>> parameters) {
        eventMap.put("parameters", parameters);
    }

    public void setBody(String body) {
        eventMap.put("body", body);
    }

    public void addEdge(Edge edge) {
        Map<String, Object> edgeProperties = Maps.newHashMap();
        EdgeEditor edgeEditor = edge.getEdgeEditor();
        edgeList.add(edgeProperties);

        Event targetEvent = edge.getTarget();

        edgeProperties.put("condition", edgeEditor.getCondition());
        edgeProperties.put("delay", edgeEditor.getDelay());
        edgeProperties.put("priority", edgeEditor.getPriority());
        edgeProperties.put("event", targetEvent.getEventState().getName());

        String edgeParameters =  edgeEditor.getParameters();
        if (!edgeParameters.equals("")) {
            edgeProperties.put("parameters", edgeEditor.getParameters());
        }
    }

    public Map<String, Object> asMap() {
        return eventMap;
    }
}
