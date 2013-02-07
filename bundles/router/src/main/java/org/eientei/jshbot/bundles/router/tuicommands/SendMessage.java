package org.eientei.jshbot.bundles.router.tuicommands;

import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandContext;
import org.eientei.jshbot.api.tuiconsole.MountPoint;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-07
 * Time: 15:29
 */
public class SendMessage implements ConsoleCommand {
    private Dispatcher dispatcher;

    public SendMessage(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void setup(ConsoleCommandContext context) {
        context.addMountPoint(new MountPoint("Sends a message manually",
                null,true,
                "subscriber", "send"));
    }

    @Override
    public void execute(List<String> cmd, List<String> arguments) {
        if (arguments.size() < 3) {
            Message message = new Message("console://stdin", "console://stdout", "Not enough parameters for message, need three: <source> <dest> <body>");
            dispatcher.dispatch(message);
            return;
        }
        String from = arguments.get(0);
        String dest = arguments.get(1);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String arg : arguments.subList(2,arguments.size())) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
            }
            sb.append(arg);
        }

        Message message = new Message(from,dest,sb.toString());
        dispatcher.dispatch(message);
    }
}
