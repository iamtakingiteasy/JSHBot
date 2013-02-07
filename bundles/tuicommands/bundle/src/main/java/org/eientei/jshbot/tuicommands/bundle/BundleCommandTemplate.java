package org.eientei.jshbot.tuicommands.bundle;

import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandCompleter;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandContext;
import org.eientei.jshbot.api.tuiconsole.MountPoint;
import org.eientei.jshbot.bundles.utils.GenericSingularServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-07
 * Time: 00:22
 */
public abstract class BundleCommandTemplate implements ConsoleCommand {
    protected BundleContext bundleContext;
    private GenericSingularServiceListener<Dispatcher> dispatcherService;
    private Map<String, Long> bundleNameIdMap = new HashMap<String, Long>();

    public BundleCommandTemplate(BundleContext bundleContext, GenericSingularServiceListener<Dispatcher> dispatcherService) {
        this.bundleContext = bundleContext;
        this.dispatcherService = dispatcherService;
    }

    protected abstract BundleIdCompleter.BundleMatcher getBundleMatcher();
    protected abstract MountPoint getMountPoint(List<ConsoleCommandCompleter> completers);
    protected abstract String getAction();
    protected abstract void command(long bundleId) throws BundleException;


    @Override
    public void setup(ConsoleCommandContext context) {
        List<ConsoleCommandCompleter> completers = new ArrayList<ConsoleCommandCompleter>();

        completers.add(new BundleIdCompleter(bundleContext, getBundleMatcher()));
        context.addMountPoint(getMountPoint(completers));
    }

    @Override
    public void execute(List<String> cmd, List<String> arguments) {
        long bundleId = -1;
        if (arguments.size() > 0) {
            try {
                bundleId = Integer.parseInt(arguments.get(0));
            } catch (NumberFormatException e) {
                for (Bundle b : bundleContext.getBundles()) {
                    if (b.getSymbolicName().equals(arguments.get(0))) {
                        bundleId = b.getBundleId();
                        break;
                    }
                }
                if (bundleId == -1) {
                    Message message;
                    if (arguments.get(0).matches("^[0-9]+.*$")) {
                        message = new Message("console://stdin", "console://stdout", "Invalid bundle id: " + arguments.get(0));
                    } else {
                        message = new Message("console://stdin", "console://stdout", "Invalid bundle symbolic name: " + arguments.get(0));
                    }
                    dispatcherService.getOrWaitForServiceInstance().dispatch(message);
                    return;
                }
            }
            Bundle bundle = null;
            for (Bundle b : bundleContext.getBundles()) {
                if (b.getBundleId() == bundleId) {
                    bundle = b;
                    break;
                }
            }
            if ((bundle == null)) {
                Message message = new Message("console://stdin", "console://stdout", "No such bundle with id " + bundleId);
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
                return;
            }
            try {
                command(bundleId);
                Message message = new Message("console://stdin", "console://stdout", "Bundle " + bundleId + " " + getAction());
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
            } catch (BundleException e) {
                e.printStackTrace();
            }
        } else {
            Message message = new Message("console://stdin", "console://stdout", "Please enter bundle id or symbolic name");
            dispatcherService.getOrWaitForServiceInstance().dispatch(message);
        }
    }
}
