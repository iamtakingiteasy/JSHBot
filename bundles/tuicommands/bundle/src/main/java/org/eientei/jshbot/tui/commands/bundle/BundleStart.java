package org.eientei.jshbot.tui.commands.bundle;

import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandCompleter;
import org.eientei.jshbot.api.tuiconsole.MountPoint;
import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.tui.utils.GenericSingularServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
    protected BundleIdCompleter.IdProvider getIdProvider() {
        return new BundleIdCompleter.IdProvider() {
            @Override
            public SortedSet<String> provide() {
                SortedSet<String> result = new TreeSet<String>();
                for (Bundle b : bundleContext.getBundles()) {
                    if (b.getState() != Bundle.ACTIVE) {
                        result.add(String.valueOf(b.getBundleId()));
                    }
                }
                return result;
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
