package org.eientei.jshbot.bundles.testconsumer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-03
 * Time: 18:32
 */
public class Activator implements BundleActivator {
    private TestConsumer testConsumer;

    @Override
    public void start(BundleContext context) throws Exception {
        testConsumer = new TestConsumer(context);
        testConsumer.start();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (testConsumer != null) {
            testConsumer.terminate();
        }
        testConsumer = null;
    }
}
