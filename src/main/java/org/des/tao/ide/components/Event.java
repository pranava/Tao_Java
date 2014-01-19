package org.des.tao.ide.components;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.des.tao.ide.editors.Editor;
import org.des.tao.ide.editors.EventEditor;
import org.des.tao.ide.resources.Colors;
import org.des.tao.ide.resources.Fonts;
import org.des.tao.ide.resources.Strokes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class Event implements GraphComponent, Comparable<Event> {
    private static final double HEIGHT = 30;
    private static final double RADIUS = 8;
    private static final double PADDING = 15;

    private Integer instanceId;
    private EventState eventState;
    private transient RoundRectangle2D boundingBox;
    private transient EventEditor eventEditor;

                                                                private transient EnumMap<State, BufferedImage> eventImageMap;

    public Event(int instanceId, Point2D center) {
        this("E" + instanceId, instanceId, center);
    }

    public Event(String name, int instanceId, Point2D center) {
        this.instanceId = instanceId;
        this.eventState = new EventState(name, center);
        this.eventImageMap = Maps.newEnumMap(State.class);
        this.eventEditor = new EventEditor(eventState) {
            @Override
            public void commitCallback() {
                redrawImage();
            }
        };

        redrawImage();
    }

    public void move(double deltaX, double deltaY) {
        Point2D center = eventState.getCenter();
        center.setLocation(
                center.getX() + deltaX,
                center.getY() + deltaY);
        generateBoundingBox(center);
    }

    public RoundRectangle2D getBoundingBox() {
        return this.boundingBox;
    }

    public EventState getEventState() {
        return eventState;
    }

    @Override
    public Editor getEditor() {
        return eventEditor;
    }

    @Override
    public boolean containsPoint(Point2D point) {
        return boundingBox.contains(point);
    }

    @Override
    public boolean intersectsWith(Rectangle2D region) {
        return region.intersects(boundingBox.getFrame());
    }

    @Override
    public void draw(Graphics2D g, State state) {
        Point2D center = eventState.getCenter();
        BufferedImage eventImage = eventImageMap.get(state);
        int imageX = (int) (center.getX() - (eventImage.getWidth() / 2.0));
        int imageY = (int) (center.getY() - (eventImage.getHeight() / 2.0));

        g.drawImage(eventImage, null, imageX, imageY);
    }

    private void generateBoundingBox(Point2D center) {
        BufferedImage eventImage = eventImageMap.get(State.NORMAL);
        boundingBox = new RoundRectangle2D.Double(
                center.getX() - eventImage.getWidth() / 2.0,
                center.getY() - eventImage.getHeight() / 2.0,
                eventImage.getWidth(), eventImage.getHeight(), RADIUS, RADIUS);
    }

    private void redrawImage() {
        Font font = Fonts.DIALOG;
        String name = eventState.getName();
        Point2D center = eventState.getCenter();
        Rectangle2D stringBounds =
                font.getStringBounds(name, Fonts.DEFAULT_RENDER_CONTEXT);
        LineMetrics lineMetrics =
                font.getLineMetrics(name, Fonts.DEFAULT_RENDER_CONTEXT);

        int height = (int) HEIGHT;
        int width = Math.max(
                (int) (stringBounds.getWidth() + PADDING), height);

        for (State state : State.values()) {
            Color backgroundColor;
            switch(state) {
                case NORMAL:
                    backgroundColor = Colors.EVENT_NORMAL;
                    break;
                case SELECTED:
                    backgroundColor = Colors.EVENT_SELECTED;
                    break;
                default:
                    backgroundColor = Colors.EVENT_NORMAL;
                    break;
            }

            BufferedImage eventImage = new BufferedImage(
                    width + 2, height + 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) eventImage.getGraphics();
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            RoundRectangle2D eventShape = new RoundRectangle2D.Double(
                    1, 1, width, height, RADIUS, RADIUS);
            g.setColor(backgroundColor);
            g.fill(eventShape);

            g.setColor(Color.BLACK);
            g.setStroke(Strokes.BORDER_STROKE);
            g.draw(eventShape);

            double textX = (eventShape.getWidth() - stringBounds.getWidth()) / 2 + 1;
            double textY = (eventShape.getHeight() - stringBounds.getHeight()) / 2
                    + lineMetrics.getAscent() + 1;
            g.setFont(font);
            g.drawString(name, (float) textX, (float) textY);
            g.dispose();

            eventImageMap.put(state, eventImage);
        }

        generateBoundingBox(center);
    }

    @Override
    public int compareTo(Event event) {
        return instanceId.compareTo(event.instanceId);
    }

    public class EventState {
        private Point2D center;
        private String name;
        private String description =
                "Provide your event description here.";
        private boolean isTraced = true;
        private String functionBody =
                "() = {\n    // Insert body here.\n}";

        public EventState(String name, Point2D center) {
            this.name = name;
            this.center = center;
        }

        public Point2D getCenter() {
            return center;
        }

        public void setCenter(Point2D center) {
            this.center = center;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isTraced() {
            return isTraced;
        }

        public void setTraced(boolean traced) {
            isTraced = traced;
        }

        public String getFunctionBody() {
            return functionBody;
        }

        public void setFunctionBody(String functionBody) {
            this.functionBody = functionBody;
        }
    }

    public static class Function {
        private static Pattern PARAMETERS_REGEX = Pattern.compile(
                "(?s)(\\w+)(<.*>)?\\s+(\\w+)");
        private static Pattern FUNCTION_REGEX = Pattern.compile(
                "(?s)\\s*\\((.*)\\)\\s*=\\s*\\{(.*)\\}\\s*");

        private String function;
        private List<Map<String, Object>> functionParameters;
        private String functionBody;

        public Function(String function) {
            this.function = function;
            this.functionParameters = Lists.newLinkedList();
        }

        public void process() {
            Matcher functionMatcher = FUNCTION_REGEX.matcher(function);
            if (functionMatcher.find()) {
                functionParameters.clear();
                String parsedParameters = functionMatcher.group(1);
                String parsedBody = functionMatcher.group(2);

                Matcher parameterMatcher = PARAMETERS_REGEX.matcher(parsedParameters);
                while (parameterMatcher.find()) {
                    Map<String, Object> parameterProperties = Maps.newHashMap();
                    parameterProperties.put("class", parameterMatcher.group(1));
                    parameterProperties.put("name", parameterMatcher.group(3));

                    if (parameterMatcher.group(2) != null) {
                        parameterProperties.put("type", parameterMatcher.group(2));
                    }

                    functionParameters.add(parameterProperties);
                }

                functionBody = parsedBody.trim().replaceAll("\n    ", "\n        ");
            }
        }

        public List<Map<String, Object>> getParameters() {
            return functionParameters;
        }

        public String getBody() {
            return functionBody;
        }
    }
}
