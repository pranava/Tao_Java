package org.des.tao.demo;

import org.des.tao.engine.Simulation;
import org.des.tao.ide.InteractiveSimulation;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class Untitled extends InteractiveSimulation {
    // Variables
    // The size of the queue at each level.
    private int[] queue;
    // The number of servers available at each level.
    private int[] servers;
    // The service delay at each level.
    private int[] delay;

    // Events
    private Method Enter;
    private Method Leave;
    private Method Start;
    private Method Run;

    public Untitled() throws NoSuchMethodException {
        super();

        this.Enter = getClass().getDeclaredMethod("Enter", int.class);
        this.Leave = getClass().getDeclaredMethod("Leave", int.class);
        this.Start = getClass().getDeclaredMethod("Start", int.class);
        this.Run = getClass().getDeclaredMethod("Run");

        this.Enter.setAccessible(true);
        this.Leave.setAccessible(true);
        this.Start.setAccessible(true);
        this.Run.setAccessible(true);
    }

    @Override
    protected void traceState(PrintWriter traceWriter, double clock, String eventName) {
        traceWriter.printf("%f\t%s\n", getClock(), eventName);
    }

    protected void Enter(int i) {
        // Insert body here.
        this.queue[i] += 1;

        if ((i == 0)) {
            scheduleEvent((5), (5), Enter, i);
        }

        if ((this.servers[i] > 0)) {
            scheduleEvent((0), (5), Start, i);
        }

    }

    protected void Leave(int i) {
        // Insert body here.
        this.servers[i] += 1;

        if ((i < 9)) {
            scheduleEvent((0), (5), Enter, i + 1);
        }

        if ((this.queue[i] > 0)) {
            scheduleEvent((0), (5), Start, i);
        }

    }

    protected void Start(int i) {
        // Insert body here.
        this.queue[i] -= 1;
        this.servers[i] -= 1;

        if ((true)) {
            scheduleEvent((this.delay[i]), (5), Leave, i);
        }

    }

    protected void Run() {
        // Insert body here.
        this.queue = new int[10];
        this.servers = new int[10];
        this.delay = new int[10];

        for (int i = 0; i < 10; i++) {
            this.queue[i] = 0;
            this.servers[i] = 1;
            this.delay[i] = (i + 1);
        }

        if ((true)) {
            scheduleEvent((0), (5), Enter, 0);
        }

    }

    public static void main(String[] args) {
        try {
            InteractiveSimulation s = new Untitled();
            s.showRemote();
            s.startSimulation(1000);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}

