package org.eientei.jshbot.protocols.irc;

import org.eientei.jshbot.api.dispatcher.Subscriber;
import org.eientei.jshbot.api.dispatcher.SubscriberContext;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.bundles.utils.GenericProducerThread;
import org.osgi.framework.BundleContext;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-09
 * Time: 03:29
 */
public class IrcManager extends GenericProducerThread implements Subscriber {
    private Queue<Message> messageQueue = new LinkedBlockingQueue<Message>();
    private Map<String,IrcProtocol> instances = new HashMap<String, IrcProtocol>();
    private SubscriberContext subscriberContext;

    public IrcManager(BundleContext context) {
        super(context, true);
    }

    @Override
    protected void initialize() {
        bundleContext.registerService(Subscriber.class, this, null);
    }

    @Override
    protected void deinitialize() {
        subscriberContext.detach();
    }

    @Override
    protected void doIterativeJob() {
        Message message;
        while ((message = messageQueue.poll()) != null) {
            if (message.getDest().getAuthority().equals("connect")) {
                String connector = message.getText();
                IrcProtocol protocol = new IrcProtocol(connector, bundleContext);
                instances.put(connector, protocol);
                protocol.start();
            } else if (message.getDest().getAuthority().equals("disconnect")) {
                IrcProtocol protocol = instances.remove(message.getText());
                if (protocol != null) {
                    protocol.terminate();
                }
            } else if (message.getDest().getAuthority().equals("list-connections")) {
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (String connector : instances.keySet()) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(" ");
                    }
                    sb.append(connector);
                }
                Message reply = new Message("irc-manager://list-connections", message.getText(), sb.toString());
                dispatcherService.getOrWaitForServiceInstance().dispatch(reply);
            }
        }
    }

    @Override
    public void consume(Message message) {
        messageQueue.add(message);
        notifyMonitor();
    }

    @Override
    public void registration(SubscriberContext subscriberContext) {
        this.subscriberContext = subscriberContext;
        subscriberContext.addTopic("irc-manager://connect");
        subscriberContext.addTopic("irc-manager://disconnect");
        subscriberContext.addTopic("irc-manager://list-connections");
    }
}
