package org.eientei.jshbot.bundles.utils;

import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.osgi.framework.BundleContext;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-03
 * Time: 18:35
 */
public abstract class GenericActivatorThread extends Thread {
    private volatile boolean running;
    private volatile boolean waiting;
    private final Object monitor = new Object();

    protected BundleContext bundleContext;
    protected GenericSingularServiceListener<Dispatcher> dispatcherService;

    public GenericActivatorThread(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.waiting = true;
    }

    public GenericActivatorThread(BundleContext bundleContext, boolean waiting) {
        this.bundleContext = bundleContext;
        this.waiting = waiting;
    }

    @Override
    public void run() {
        dispatcherService = new GenericSingularServiceListener<Dispatcher>(Dispatcher.class.getName(),bundleContext);
        dispatcherService.registerAsServiceListener();

        initialize();
        running = true;
        while (running) {
            doIterativeJob();
            if (waiting) {
                try {
                    synchronized (monitor) {
                        monitor.wait();
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        deinitialize();
        dispatcherService.unregisterServiceListener();
    }

    protected boolean isRunning() {
        return running;
    }

    protected abstract void initialize();
    protected abstract void deinitialize();

    protected abstract void doIterativeJob();

    protected void termination() {

    }

    public void notifyMonitor() {
        synchronized (monitor) {
            monitor.notify();
        }
    }

    public void terminate() throws InterruptedException {
        if (running) {
            running = false;
            termination();
            interrupt();
            notifyMonitor();
            join();
        }
    }
}
