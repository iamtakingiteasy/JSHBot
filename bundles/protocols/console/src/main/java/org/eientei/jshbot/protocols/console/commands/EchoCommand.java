package org.eientei.jshbot.protocols.console.commands;

import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandContext;
import org.eientei.jshbot.api.tuiconsole.MountPoint;
import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.bundles.utils.GenericSingularServiceListener;

import java.net.URI;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 20:37
 */
public class EchoCommand implements ConsoleCommand {
    private GenericSingularServiceListener<Dispatcher> dispatcherService;

    public EchoCommand(GenericSingularServiceListener<Dispatcher> dispatcherService) {
        this.dispatcherService = dispatcherService;
    }


    @Override
    public void setup(ConsoleCommandContext context) {
        context.addMountPoint(new MountPoint("Echoes it's output",
                null,
                false,
                "echo"));
    }

    @Override
    public void execute(List<String> arguments) {
        StringBuilder output = new StringBuilder();
        boolean first = true;
        for (String arg : arguments) {
            if (first) {
                first = false;
            } else {
                output.append(' ');
            }
            output.append(arg);
        }

        Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), output.toString());

        dispatcherService.getOrWaitForServiceInstance().dispatch(message);
    }
}
