package org.des.tao.demo;

import org.des.tao.engine.Simulation;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class CarWash extends Simulation {
    // Variables
    private int queue;
    private int servers;

    // Events
    private Method Enter;
    private Method Leave;
    private Method Start;
    private Method Run;

    public CarWash() throws NoSuchMethodException {
        super();

        this.Enter = getClass().getDeclaredMethod("Enter");
        this.Leave = getClass().getDeclaredMethod("Leave");
        this.Start = getClass().getDeclaredMethod("Start");
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

    protected void Enter() {
        // Insert body here.
        this.queue += 1;

        scheduleEvent((5), (5), Enter);
        if (this.servers > 0) {
            scheduleEvent((0), (5), Start);
        }
    }

    protected void Leave() {
        // Insert body here.
        this.servers += 1;

        if (this.queue > 0) {
            scheduleEvent((0), (5), Start);
        }
    }

    protected void Start() {
        // Insert body here.
        this.queue -= 1;
        this.servers -= 1;

        if ((true)) {
            scheduleEvent((12), (5), Leave);
        }

    }

    protected void Run() {
        // Insert body here.
        this.queue = 0;
        this.servers = 1;

        if ((true)) {
            scheduleEvent((0), (5), Enter);
        }

    }

    public static void main(String[] args) {
        try {
            Simulation s = new CarWashPending();
            s.startSimulation(1000);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}

