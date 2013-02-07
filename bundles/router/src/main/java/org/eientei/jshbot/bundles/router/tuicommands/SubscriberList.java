package org.eientei.jshbot.bundles.router.tuicommands;

import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandContext;
import org.eientei.jshbot.api.tuiconsole.MountPoint;
import org.eientei.jshbot.bundles.router.SubscriberContextImpl;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-07
 * Time: 15:17
 */
public class SubscriberList implements ConsoleCommand {
    private ConcurrentMap<String, SubscriberContextImpl> subscribers;
    private Dispatcher dispatcher;


    public SubscriberList(ConcurrentMap<String, SubscriberContextImpl> subscribers, Dispatcher dispatcher) {
        this.subscribers = subscribers;
        this.dispatcher = dispatcher;
    }

    @Override
    public void setup(ConsoleCommandContext context) {
        context.addMountPoint(new MountPoint("Operates with subscribers",
                null,false,
                "subscriber"));
        context.addMountPoint(new MountPoint("Lists subscribers on this dispatcher",
                null,false,
                "subscriber", "list"));
    }

    @Override
    public void execute(List<String> cmd, List<String> arguments) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,SubscriberContextImpl> entry: subscribers.entrySet()) {
            sb.append("    ");
            sb.append(entry.getValue().getUuid().toString());
            sb.append("    ");
            sb.append(entry.getKey());
            sb.append("\n");
            for (URI uri : entry.getValue().getTopics()) {
                sb.append("        -    ");
                sb.append(uri.toString());
                sb.append("\n");
            }

        }

        Message message = new Message("console://stdin", "console://stdout", sb.toString());
        dispatcher.dispatch(message);
    }
}
