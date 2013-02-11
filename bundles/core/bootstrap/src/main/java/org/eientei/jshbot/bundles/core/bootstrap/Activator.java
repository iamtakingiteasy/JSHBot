package org.eientei.jshbot.bundles.core.bootstrap;

import org.osgi.framework.*;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-09
 * Time: 16:25
 */
public class Activator implements BundleActivator {
    private File bundleRoot;
    private BundleContext bundleContext;

    private void stopApp() throws BundleException {
        bundleContext.getBundle(0).stop();
    }

    private void populateBundleUrls(List<String> bundleUrls, File dir) {
        String relativeDirPath = dir.getAbsolutePath().replaceFirst(bundleRoot.getAbsolutePath(), "...");
        System.out.println("Scanning directory: " + relativeDirPath);


        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory() && pathname.getName().toLowerCase().endsWith(".jar");
            }
        });

        if (files != null) {
            for (File f : files) {
                bundleUrls.add("file:" + f.getAbsolutePath());
            }
        }

        File[] dirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        if (dirs != null) {
            for (File d : dirs) {
                populateBundleUrls(bundleUrls,d);
            }
        }
    }

    @Override
    public void start(BundleContext context) throws Exception {
        bundleContext = context;

        if (bundleContext.getDataFile("bootstrapped").exists()) return;;

        String bundleRootPath = context.getProperty("bundleRoot");
        if (bundleRootPath == null) {
            System.out.println("Bundle root is not set.");
            stopApp();
            return;
        }
        bundleRoot = new File(bundleRootPath);
        if (!bundleRoot.exists()) {
            System.out.println("Bundle root is not exist: " + bundleRootPath);
            stopApp();
            return;
        }

        if (!bundleRoot.isDirectory()) {
            System.out.println("Bundle root is not a directory: " + bundleRootPath);
            stopApp();
            return;
        }

        List<String> bundleUrls = new ArrayList<String>();
        populateBundleUrls(bundleUrls, bundleRoot);
        System.out.println("Scanning complete");

        List<Bundle> bundles = new ArrayList<Bundle>();
        for (String url : bundleUrls) {
            System.out.println("Installing bundle: " + url.replaceFirst(bundleRoot.getAbsolutePath() + "/", ".../"));
            bundles.add(context.installBundle(url));
        }

        System.out.println("Installation complete");

        for (Bundle b : bundles) {
            if (b.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
                System.out.println("Starting bundle: " + b.getSymbolicName());
                if (b.getState() != Bundle.ACTIVE) {
                    b.start();
                }
            }
        }

        System.out.println("Bootstrap sequence complete");

        bundleContext.getDataFile("bootstrapped").createNewFile();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
