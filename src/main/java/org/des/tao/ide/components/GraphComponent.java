package org.des.tao.ide.components;

import org.des.tao.ide.editors.Editor;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public interface GraphComponent {
    public enum State { NORMAL, SELECTED }

    public void draw(Graphics2D g2, State state);
    public boolean containsPoint(Point2D point);
    public boolean intersectsWith(Rectangle2D region);

    public Editor getEditor();
}
