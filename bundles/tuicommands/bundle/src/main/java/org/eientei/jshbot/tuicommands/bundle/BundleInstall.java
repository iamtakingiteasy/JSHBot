package org.eientei.jshbot.tuicommands.bundle;

import jline.console.completer.FileNameCompleter;
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
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-07
 * Time: 16:38
 */
public class BundleInstall implements ConsoleCommand {
    private BundleContext bundleContext;
    private GenericSingularServiceListener<Dispatcher> dispatcherService;

    public BundleInstall(BundleContext bundleContext, GenericSingularServiceListener<Dispatcher> dispatcherService) {
        this.bundleContext = bundleContext;
        this.dispatcherService = dispatcherService;
    }

    @Override
    public void setup(ConsoleCommandContext context) {
        List<ConsoleCommandCompleter> completers = new ArrayList<ConsoleCommandCompleter>();
        completers.add(new ConsoleCommandCompleter() {
            private FileNameCompleter completer = new FileNameCompleter();
            @Override
            public int complete(String buffer, int cursor, List<CharSequence> candidates) {
                return completer.complete(buffer,cursor,candidates);
            }
        });
        context.addMountPoint(new MountPoint("Installs bundle from specified location",
                completers,
                false,
                "bundle", "install"));
    }

    @Override
    public void execute(List<String> cmd, List<String> arguments) {
        if (arguments.isEmpty() || arguments.get(0).isEmpty()) {
            Message message = new Message("console://stdin", "console://stdout", "Empty installation path");
            dispatcherService.getOrWaitForServiceInstance().dispatch(message);
            return;
        }
        String filepath = "file:" + arguments.get(0);
        try {
            Bundle b = bundleContext.installBundle(filepath);
            Message message = new Message("console://stdin", "console://stdout", "Bundle installed at id " + b.getBundleId());
            dispatcherService.getOrWaitForServiceInstance().dispatch(message);
        } catch (BundleException e) {
            e.printStackTrace();
        }
    }
}
