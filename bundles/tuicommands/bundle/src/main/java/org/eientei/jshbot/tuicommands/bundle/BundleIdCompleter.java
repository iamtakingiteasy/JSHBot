package org.eientei.jshbot.tuicommands.bundle;

import org.eientei.jshbot.api.tuiconsole.ConsoleCommandCompleter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 21:43
 */
public class BundleIdCompleter implements ConsoleCommandCompleter {
    private BundleContext bundleContext;
    private BundleMatcher matcher;

    public BundleIdCompleter(BundleContext bundleContext, BundleMatcher matcher) {
        this.bundleContext = bundleContext;
        this.matcher = matcher;
    }

    public static interface BundleMatcher {
        boolean bundleMatches(Bundle bundle);
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        Bundle[] bundles = bundleContext.getBundles();

        List<Long>  bundleIds = new ArrayList<Long>();
        for (Bundle b : bundles) {
            if (matcher.bundleMatches(b)) {
                bundleIds.add(b.getBundleId());
            }
        }

        SortedSet<String> variants = new TreeSet<String>();
        if (buffer.matches("^[0-9]+$")) {
            for (Long l : bundleIds) {
                variants.add(String.valueOf(l));
            }
        } else {
            for (Long l : bundleIds) {
                variants.add(bundleContext.getBundle(l).getSymbolicName());
            }
        }
        if (buffer.isEmpty()) {
            candidates.addAll(variants);
        } else {
            for (String match : variants.tailSet(buffer)) {
                if (!match.startsWith(buffer)) {
                    break;
                }
                candidates.add(match.substring(buffer.length()));
            }
        }
        return candidates.isEmpty() ? -1 : buffer.length();
    }
}
