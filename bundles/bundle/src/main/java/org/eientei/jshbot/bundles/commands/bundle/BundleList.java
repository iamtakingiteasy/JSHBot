package org.eientei.jshbot.bundles.commands.bundle;

import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandContext;
import org.eientei.jshbot.api.tuiconsole.MountPoint;
import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.bundles.utils.GenericSingularServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.net.URI;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 18:49
 */
public class BundleList implements ConsoleCommand {
    private BundleContext bundleContext;
    private GenericSingularServiceListener<Dispatcher> dispatcherService;

    public BundleList(BundleContext bundleContext, GenericSingularServiceListener<Dispatcher> dispatcherService) {
        this.bundleContext = bundleContext;
        this.dispatcherService = dispatcherService;
    }

    @Override
    public void setup(ConsoleCommandContext context) {
        context.addMountPoint(new MountPoint("Bundles operations",
                null,
                false,
                "bundle"));
        context.addMountPoint(new MountPoint("Bundles list",
                null,
                false,
                "bundle", "list"));
    }

    @Override
    public void execute(List<String> arguments) {
        StringBuilder sb = new StringBuilder();
        Bundle[] bundles = bundleContext.getBundles();
        boolean first = true;
        if (bundles != null) {
            for (Bundle b : bundles) {
                if (!first) {
                    sb.append("\n");
                } else {
                    first = false;
                }
                switch (b.getState()) {
                    case Bundle.ACTIVE:
                        sb.append("     ACTIVE");
                        break;
                    case Bundle.STARTING:
                        sb.append("   STARTING");
                        break;
                    case Bundle.STOPPING:
                        sb.append("   STOPPING");
                        break;
                    case Bundle.INSTALLED:
                        sb.append("  INSTALLED");
                        break;
                    case Bundle.UNINSTALLED:
                        sb.append("UNINSTALLED");
                        break;
                    case Bundle.RESOLVED:
                        sb.append("   RESOLVED");
                        break;
                    default:
                        sb.append("    UNKNOWN");
                        break;
                }
                sb.append("    ");
                sb.append(String.format("%6d", b.getBundleId()));
                sb.append("    ");
                sb.append(b.getSymbolicName());
            }
        }
        Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), sb.toString());
        dispatcherService.getOrWaitForServiceInstance().dispatch(message);
    }
}
