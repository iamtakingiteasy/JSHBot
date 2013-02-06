package org.eientei.jshbot.core.launcher;

import org.apache.commons.cli.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-02
 * Time: 18:03
 */
public class Main {
    private static String prefix = "runtime";
    private static String frameworkStorage = prefix + File.separator + "Storage";
    private static String bundleRoot = prefix + File.separator + "Bundles";
    private static String entryBundleName = bundleRoot + File.separator + "core.jar";

    private static Options options = new Options() {{
        addOption("s", "storage", true, "Framework storage");
        addOption("b", "bundles", true, "Bundles collection root");
        addOption("e", "entry", true, "Entry (core) bundle");
        addOption("h", "help", false, "Prints this help");
    }};

    private static void parseOptions(String[] args) throws ParseException {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(options,args);

        if (cmd.getOptionValue("storage") != null) {
            frameworkStorage = cmd.getOptionValue("storage");
        }

        if (cmd.getOptionValue("bundles") != null) {
            bundleRoot = cmd.getOptionValue("bundles");
        }

        if (cmd.getOptionValue("entry") != null) {
            entryBundleName = cmd.getOptionValue("entry");
        }

        if (cmd.hasOption("help")) {
            printHelpAndExit();
        }
    }

    private static void printHelpAndExit() {
        HelpFormatter fmt = new HelpFormatter();
        fmt.printHelp("launcher.jar", options);
        System.exit(0);
    }

    public static String findEndtryBundle(String name) {
        File f = new File(name);
        if (!f.exists()) return null;
        return "file:" + f.getAbsolutePath();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Launcher activated");
        parseOptions(args);

        String entryBundleLocation = findEndtryBundle(entryBundleName);

        if (entryBundleLocation == null) {
            System.out.println("Could not find entry bundle location: " + entryBundleName);
            printHelpAndExit();
        }

        FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();
        Map<String, String> config = new HashMap<String, String>();

        config.put(Constants.FRAMEWORK_STORAGE, frameworkStorage);
        config.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
        config.put(Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT,"true");

        config.put("bundleRoot",bundleRoot);

        final Framework framework = frameworkFactory.newFramework(config);

        framework.start();

        BundleContext bundleContext = framework.getBundleContext();

        Bundle entryBundle = bundleContext.installBundle(entryBundleLocation);

        if (entryBundle.getHeaders().get(Constants.FRAGMENT_HOST) != null) {
            framework.waitForStop(0);
            System.out.println("Supplied entry bundle is fragment bundle. Aborting.");
            printHelpAndExit();
        } else {
            entryBundle.start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    framework.stop();
                    framework.waitForStop(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BundleException e) {
                    e.printStackTrace();
                }
            }
        });

        framework.waitForStop(0);
    }
}
