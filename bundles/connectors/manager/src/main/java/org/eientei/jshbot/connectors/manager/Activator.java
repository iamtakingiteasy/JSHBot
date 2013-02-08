package org.eientei.jshbot.connectors.manager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-07
 * Time: 18:05
 */
public class Activator implements BundleActivator {
    private ConnectorManager connectorManager;

    @Override
    public void start(BundleContext context) throws Exception {
        connectorManager = new ConnectorManager(context);
        connectorManager.start();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (connectorManager != null) {
            connectorManager.terminate();
        }
        connectorManager = null;
    }
}
