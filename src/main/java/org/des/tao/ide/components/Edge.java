package org.des.tao.ide.components;

import org.des.tao.ide.editors.EdgeEditor;
import org.des.tao.ide.editors.Editor;
import org.des.tao.ide.resources.Colors;
import org.des.tao.ide.utilities.GeometryUtilities;
import org.des.tao.ide.utilities.RectangleUtilities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class Edge implements GraphComponent {
    private static int ARROW_HEIGHT = 9;
    private static int ARROW_WIDTH = 12;
    private static int SELF_LOOP_DIAMETER = 50;
    private static int CLICK_TOLERANCE = 7;
    private static double ARROW_TIP_ANGLE =
            Math.atan2(ARROW_HEIGHT / 2.0, ARROW_WIDTH);
    private static Polygon ARROW_HEAD;

    private Event source;
    private Event target;
    private Shape edgeLine;
    private double offsetDirection;
    private transient EdgeEditor edgeEditor;

    static {
        ARROW_HEAD = new Polygon();
        ARROW_HEAD.addPoint(0, 0);
        ARROW_HEAD.addPoint(-ARROW_WIDTH,  ARROW_HEIGHT / 2);
        ARROW_HEAD.addPoint(-ARROW_WIDTH, -ARROW_HEIGHT / 2);
    }

    public Edge(Event source, Event target) {
        this.source = source;
        this.target = target;
        this.offsetDirection = 0;

        generateEdgeLine();
        this.edgeEditor = new EdgeEditor();
    }

    public void setOffsetDirection(double offsetDirection) {
        this.offsetDirection = offsetDirection;
    }

    public Event getSource() {
        return source;
    }

    public Event getTarget() {
        return target;
    }

    public EdgeEditor getEdgeEditor() {
        return edgeEditor;
    }

    @Override
    public Editor getEditor() {
        return getEdgeEditor();
    }

    @Override
    public boolean containsPoint(Point2D point) {
        Rectangle2D toleranceRegion =
                RectangleUtilities.generateCenteredRectangle(
                        point, CLICK_TOLERANCE, CLICK_TOLERANCE);
        return edgeLine.intersects(toleranceRegion);
    }

    @Override
    public boolean intersectsWith(Rectangle2D region) {
        return edgeLine.intersects(region);
    }

    @Override
    public void draw(Graphics2D g, State state) {
        Point2D symbolPosition = generateEdgeLine();
        Point2D startPosition = source.getEventState().getCenter();
        Point2D endPosition = target.getEventState().getCenter();
        RoundRectangle2D boundingBox = target.getBoundingBox();
        Point2D intercept;

        double arrowAngle;
        double symbolAngle;

        if (startPosition.equals(endPosition)) {
            double radius = SELF_LOOP_DIAMETER / 2;
            double yPos = endPosition.getY() - SELF_LOOP_DIAMETER;
            double centerX = endPosition.getX();
            double centerY = yPos + radius;

            if (boundingBox.getWidth() > SELF_LOOP_DIAMETER) {
                double arrowY = boundingBox.getMinY();
                double arrowX = centerX - Math.sqrt(Math.pow(radius, 2) - Math.pow((arrowY - centerY), 2));
                intercept = new Point.Double(arrowX, arrowY);
            } else {
                double arrowX = boundingBox.getMinX();
                double arrowY = Math.sqrt(Math.pow(radius, 2) - Math.pow((arrowX - centerX), 2)) + centerY;
                intercept = new Point.Double(arrowX, arrowY);
            }

            symbolAngle = 0;
            arrowAngle = GeometryUtilities.getAngle(
                    new Point2D.Double(centerX, centerY), intercept) -
                    GeometryUtilities.HALF_PI + ARROW_TIP_ANGLE;
        } else {
            arrowAngle = GeometryUtilities.getAngle(startPosition, endPosition);
            symbolAngle = arrowAngle;

            if (offsetDirection != 0) {
                double normalizedAngle = (arrowAngle + Math.PI) % Math.PI;
                double offsetAngle = normalizedAngle + offsetDirection * GeometryUtilities.HALF_PI;

                Point2D offsetStartPosition =
                        GeometryUtilities.translatePoint(startPosition, offsetAngle, 5);
                Point2D offsetEndPosition =
                        GeometryUtilities.translatePoint(endPosition, offsetAngle, 5);
                symbolPosition =
                        GeometryUtilities.translatePoint(symbolPosition, offsetAngle, 5);
                intercept = RectangleUtilities.interceptLineAndBox(
                        offsetStartPosition, offsetEndPosition, boundingBox);
                ((Line2D)edgeLine).setLine(offsetStartPosition, offsetEndPosition);
            } else {
                intercept = RectangleUtilities.interceptLineAndBox(
                        startPosition, endPosition, boundingBox);
            }
        }

        Color backgroundColor;
        switch(state) {
            case NORMAL:
                backgroundColor = Colors.EDGE_NORMAL;
                break;
            case SELECTED:
                backgroundColor = Colors.EDGE_SELECTED;
                break;
            default:
                backgroundColor = Colors.EVENT_NORMAL;
                break;
        }

        g.setColor(backgroundColor);
        g.draw(edgeLine);
        drawEdgeSymbol(g, symbolPosition, symbolAngle);
        drawArrowHead(g, intercept, arrowAngle);
    }

    public static void drawArrowHead(
            Graphics2D g, Point2D arrowPosition, double angle) {
        Graphics2D localGraphics = transformGraphics(g, arrowPosition, angle);
        localGraphics.fill(ARROW_HEAD);
        localGraphics.dispose();
    }

    private void drawEdgeSymbol(
            Graphics2D g, Point2D symbolPosition, double angle) {
        EdgeType edgeType = edgeEditor.getEdgeType();
        if (edgeType == EdgeType.SCHEDULING) return;

        Graphics2D localGraphics = transformGraphics(g, symbolPosition, angle);
        Color graphicsColor = localGraphics.getColor();
        if (edgeType == EdgeType.CANCELLING) {
            localGraphics.drawLine(-5, -5, 5, 5);
            localGraphics.drawLine(-5, 5, 5, -5);
        } else if (edgeType == EdgeType.PENDING) {
            localGraphics.setColor(Colors.ERG_BACKGROUND);
            localGraphics.fillRect(-3, -5, 6, 10);

            localGraphics.setColor(graphicsColor);
            localGraphics.drawLine(-3, -5, -3, 5);
            localGraphics.drawLine(3, -5, 3, 5);
        }

        localGraphics.dispose();
    }

    private static Graphics2D transformGraphics(
            Graphics2D g, Point2D position, double angle) {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToIdentity();
        affineTransform.translate(position.getX(), position.getY());
        affineTransform.rotate(angle);

        Graphics2D localGraphics = (Graphics2D) g.create();
        localGraphics.setTransform(affineTransform);

        return localGraphics;
    }

    private Point2D generateEdgeLine() {
        Point2D symbolPosition;
        Point2D startPosition = source.getEventState().getCenter();
        Point2D endPosition = target.getEventState().getCenter();

        if (startPosition.equals(endPosition)) {
            double radius = SELF_LOOP_DIAMETER / 2;
            double xPos = endPosition.getX() - radius;
            double yPos = endPosition.getY() - SELF_LOOP_DIAMETER;

            edgeLine = new Ellipse2D.Double(
                    xPos, yPos, SELF_LOOP_DIAMETER, SELF_LOOP_DIAMETER);
            symbolPosition = new Point2D.Double(endPosition.getX(), yPos);
        } else {
            edgeLine = new Line2D.Double(startPosition, endPosition);
            symbolPosition =
                    GeometryUtilities.getMidpoint(startPosition, endPosition);
        }

        return symbolPosition;
    }

    public enum EdgeType {
        SCHEDULING("Scheduling"),
        PENDING("Pending"),
        CANCELLING("Cancelling");

        private final String displayName;

        private EdgeType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
