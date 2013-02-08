package org.eientei.jshbot.connectors.manager;

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
 * Date: 2013-02-07
 * Time: 18:05
 */
public class ConnectorManager extends GenericProducerThread implements Subscriber {
    private Queue<Message> messageQueue = new LinkedBlockingQueue<Message>();
    private Map<URI,PlainConnector> connectors = new HashMap<URI, PlainConnector>();
    private SubscriberContext subscriberContext;

    public ConnectorManager(BundleContext bundleContext) {
        super(bundleContext, true);
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
                URI serverUri = URI.create(message.getText());
                PlainConnector connector = new PlainConnector(serverUri, bundleContext);
                connectors.put(serverUri, connector);
                connector.start();
            } else if (message.getDest().getAuthority().equals("disconnect")) {
                PlainConnector connector = connectors.remove(URI.create(message.getText()));
                if (connector != null) {
                    connector.terminate();
                }
            } else if (message.getDest().getAuthority().equals("list-connections")) {
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (URI uri : connectors.keySet()) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(" ");
                    }
                    sb.append(uri.toString());
                }
                Message reply = new Message("connector-manager://list-connections", message.getText(), sb.toString());
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
        subscriberContext.addTopic("connector-manager://connect");
        subscriberContext.addTopic("connector-manager://disconnect");
        subscriberContext.addTopic("connector-manager://list-connections");
    }
}
