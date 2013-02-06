package org.eientei.jshbot.protocols.console.commands;

import jline.console.completer.Completer;
import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.bundles.utils.GenericSingularServiceListener;
import org.eientei.jshbot.protocols.console.api.ConsoleCommand;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
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
    public String[][] getMountPoints() {
        return new String[][] {
                new String[] {"echo"},
                new String[] {"some", "long", "path", "to", "a", "regular", "echo"},
                new String[] {"some", "long", "path", "to", "a", "regular", "newecho"}

        };
    }

    @Override
    public String getDesc() {
        return "Echoes it's arguments";
    }

    @Override
    public Collection<Completer> getCompleters() {
        return new ArrayList<Completer>();
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
