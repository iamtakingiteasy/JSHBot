package org.eientei.jshbot.bundles.services.dispatcher;

import org.apache.felix.ipojo.annotations.*;
import org.eientei.jshbot.bundles.api.message.Dispatcher;
import org.eientei.jshbot.bundles.api.message.Result;
import org.eientei.jshbot.bundles.api.message.Subscriber;
import org.eientei.jshbot.bundles.utils.ithread.InterruptableThread;
import org.osgi.framework.ServiceReference;

import java.net.URI;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-17
 * Time: 15:19
 */
@Provides
@Instantiate
@Component
public class DispatcherImpl extends InterruptableThread implements Dispatcher {
    private final long pausedTTL = 1800000; // 30 minutes
    private final long maxTTL = 300000;     // five minutes
    private final long defaultTTL = 60000;  // one minute
    private final Queue<MessageImpl> messageQueue = new LinkedBlockingQueue<MessageImpl>();
    private final Map<SubscriberId, SubscriberContext> subscribers = new HashMap<SubscriberId, SubscriberContext>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public DispatcherImpl() {
        setName("Message Dispatcher thread");
        setTimeoutable(true);
        setTimeout(100);
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                rmrConns();
            }
        }, pausedTTL, pausedTTL, TimeUnit.MILLISECONDS);
    }

    @Bind(aggregate = true, optional = true)
    private void bundSubscriber(Subscriber subscriber, ServiceReference<Subscriber> ref) {
        SubscriberId id = new SubscriberId(subscriber,ref);
        SubscriberContext context = subscribers.get(id);
        if (context == null) {
            context = new SubscriberContext(this);
            context.start();
        }
        context.unpause(subscriber);
    }

    @Unbind
    private void unbindSubscriber(Subscriber subscriber, ServiceReference<Subscriber> ref) {
        SubscriberId id = new SubscriberId(subscriber,ref);
        SubscriberContext context = subscribers.get(id);
        if (context != null) {
            context.pause();
        }
    }


    @PostRegistration
    public void registration(ServiceReference ref) {
        start();
    }

    @PostUnregistration
    public void unregistration(ServiceReference ref) {
        scheduler.shutdown();
        try {
            scheduler.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        if (!scheduler.isTerminated()) {
            scheduler.shutdownNow();
        }

        terminate();
        for (SubscriberContext context : subscribers.values()) {
            context.terminate();
        }
    }

    @Override
    protected void job() throws Exception {
        MessageImpl message;
        List<MessageImpl> retries = new ArrayList<MessageImpl>();
        while ((message = messageQueue.poll()) != null) {
            boolean matched = false;
            for (SubscriberContext context : subscribers.values()) {
                if (context.matches(message)) {
                    context.enqueue(message);
                    matched = true;
                }
            }
            if (!matched) {
                if (message.timeLeft() > 0) {
                    retries.add(message);
                }
            }
        }
        messageQueue.addAll(retries);
    }

    @Override
    public <T> Result<T> send(T data, String topic) {
        return send(data,topic,defaultTTL);
    }

    @Override
    public <T> Result<T> send(T data, String topic, long ttl) {
        if (ttl > maxTTL) ttl = maxTTL;
        ResultImpl<T> result = new ResultImpl<T>(ttl);
        MessageImpl<T> message = new MessageImpl<T>(data, URI.create(topic),result);

        enqueue(message);

        return result;
    }

    public void rmrConns() {
        List<SubscriberId> idsToremove = new ArrayList<SubscriberId>();
        for (Map.Entry<SubscriberId,SubscriberContext> context : subscribers.entrySet()) {
            if (context.getValue().isPaused() && context.getValue().pausedFor() > pausedTTL) {
                context.getValue().terminate();
                idsToremove.add(context.getKey());
            }
        }
        for (SubscriberId id : idsToremove) {
            subscribers.remove(id);
        }
    }

    public void enqueue(MessageImpl message) {
        messageQueue.offer(message);
        wakeup();
    }


}
