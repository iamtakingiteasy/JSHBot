package org.eientei.jshbot.bundles.commands.bundle;

import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.bundles.utils.GenericSingularServiceListener;
import org.eientei.jshbot.protocols.console.api.ConsoleCommand;
import org.eientei.jshbot.protocols.console.api.ConsoleCommandCompleter;
import org.eientei.jshbot.protocols.console.api.ConsoleCommandContext;
import org.eientei.jshbot.protocols.console.api.MountPoint;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 21:38
 */
public class BundleStop implements ConsoleCommand {
    private BundleContext bundleContext;
    private GenericSingularServiceListener<Dispatcher> dispatcherService;

    public BundleStop(BundleContext bundleContext, GenericSingularServiceListener<Dispatcher> dispatcherService) {
        this.bundleContext = bundleContext;
        this.dispatcherService = dispatcherService;
    }


    @Override
    public void setup(ConsoleCommandContext context) {
        List<ConsoleCommandCompleter> completers = new ArrayList<ConsoleCommandCompleter>();
        completers.add(new BundleIdCompleter(bundleContext, new BundleIdCompleter.IdProvider() {
            @Override
            public SortedSet<String> provide() {
                SortedSet<String> result = new TreeSet<String>();
                for (Bundle b : bundleContext.getBundles()) {
                    if (b.getState() == Bundle.ACTIVE) {
                        result.add(String.valueOf(b.getBundleId()));
                    }
                }
                return result;
            }
        }));
        context.addMountPoint(new MountPoint("Stops bundle",
                completers,
                true,
                "bundle", "stop"));
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
                bundleContext.getBundle(bundleId).stop();
                Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), "Bundle " + bundleId + " stopped.");
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
            } catch (BundleException e) {
                e.printStackTrace();
            }
        }
    }
}
