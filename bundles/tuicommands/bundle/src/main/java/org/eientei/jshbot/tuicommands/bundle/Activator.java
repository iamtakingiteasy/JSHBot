package org.eientei.jshbot.tuicommands.bundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 18:46
 */
public class Activator implements BundleActivator {
    BundleCommands bundleCommands;

    @Override
    public void start(BundleContext context) throws Exception {
        bundleCommands = new BundleCommands(context);
        bundleCommands.start();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (bundleCommands != null) {
            bundleCommands.terminate();
        }
        bundleCommands = null;
    }
}
