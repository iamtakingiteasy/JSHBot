package org.eientei.jshbot.tuicommands.bundle;

import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandContext;
import org.eientei.jshbot.api.tuiconsole.MountPoint;
import org.eientei.jshbot.bundles.utils.GenericSingularServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-07
 * Time: 21:53
 */
public class BundleReload implements ConsoleCommand {
    private BundleContext bundleContext;
    private GenericSingularServiceListener<Dispatcher> dispatcherService;

    public BundleReload(BundleContext bundleContext, GenericSingularServiceListener<Dispatcher> dispatcherService) {
        this.bundleContext = bundleContext;
        this.dispatcherService = dispatcherService;
    }

    @Override
    public void setup(ConsoleCommandContext context) {
        context.addMountPoint(new MountPoint("Reloads bundles",
                null,
                true,
                "bundle", "reload"));
        context.addMountPoint(new MountPoint("Reloads all bundles anyways",
                null,
                true,
                "bundle", "reload-force"));
    }

    @Override
    public void execute(List<String> cmd, List<String> arguments) {
        List<Bundle> bundles = new ArrayList<Bundle>();
        boolean reloadAnyway = cmd.get(cmd.size()-1).equals("reload-force");
        for (Bundle b : bundleContext.getBundles()) {
            if (b.getBundleId() == bundleContext.getBundle().getBundleId() || b.getBundleId() == 0) {
                continue;
            }
            if (reloadAnyway || b.getLastModified() < new File(b.getLocation().replaceFirst("^file:","")).lastModified()) {
                bundles.add(b);
            }
        }
        Bundle thisBundle = bundleContext.getBundle();
        if (reloadAnyway || thisBundle.getLastModified() < new File(thisBundle.getLocation().replaceFirst("^file:","")).lastModified()) {
            bundles.add(thisBundle);
        }
        if (bundles.isEmpty()) {
            Message message = new Message("console://stdin", "console://stdout", "Nothing to reload");
            dispatcherService.getOrWaitForServiceInstance().dispatch(message);
        }
        for (Bundle b : bundles) {
            try {
                Message message = new Message("console://stdin", "console://stdout", "Reloading bundle " + b.getBundleId() + " (" + b.getSymbolicName() + ")");
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
                b.update();
            } catch (BundleException e) {
                e.printStackTrace();
            }
        }
    }
}
