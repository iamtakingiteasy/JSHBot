package org.eientei.jshbot.bundles.utils.ithread;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-09
 * Time: 18:39
 */
public abstract class InterruptableThread extends Thread {
    private volatile boolean running = false;
    private final Object monitor = new Object();
    private boolean timeoutable = false;
    private int timeout = 100;

    protected void initialize() {

    }
    protected void deinitialize() {

    }
    protected abstract void job() throws Exception;

    @Override
    public final void run() {
        initialize();
        running = true;
        while (isRunning()) {
            try {
                job();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                synchronized (monitor) {
                    if (timeoutable) {
                        monitor.wait(timeout);
                    } else {
                        monitor.wait();
                    }
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        deinitialize();
    }

    public final void terminate() {
        if (running) {
            running = false;
            interrupt();
            wakeup();
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public final void wakeup() {
        synchronized (monitor) {
            monitor.notify();
        }
    }

    public final boolean isRunning() {
        return running;
    }


    protected void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    protected void setTimeoutable(boolean timeoutable) {
        this.timeoutable = timeoutable;
    }
}
