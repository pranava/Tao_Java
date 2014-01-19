package org.des.tao.ide;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.des.tao.ide.components.Edge;
import org.des.tao.ide.components.Event;
import org.des.tao.ide.components.GraphComponent;
import org.des.tao.ide.editors.Editor;
import org.des.tao.ide.resources.Colors;
import org.des.tao.ide.utilities.GeometryUtilities;

import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
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

public class EventRelationshipGraph extends InteractiveGraph {
    private static final int FRAME_RATE = 60;
    private static final int REFRESH_INTERVAL = 1000 / FRAME_RATE;
    private static final double NAVIGATION_ACCELERATION = 0.01d / 1000;
    private static final double MAXIMUM_VELOCITY = 30d / 1000;

    private transient double navigationVelocity = 0;
    private transient Set<GraphComponent> selectedComponents;
    private transient Event sourceEvent;

    private int eventCount = 0;
    private Set<Event> events;
    private Table<Event, Event, List<Edge>> adjacencyList;

    private Event run;

    public EventRelationshipGraph() {
        super();

        events = Sets.newLinkedHashSet();
        selectedComponents = Sets.newLinkedHashSet();
        adjacencyList = HashBasedTable.create();

        run = new Event("Run", eventCount++, new Point2D.Double(50, 50));
        events.add(run);

        Timer frameTimer = new Timer(REFRESH_INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                repaint();
            }
        });

        frameTimer.setRepeats(true);
        frameTimer.start();
    }

    private void addEvent(Point location) {
        Event event = new Event(eventCount++, location);
        events.add(event);
    }

    private void addEdge(Event source, Event target) {
        Edge edge = new Edge(source, target);
        List<Edge> edgeList = adjacencyList.get(source, target);
        if (edgeList == null) {
            edgeList = Lists.newLinkedList();
            adjacencyList.put(source, target, edgeList);
        }
        edgeList.add(edge);
    }

    private void deleteEvent(Event event) {
        if (!event.equals(run)) {
            events.remove(event);
            adjacencyList.row(event).clear();
            adjacencyList.column(event).clear();
        }
    }

    private void deleteEdge(Edge edge) {
        Event source = edge.getSource();
        Event target = edge.getTarget();

        List<Edge> edgeList = adjacencyList.get(source, target);
        if (edgeList != null) {
            edgeList.remove(edge);
        }
    }

    private <T> T findComponent(
            Point point, Class<T> componentClass) {
        if (componentClass.equals(Event.class) ||
                componentClass.equals(GraphComponent.class)) {
            for (Event event : events) {
                if (event.containsPoint(point)) {
                    return (T)event;
                }
            }
        }

        if (componentClass.equals(Edge.class) ||
                componentClass.equals(GraphComponent.class)) {
            for (List<Edge> edges : adjacencyList.values()) {
                for (Edge edge : edges) {
                    if (edge.containsPoint(point)) {
                        return (T)edge;
                    }
                }
            }
        }

        return null;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public Table<Event, Event, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    public void selectAllComponents() {
        selectedComponents.clear();
        selectedComponents.addAll(events);
    }

    public void removeSelectedComponents() {
        for (GraphComponent component : selectedComponents) {
            if (component.getClass() == Event.class) {
                deleteEvent((Event) component);
            } else if (component.getClass() == Edge.class) {
                deleteEdge((Edge) component);
            }
        }

        selectedComponents.clear();
    }

    private void drawGraphComponent(Graphics2D g, GraphComponent graphComponent) {
        if (selectedComponents.contains(graphComponent)) {
            graphComponent.draw(g, GraphComponent.State.SELECTED);
        } else {
            graphComponent.draw(g, GraphComponent.State.NORMAL);
        }
    }

    @Override
    public void paintComponent(Graphics2D g) {
        moveEvents();

        for (List<Edge> edges : adjacencyList.values()) {
            if (!edges.isEmpty()) {
                Edge edge = edges.get(0);
                Event sourceEvent = edge.getSource();
                Event targetEvent = edge.getTarget();
                List<Edge> reverseEdge = adjacencyList.get(targetEvent, sourceEvent);

                edge.setOffsetDirection(0);
                if (reverseEdge != null && !reverseEdge.isEmpty()) {
                    double offsetDirection =
                            sourceEvent.compareTo(targetEvent);
                    edge.setOffsetDirection(offsetDirection);
                }

                drawGraphComponent(g, edge);
            }
        }

        if (action == Action.DRAW_EDGE) {
            Point2D source = sourceEvent.getEventState().getCenter();
            Point2D dragEnd = drag.getDragEnd();
            double angle = GeometryUtilities.getAngle(source, dragEnd);

            g.draw(new Line2D.Double(source, dragEnd));
            Edge.drawArrowHead(g, dragEnd, angle);
        }

        for (Event event : events) {
            drawGraphComponent(g, event);
        }

        if (action == Action.SELECT) {
            g.setColor(Colors.SELECT_FILL);
            g.fill(drag.getDragRegion());
            g.setColor(Colors.SELECT_OUTLINE);
            g.draw(drag.getDragRegion());
        }
    }

    @Override
    public void onSingleMouseClick(MouseEvent mouseEvent) {
        Point point = mouseEvent.getPoint();
        GraphComponent component = findComponent(
                point, GraphComponent.class);

        if (component != null) {
            selectedComponents.clear();
            selectedComponents.add(component);
        } else if (!selectedComponents.isEmpty()) {
            selectedComponents.clear();
        }

        action = Action.NONE;
    }

    @Override
    public void onDoubleMouseClick(MouseEvent mouseEvent) {
        Point point = mouseEvent.getPoint();
        GraphComponent component = findComponent(
                point, GraphComponent.class);

        if (component == null) {
            addEvent(point);
        } else {
            Editor editor = component.getEditor();
            Dimension dimension = editor.getDimension();
            editor.revert();

            if (dimension != null) {
                editor.setPreferredSize(dimension);
            }
            editor.pack();
            editor.setVisible(true);
        }
    }

    @Override
    public void onMousePress(MouseEvent mouseEvent) {
        GraphComponent component = findComponent(
                mouseEvent.getPoint(), GraphComponent.class);
        if (component != null) {
            if (selectedComponents.isEmpty() &&
                    component.getClass() == Event.class) {
                action = Action.DRAW_EDGE;
                sourceEvent = (Event) component;
            } else {
                if (selectedComponents.contains(component)) {
                    action = Action.DRAG_EVENT;
                }
            }
        } else {
            action = Action.SELECT;
        }
    }

    @Override
    public void onMouseDrag(MouseEvent mouseEvent) {
        if (action == Action.DRAG_EVENT) {
            Point2D dragDelta = drag.getInstantaneousDelta();
            double deltaX = dragDelta.getX();
            double deltaY = dragDelta.getY();

            for (GraphComponent component : selectedComponents) {
                if (component.getClass() == Event.class) {
                    Event event = (Event) component;
                    event.move(deltaX, deltaY);
                }
            }
        } else if (action == Action.SELECT) {
            selectedComponents.clear();
            Rectangle2D selectionRectangle = drag.getDragRegion();

            for (GraphComponent component : events) {
                if (component.intersectsWith(selectionRectangle)) {
                    selectedComponents.add(component);
                }
            }
        }
    }

    @Override
    public void onMouseRelease(MouseEvent mouseEvent) {
        if (action == Action.DRAW_EDGE) {
            Event targetEvent = findComponent(
                    mouseEvent.getPoint(), Event.class);
            if (targetEvent != null) {
                addEdge(sourceEvent, targetEvent);
            }
        }

        action = Action.NONE;
    }

    private void moveEvents() {
        Point2D navigationVector = keyboard.getDirection();
        double navigationX = navigationVector.getX();
        double navigationY = navigationVector.getY();

        if (navigationX == 0 && navigationY == 0) {
            navigationVelocity = 0;
            return;
        }

        if (navigationVelocity < MAXIMUM_VELOCITY) {
            navigationVelocity += NAVIGATION_ACCELERATION * FRAME_RATE;
        }

        if (navigationVelocity > MAXIMUM_VELOCITY) {
            navigationVelocity = MAXIMUM_VELOCITY;
        }

        double deltaX = navigationVelocity * navigationX * FRAME_RATE;
        double deltaY = navigationVelocity * navigationY * FRAME_RATE;
        for (GraphComponent component : selectedComponents) {
            if (component.getClass() == Event.class) {
                Event event = (Event) component;
                event.move(deltaX, deltaY);
            }
        }
    }
}