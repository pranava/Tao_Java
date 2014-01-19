package org.des.tao.ide.utilities;

import java.awt.geom.Point2D;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class GeometryUtilities {
    public static double HALF_PI = Math.PI / 2;
    public static double QUARTER_PI = Math.PI / 4;
    public static double THREE_QUARTER_PI = 3 * QUARTER_PI;

    public static double getAngle(Point2D startPosition, Point2D endPosition) {
        double deltaX = endPosition.getX() - startPosition.getX();
        double deltaY = endPosition.getY() - startPosition.getY();

        return Math.atan2(deltaY, deltaX);
    }

    public static Point2D translatePoint(
            Point2D point, double angle, double magnitude) {
        double deltaX = magnitude * Math.cos(angle);
        double deltaY = magnitude * Math.sin(angle);

        return new Point2D.Double(
                point.getX() + deltaX, point.getY() + deltaY);
    }

    public static Point2D getMidpoint(Point2D start, Point2D end) {
        return new Point2D.Double(
                (start.getX() + end.getX()) / 2,
                (start.getY() + end.getY()) / 2);
    }
}
