package org.des.tao.ide.utilities;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class RectangleUtilities {
    public static Rectangle2D generateRectangle(Point2D p1, Point2D p2) {
        double startX = Math.min(p1.getX(), p2.getX());
        double startY = Math.min(p1.getY(), p2.getY());
        double endX = Math.max(p1.getX(), p2.getX());
        double endY = Math.max(p1.getY(), p2.getY());

        return new Rectangle2D.Double(
                startX, startY, endX - startX, endY - startY);
    }

    public static Rectangle2D generateCenteredRectangle(
            Point2D center, double width, double height) {
        double startX = center.getX() - width / 2;
        double startY = center.getY() - height / 2;

        return new Rectangle2D.Double(
                startX, startY, width, height);
    }

    private static double intersectHorizontal(
            double y, double slope, double offset) {
        return (y - offset) / slope;
    }

    private static double intersectVertical(
            double x, double slope, double offset) {
        return slope * x + offset;
    }

    public static Point2D interceptLineAndBox(
            Point2D startPosition, Point2D endPosition,
            RectangularShape boundingBox) {
        Point2D intercept = new Point2D.Double();

        if (startPosition.getX() == endPosition.getX()) {
            if (startPosition.getY() > endPosition.getY()) {
                intercept.setLocation(startPosition.getX(), boundingBox.getMaxY());
            } else {
                intercept.setLocation(startPosition.getX(), boundingBox.getMinY());
            }
        } else {
            double slope =
                    (endPosition.getY() - startPosition.getY()) /
                            (endPosition.getX() - startPosition.getX());
            double offset = endPosition.getY() - slope * endPosition.getX();
            double rightX = boundingBox.getMaxX();
            double leftX = boundingBox.getMinX();

            if (startPosition.getY() > endPosition.getY()) {
                double bottomY = boundingBox.getMaxY();
                double bottomX = intersectHorizontal(bottomY, slope, offset);

                if (bottomX > rightX) {
                    intercept.setLocation(rightX, intersectVertical(rightX, slope, offset));
                } else if (bottomX < leftX) {
                    intercept.setLocation(leftX, intersectVertical(leftX, slope, offset));
                } else {
                    intercept.setLocation(bottomX, bottomY);
                }
            } else if (startPosition.getY() < endPosition.getY()) {
                double topY = boundingBox.getMinY();
                double topX = intersectHorizontal(topY, slope, offset);

                if (topX > rightX) {
                    intercept.setLocation(rightX, intersectVertical(rightX, slope, offset));
                } else if (topX < leftX) {
                    intercept.setLocation(leftX, intersectVertical(leftX, slope, offset));
                } else {
                    intercept.setLocation(topX, topY);
                }
            } else {
                if (startPosition.getX() > rightX) {
                    intercept.setLocation(rightX, startPosition.getY());
                } else {
                    intercept.setLocation(leftX, startPosition.getY());
                }
            }
        }

        return intercept;
    }
}
