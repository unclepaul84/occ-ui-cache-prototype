package com.unclepaul.uicacheprototype.utils;

public class ManualResetEvent {
    private final Object monitor = new Object();
    private volatile boolean open = false;

    public ManualResetEvent(boolean isSet) {
        this.open = open;
    }

    public Boolean isSet() {
        return open;
    }

    public void waitOne() throws InterruptedException {
        synchronized (monitor) {
            while (open == false) {
                monitor.wait();
            }
        }
    }

    public void set() {//open start
        synchronized (monitor) {
            open = true;
            monitor.notifyAll();
        }
    }

    public void reset() {//close stop
        synchronized (monitor) {
            open = false;
        }
    }
}