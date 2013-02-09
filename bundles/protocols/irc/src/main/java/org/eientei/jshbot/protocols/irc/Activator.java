package org.eientei.jshbot.protocols.irc;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-08
 * Time: 19:47
 */
public class Activator implements BundleActivator {
    private IrcManager ircManager;

    @Override
    public void start(BundleContext context) throws Exception {
        ircManager = new IrcManager(context);
        ircManager.start();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (ircManager != null) {
            ircManager.terminate();
        }
        ircManager = null;
    }
}
