package org.des.tao.ide;

import org.des.tao.engine.Simulation;
import org.des.tao.ide.editors.Remote;

import javax.swing.JFrame;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public abstract class InteractiveSimulation extends Simulation {
    private Remote remoteControl;

    public InteractiveSimulation() throws NoSuchMethodException {
        super();
        remoteControl = new Remote();
    }

    public void showRemote() {
        JFrame remoteFrame = new JFrame();
        remoteFrame.add(remoteControl);
        remoteFrame.pack();
        remoteFrame.setVisible(true);
    }

    public void startSimulation(double finishTime) {
        /*scheduleEvent(0, Run);
        Priority minPriority = futureEventsList.firstKey();

        while (minPriority != null && minPriority.getTime() <= finishTime) {
            Event event = futureEventsList.remove(minPriority);
            Method eventMethod = event.getEventMethod();
            Object[] eventArguments = event.getEventArguments();

            clock = minPriority.getTime();

            try {
                String eventName = event.getEventMethod().getName();
                remoteControl.setCurrentEvent(eventName);
                remoteControl.setFutureEvents(futureEventsList.values());

                eventMethod.invoke(this, eventArguments);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            minPriority = futureEventsList.isEmpty() ? null : futureEventsList.firstKey();
        }*/
    }

}
