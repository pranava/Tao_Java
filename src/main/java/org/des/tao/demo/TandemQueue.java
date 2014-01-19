package org.des.tao.demo;

import org.des.tao.engine.Simulation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class TandemQueue extends Simulation {
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

    private XYSeries[] queueSeries;
    private XYSeriesCollection queueData;
    private JFrame appFrame;

    public TandemQueue() throws NoSuchMethodException {
        super();

        this.queueSeries = new XYSeries[10];
        this.queueData = new XYSeriesCollection();

        for (int i = 0; i < 10; i++) {
            this.queueSeries[i] = new XYSeries("Queue[" + i + "]");
            this.queueData.addSeries(this.queueSeries[i]);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "XY Series Demo",
                "X",
                "Y",
                this.queueData,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        this.appFrame = new JFrame("Demo");
        this.appFrame.setPreferredSize(new java.awt.Dimension(500, 270));
        this.appFrame.setContentPane(chartPanel);
        this.appFrame.pack();
        RefineryUtilities.centerFrameOnScreen(this.appFrame);
        this.appFrame.setVisible(true);

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
        //traceWriter.printf("%f\t%s\n", getClock(), eventName);
        for (int i = 5; i < 7; i++) {
            this.queueSeries[i].add(clock, queue[i]);
        }
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
            Simulation s = new TandemQueue();
            s.startSimulation(1000);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}

