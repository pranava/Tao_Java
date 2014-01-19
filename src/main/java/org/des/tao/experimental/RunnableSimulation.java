package org.des.tao.experimental;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public abstract class RunnableSimulation implements Runnable {
    protected TreeSet<Event> scheduledEvents;
    protected TreeSet<PendingEvent> pendingEvents;
    protected Method Run;

    protected double clock;
    protected int scheduledEventCount;
    protected int pendingEventCount;

    public RunnableSimulation() throws NoSuchMethodException {
        this.scheduledEvents = new TreeSet<Event>();
        this.pendingEvents = new TreeSet<PendingEvent>();
        this.clock = 0;
        this.scheduledEventCount = 0;
        this.pendingEventCount = 0;

        this.Run = getClass().getDeclaredMethod("Run");
        this.Run.setAccessible(true);
    }

    protected double getClock() {
        return clock;
    }

    protected void scheduleEvent(
            double time, Method eventMethod, Object... eventArguments) {
        scheduleEvent(time, 5, eventMethod, eventArguments);
    }

    protected void scheduleEvent(
            double time, double eventPriority,
            Method eventMethod, Object... eventArguments) {
        scheduledEvents.add(new Event(
                scheduledEventCount++, clock + time, eventPriority,
                eventMethod, eventArguments));
    }

    protected void schedulePendingEvent(
            Condition pendingCondition, Method eventMethod, Object... eventArguments) {
        schedulePendingEvent(pendingCondition, 5, eventMethod, eventArguments);
    }

    protected void schedulePendingEvent(
            Condition pendingCondition, double eventPriority,
            Method eventMethod, Object... eventArguments) {
        pendingEvents.add(new PendingEvent(
                scheduledEventCount++, eventPriority,
                pendingCondition, eventMethod, eventArguments));
    }

    protected void startSimulation() {
        TreeSet<PendingEvent> currentPendingEvents = pendingEvents;
        TreeSet<PendingEvent> generatedPendingEvents =
                new TreeSet<PendingEvent>();
        scheduleEvent(0, Run);

        while (!scheduledEvents.isEmpty()) {
            Event currentEvent = scheduledEvents.pollFirst();
            if (terminationCondition()) break;

            Method eventMethod = currentEvent.getEventMethod();
            Object[] eventArguments = currentEvent.getEventArguments();

            clock = currentEvent.getScheduledTime();

            try {
                eventMethod.invoke(this, eventArguments);
                //traceState(traceWriter, clock, eventMethod.getName());

                // The implicit assumption here is that if the execution of
                // a pending event generates additional pending events, the
                // newly generated pending events will not be considered.
                Iterator<PendingEvent> pendingEventIterator =
                        currentPendingEvents.iterator();
                pendingEvents = generatedPendingEvents;
                while (pendingEventIterator.hasNext()) {
                    PendingEvent pendingEvent = pendingEventIterator.next();
                    if (pendingEvent.getCondition().evaluate()) {
                        Method pendingEventMethod = pendingEvent.getEventMethod();
                        Object[] pendingEventArguments = pendingEvent.getEventArguments();

                        pendingEventMethod.invoke(this, pendingEventArguments);
                        //traceState(traceWriter, clock, pendingEventMethod.getName());
                        pendingEventIterator.remove();
                    }
                }
                pendingEvents = currentPendingEvents;

                while (generatedPendingEvents.size() > 0) {
                    PendingEvent pendingEvent = generatedPendingEvents.pollFirst();
                    currentPendingEvents.add(pendingEvent);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        //traceWriter.close();
    }

    protected abstract void Run();
    protected abstract boolean terminationCondition();

    public void reset() {
        this.scheduledEvents.clear();
        this.pendingEvents.clear();
        this.clock = 0;
        this.scheduledEventCount = 0;
        this.pendingEventCount = 0;
    }

    public interface Condition {
        public boolean evaluate();
    }

    private class Event implements Comparable<Event> {
        private int count;
        private double scheduledTime;
        private double priority;

        private Method eventMethod;
        private Object[] eventArguments;

        public Event(int count, double scheduledTime, double priority,
                     Method eventMethod, Object[] eventArguments) {
            this.count = count;
            this.scheduledTime = scheduledTime;
            this.priority = priority;

            this.eventMethod = eventMethod;
            this.eventArguments = eventArguments;
        }

        public Method getEventMethod() {
            return eventMethod;
        }

        public Object[] getEventArguments() {
            return eventArguments;
        }

        public double getScheduledTime() {
            return scheduledTime;
        }

        @Override
        public int compareTo(Event event) {
            if (count == event.count) return 0;

            // Defaults to LIFO
            int result = (count > event.count) ? -1 : 1;
            if (scheduledTime == event.scheduledTime) {
                if (priority != event.priority) {
                    result = (priority < event.priority) ? -1 : 1;
                }
            } else {
                result = (scheduledTime < event.scheduledTime) ? -1 : 1;
            }

            return result;
        }
    }

    private class PendingEvent implements Comparable<PendingEvent> {
        private int count;
        private double priority;

        private Condition condition;
        private Method eventMethod;
        private Object[] eventArguments;

        public PendingEvent(int count, double priority, Condition condition,
                            Method eventMethod, Object[] eventArguments) {
            this.count = count;
            this.priority = priority;

            this.condition = condition;
            this.eventMethod = eventMethod;
            this.eventArguments = eventArguments;
        }

        public Method getEventMethod() {
            return eventMethod;
        }

        public Condition getCondition() {
            return condition;
        }


        public Object[] getEventArguments() {
            return eventArguments;
        }

        @Override
        public int compareTo(PendingEvent pendingEvent) {
            if (count == pendingEvent.count) return 0;

            // Defaults to FIFO
            int result = (count < pendingEvent.count) ? -1 : 1;
            if (priority != pendingEvent.priority) {
                result = (priority < pendingEvent.priority) ? -1 : 1;
            }

            return result;
        }
    }
}
