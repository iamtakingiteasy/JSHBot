package org.eientei.jshbot.tui.router;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-03
 * Time: 05:48
 */
public class Activator implements BundleActivator {
    private DispatcherImpl dispatcher = null;

    @Override
    public void start(BundleContext context) throws Exception {
        dispatcher = new DispatcherImpl(context);
        dispatcher.start();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (dispatcher != null) {
            dispatcher.terminate();
        }
        dispatcher = null;
    }
}
