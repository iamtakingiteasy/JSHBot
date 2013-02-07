package org.eientei.jshbot.protocols.irc;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-07
 * Time: 18:05
 */
public class Activator implements BundleActivator {
    private IrcProtocol ircProtocol;

    @Override
    public void start(BundleContext context) throws Exception {
        ircProtocol = new IrcProtocol(context);
        ircProtocol.start();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (ircProtocol != null) {
            ircProtocol.terminate();
        }
        ircProtocol = null;
    }
}
