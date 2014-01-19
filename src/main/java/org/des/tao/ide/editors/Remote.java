package org.des.tao.ide.editors;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class Remote extends JPanel {
    private JLabel currentEventLabel;
    private JLabel futureEventsLabel;

    private JTextField currentEvent;
    private JTextArea futureEvents;

    public Remote() {
        super(new MigLayout());
        initialize();
    }

    public void initialize() {
        currentEventLabel = new JLabel("Current: ");
        currentEvent = new JTextField();
        currentEvent.setEditable(false);

        add(currentEventLabel, "left");
        add(currentEvent, "right wrap");

        futureEventsLabel = new JLabel("Future: ");
        futureEvents = new JTextArea();
        futureEvents.setEditable(false);

        add(futureEventsLabel, "left");
        add(futureEvents, "right wrap");

        setPreferredSize(new Dimension(317, 500));
    }

    public void setCurrentEvent(String eventName) {
        currentEvent.setText(eventName);
    }

    /*public void setFutureEvents(Collection<Simulation.Event> values) {
        futureEvents.removeAll();
        for (Simulation.Event event : values) {
            futureEvents.append(event.getEventMethod().getName());
        }
    }*/
}
