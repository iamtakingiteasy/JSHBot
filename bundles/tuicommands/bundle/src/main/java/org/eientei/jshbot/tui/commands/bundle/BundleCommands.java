package org.eientei.jshbot.tui.commands.bundle;

import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.tui.utils.GenericActivatorThread;
import org.osgi.framework.BundleContext;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 18:47
 */
public class BundleCommands extends GenericActivatorThread {
    public BundleCommands(BundleContext bundleContext) {
        super(bundleContext);
    }

    @Override
    protected void initialize() {
        bundleContext.registerService(ConsoleCommand.class, new BundleList(bundleContext, dispatcherService), null);
        bundleContext.registerService(ConsoleCommand.class, new BundleStop(bundleContext, dispatcherService), null);
        bundleContext.registerService(ConsoleCommand.class, new BundleStart(bundleContext, dispatcherService), null);
        bundleContext.registerService(ConsoleCommand.class, new BundleRestart(bundleContext, dispatcherService), null);
        bundleContext.registerService(ConsoleCommand.class, new BundleUpdate(bundleContext, dispatcherService), null);
        bundleContext.registerService(ConsoleCommand.class, new BundleUninstall(bundleContext, dispatcherService), null);
    }

    @Override
    protected void deinitialize() {

    }

    @Override
    protected void doIterativeJob() {

    }
}
