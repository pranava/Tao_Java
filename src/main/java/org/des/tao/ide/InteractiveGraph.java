package org.des.tao.ide;

import org.des.tao.ide.utilities.RectangleUtilities;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.BitSet;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public abstract class InteractiveGraph extends JPanel
        implements MouseListener, MouseMotionListener, KeyListener {
    protected Drag drag;
    protected Keyboard keyboard;
    protected Action action;

    public InteractiveGraph() {
        drag = new Drag();
        keyboard = new Keyboard();
        action = Action.NONE;

        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        paintComponent(graphics2D);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        return;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        keyboard.addKeyPress(keyEvent.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        keyboard.removeKeyPress(keyEvent.getKeyCode());
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        return;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        Point point = mouseEvent.getPoint();
        drag.startDrag(point);

        onMousePress(mouseEvent);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        Point point = mouseEvent.getPoint();
        drag.updateDrag(point);

        onMouseDrag(mouseEvent);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        drag.releaseDrag();

        if (drag.getDragDeltaCount() == 0) {
            if (mouseEvent.getClickCount() == 1) {
                onSingleMouseClick(mouseEvent);
            } else {
                onDoubleMouseClick(mouseEvent);
            }
        } else {
            onMouseRelease(mouseEvent);
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        return;
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        return;
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        return;
    }

    public abstract void paintComponent(Graphics2D g);
    public abstract void onSingleMouseClick(MouseEvent mouseEvent);
    public abstract void onDoubleMouseClick(MouseEvent mouseEvent);
    public abstract void onMousePress(MouseEvent mouseEvent);
    public abstract void onMouseDrag(MouseEvent mouseEvent);
    public abstract void onMouseRelease(MouseEvent mouseEvent);

    protected enum Action { SELECT, DRAG_EVENT, DRAW_EDGE, NONE }

    protected static class Keyboard {
        private BitSet keyBits;
        private Point2D navigationVector;

        public Keyboard() {
            keyBits = new BitSet(256);
            navigationVector = new Point2D.Double(0, 0);
            keyBits.clear();
        }

        public void addKeyPress(int keyCode) {
            keyBits.set(keyCode);
        }

        public void removeKeyPress(int keyCode) {
            keyBits.clear(keyCode);
        }

        public Point2D getDirection() {
            double deltaX = 0;
            double deltaY = 0;

            if (keyBits.get(37)) {
                deltaX -= 1;
            }

            if (keyBits.get(38)) {
                deltaY -= 1;
            }

            if (keyBits.get(39)) {
                deltaX += 1;
            }

            if (keyBits.get(40)) {
                deltaY += 1;
            }

            navigationVector.setLocation(deltaX, deltaY);
            return navigationVector;
        }
    }

    protected static class Drag {
        private Point2D dragStart;
        private Point2D dragPrevious;
        private Point2D dragEnd;
        private Rectangle2D dragRegion;
        private long dragDeltaCount;

        public Drag() {
            dragStart = new Point2D.Double();
            dragPrevious = new Point2D.Double();
            dragEnd = new Point2D.Double();
            dragRegion = RectangleUtilities.
                    generateRectangle(dragStart, dragEnd);
            dragDeltaCount = 0;
        }

        public void startDrag(Point2D location) {
            dragStart.setLocation(location);
            dragPrevious.setLocation(location);
            dragEnd.setLocation(location);
            dragRegion = RectangleUtilities.
                    generateRectangle(dragStart, dragEnd);
            dragDeltaCount = 0;
        }

        public void updateDrag(Point2D location) {
            dragPrevious.setLocation(dragEnd);
            dragEnd.setLocation(location);
            dragRegion = RectangleUtilities.
                    generateRectangle(dragStart, dragEnd);
            dragDeltaCount += 1;
        }

        public void releaseDrag() {
            dragRegion = RectangleUtilities.
                    generateRectangle(dragStart, dragEnd);
            dragStart.setLocation(0, 0);
            dragPrevious.setLocation(0, 0);
            dragEnd.setLocation(0, 0);
        }

        public Point2D getInstantaneousDelta() {
            return new Point2D.Double(
                    dragEnd.getX() - dragPrevious.getX(),
                    dragEnd.getY() - dragPrevious.getY());
        }

        public Point2D getDragStart() {
            return dragStart;
        }

        public Point2D getDragEnd() {
            return dragEnd;
        }

        public long getDragDeltaCount() {
            return dragDeltaCount;
        }

        public Rectangle2D getDragRegion() {
            return dragRegion;
        }
    }
}
