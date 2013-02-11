package org.eientei.jshbot.bundles.utils.tactivator;

import org.eientei.jshbot.bundles.utils.ithread.InterruptableThread;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-09
 * Time: 17:36
 */
public abstract class ThreadedActivator implements BundleActivator {
    private final InterruptableThread thread;
    private final ThreadedActivator thisActivator = this;

    protected abstract void initialize();
    protected abstract void deinitialize();

    protected ThreadedActivator() {
        thread = new InterruptableThread() {
            @Override
            protected void initialize() {
                thisActivator.initialize();
            }

            @Override
            protected void deinitialize() {
                thisActivator.deinitialize();
            }
        };
    }


    @Override
    public final void start(BundleContext context) throws Exception {
        thread.start();
    }

    @Override
    public final void stop(BundleContext context) throws Exception {
        thread.terminate();
    }
}
