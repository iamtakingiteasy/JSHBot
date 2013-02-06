package org.eientei.jshbot.bundles.commands.bundle;

import org.eientei.jshbot.protocols.console.api.ConsoleCommandCompleter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.List;
import java.util.SortedSet;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 21:43
 */
public class BundleIdCompleter implements ConsoleCommandCompleter {
    private BundleContext bundleContext;
    private IdProvider provider;

    public BundleIdCompleter(BundleContext bundleContext, IdProvider provider) {
        this.bundleContext = bundleContext;
        this.provider = provider;
    }

    public static interface IdProvider {
        public SortedSet<String> provide();
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        Bundle[] bundles = bundleContext.getBundles();
        if (bundles != null) {
            SortedSet<String> bundleIds = provider.provide();
            if (buffer.matches("^[0-9]+$")) {
                for (String match : bundleIds.tailSet(buffer)) {
                    if (!match.startsWith(buffer)) {
                        break;
                    }
                    candidates.add(match);
                }
            } else {
                candidates.addAll(bundleIds);
            }
        }

        return candidates.isEmpty() ? -1 : 0;
    }
}
