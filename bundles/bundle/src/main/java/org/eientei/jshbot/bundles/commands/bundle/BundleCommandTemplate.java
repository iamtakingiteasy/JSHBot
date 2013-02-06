package org.eientei.jshbot.bundles.commands.bundle;

import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandCompleter;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandContext;
import org.eientei.jshbot.api.tuiconsole.MountPoint;
import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.bundles.utils.GenericSingularServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-07
 * Time: 00:22
 */
public abstract class BundleCommandTemplate implements ConsoleCommand {
    protected BundleContext bundleContext;
    private GenericSingularServiceListener<Dispatcher> dispatcherService;

    public BundleCommandTemplate(BundleContext bundleContext, GenericSingularServiceListener<Dispatcher> dispatcherService) {
        this.bundleContext = bundleContext;
        this.dispatcherService = dispatcherService;
    }

    protected abstract BundleIdCompleter.IdProvider getIdProvider();
    protected abstract MountPoint getMountPoint(List<ConsoleCommandCompleter> completers);
    protected abstract String getAction();
    protected abstract void command(long bundleId) throws BundleException;


    @Override
    public void setup(ConsoleCommandContext context) {
        List<ConsoleCommandCompleter> completers = new ArrayList<ConsoleCommandCompleter>();
        completers.add(new BundleIdCompleter(bundleContext, getIdProvider()));
        context.addMountPoint(getMountPoint(completers));
    }

    @Override
    public void execute(List<String> arguments) {
        int bundleId = -1;
        if (arguments.size() > 0) {
            try {
                bundleId = Integer.parseInt(arguments.get(0));
            } catch (NumberFormatException e) {
                Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), "Wrong bundle id: " + arguments.get(0));
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
                return;
            }
            Bundle bundle = null;
            for (Bundle b : bundleContext.getBundles()) {
                if (b.getBundleId() == bundleId) {
                    bundle = b;
                    break;
                }
            }
            if ((bundle == null)) {
                Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), "No such bundle with id " + bundleId);
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
                return;
            }
            try {
                command(bundleId);
                Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), "Bundle " + bundleId + " " + getAction());
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
            } catch (BundleException e) {
                e.printStackTrace();
            }
        }
    }
}
