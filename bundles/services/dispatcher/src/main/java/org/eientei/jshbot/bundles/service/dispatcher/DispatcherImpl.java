package org.eientei.jshbot.bundles.service.dispatcher;

import org.apache.felix.ipojo.annotations.*;
import org.eientei.jshbot.bundles.api.message.Message;
import org.eientei.jshbot.bundles.api.message.Subscriber;
import org.eientei.jshbot.bundles.utils.ithread.InterruptableThread;
import org.eientei.jshbot.bundles.utils.uri.UriUtils;
import org.osgi.framework.ServiceReference;

import java.net.URI;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-09
 * Time: 18:42
 */
@Provides
@Instantiate
@Component
public class DispatcherImpl extends InterruptableThread {
    private Map<UUID, SubscriberContextImpl> subscribers = new HashMap<UUID, SubscriberContextImpl>();
    private Map<SubscriberId,UUID> mailboxRegistry = new HashMap<SubscriberId, UUID>();
    private Queue<MessageContext> globalQueue = new LinkedBlockingQueue<MessageContext>();
    private int messageTTL = 5000;

    @Override
    protected void initialize() {
        setTimeout(1000);
        setTimeoutable(true);
    }

    @PostRegistration
    public void registration(ServiceReference ref) {
        start();
    }

    @PostUnregistration
    public void unregistration(ServiceReference ref) {
        terminate();
    }

    @Bind(aggregate = true, optional = true)
    private void bindSubscriber(Subscriber subscriber, ServiceReference<Subscriber> serviceReference) {
        SubscriberId id = new SubscriberId(subscriber, serviceReference);
        UUID mailboxId = mailboxRegistry.get(id);
        SubscriberContextImpl context;

        if (mailboxId != null) {
            context = subscribers.get(mailboxId);
            context.renew(subscriber);
        } else {
            mailboxId = UUID.nameUUIDFromBytes(id.getValue().getBytes());
            while (mailboxRegistry.values().contains(mailboxId)) {
                mailboxId = UUID.randomUUID();
            }
            context = new SubscriberContextImpl(this, subscriber, mailboxId);
            mailboxRegistry.put(id,mailboxId);
            subscribers.put(mailboxId,context);
            context.start();
        }

        subscriber.setSubscriberContext(context);
    }

    @Unbind
    private void unbindSubscriber(Subscriber subscriber, ServiceReference<Subscriber> serviceReference) {
        SubscriberId id = new SubscriberId(subscriber, serviceReference);
        SubscriberContextImpl context = subscribers.get(id);
        if (context != null) {
            context.detach();
            context.checkRedundanty();
        }
    }

    @Override
    protected synchronized void job() throws Exception {
        MessageContext messageContext;
        List<MessageContext> delayedMessages = new ArrayList<MessageContext>();
        while ((messageContext = globalQueue.poll()) != null) {
            long currentTime = new Date().getTime();
            Message message = messageContext.getMessage();
            boolean delivered = false;
            for (SubscriberContextImpl context : subscribers.values()) {
                List<URI> matchedTopics = new ArrayList<URI>();
                Set<URI> topics = context.getTopics();
                topics.add(context.getMailbox());
                for (URI topic : topics) {
                    URI source = message.getRouting().getOrigin();
                    if (source == null) {
                        source = message.getRouting().getCurrentSender();
                    }
                    if (       (message.getRouting().getDest() == null && UriUtils.match(source, topic))
                            || (message.getRouting().getDest() != null && UriUtils.match(message.getRouting().getDest(), topic))) {
                        matchedTopics.add(topic);
                    }
                }
                if (!matchedTopics.isEmpty()) {
                    context.addMessage(new ResolvedMessage(matchedTopics, message));
                    delivered = true;
                }

            }
            if (!delivered) {
                if ((currentTime - messageContext.getRecievedTime()) < messageTTL) {
                    delayedMessages.add(messageContext);
                }
            }
        }
        globalQueue.addAll(delayedMessages);
    }

    public boolean dispatch(MessageContext message) {
        boolean result = globalQueue.offer(message);
        if (result) {
            wakeup();
        }
        return result;
    }

    public SubscriberContextImpl removeSubscriber(UUID id) {
        SubscriberContextImpl context = subscribers.remove(id);
        if (context != null) {
            mailboxRegistry.remove(id);
            context.terminate();
        }
        return context;
    }
}
