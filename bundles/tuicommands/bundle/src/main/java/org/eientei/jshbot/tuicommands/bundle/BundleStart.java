package org.eientei.jshbot.tuicommands.bundle;

import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandCompleter;
import org.eientei.jshbot.api.tuiconsole.MountPoint;
import org.eientei.jshbot.bundles.utils.GenericSingularServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-07
 * Time: 00:21
 */
public class BundleStart extends BundleCommandTemplate implements ConsoleCommand {
    public BundleStart(BundleContext bundleContext, GenericSingularServiceListener<Dispatcher> dispatcherService) {
        super(bundleContext, dispatcherService);
    }

    @Override
    protected BundleIdCompleter.BundleMatcher getBundleMatcher() {
        return new BundleIdCompleter.BundleMatcher() {
            @Override
            public boolean bundleMatches(Bundle bundle) {
                return bundle.getState() != Bundle.ACTIVE;
            }
        };
    }

    @Override
    protected MountPoint getMountPoint(List<ConsoleCommandCompleter> completers) {
        return new MountPoint("Starts bundle", completers, true,
                "bundle", "start");
    }

    @Override
    protected String getAction() {
        return "started";
    }

    @Override
    protected void command(long bundleId) throws BundleException {
        bundleContext.getBundle(bundleId).start();
    }
}
