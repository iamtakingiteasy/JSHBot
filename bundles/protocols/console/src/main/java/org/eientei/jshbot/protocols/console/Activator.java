package org.eientei.jshbot.protocols.console;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 15:17
 */
public class Activator implements BundleActivator {
    private ConsoleProtocol protocol;

    @Override
    public void start(BundleContext context) throws Exception {
        protocol = new ConsoleProtocol(context);
        protocol.start();

    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (protocol != null) {
            protocol.terminate();
        }
        protocol = null;
    }
}
