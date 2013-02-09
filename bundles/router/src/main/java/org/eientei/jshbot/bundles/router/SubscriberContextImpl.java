package org.eientei.jshbot.bundles.router;

import org.eientei.jshbot.api.dispatcher.Subscriber;
import org.eientei.jshbot.api.dispatcher.SubscriberContext;
import org.eientei.jshbot.api.message.Message;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 11:19
 */
public class SubscriberContextImpl implements SubscriberContext {
    private int queueSize = 32;
    private ArrayBlockingQueue<Message> messageQueue = new ArrayBlockingQueue<Message>(queueSize,true);
    private ConcurrentSkipListSet<URI> topics = new ConcurrentSkipListSet<URI>();
    private Subscriber subscriber;
    private UUID uuid;
    private final Object monitor = new Object();
    private boolean transientSubscriber = false;
    private String serviceSymbolicName;
    private DispatcherImpl dispatcher;

    public SubscriberContextImpl(Subscriber subscriber, UUID uuid, String serviceSymbolicName, DispatcherImpl dispatcher) {
        this.subscriber = subscriber;
        this.uuid = uuid;
        this.serviceSymbolicName = serviceSymbolicName;
        this.dispatcher = dispatcher;
        runConsumer();
    }

    private void runConsumer() {
        new Thread() {
            @Override
            public void run() {
                Message message;
                while (true) {
                    synchronized (monitor) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                        }
                        while (subscriber != null && (message = messageQueue.poll()) != null) {
                            subscriber.consume(message);
                        }
                    }
                }
            }
        }.start();
    }

    public void addMessage(Message message) {
        if (messageQueue.remainingCapacity() == 0) {
            messageQueue.poll();
        }
        messageQueue.offer(message);
        synchronized (monitor) {
            monitor.notify();
        }
    }

    @Override
    public void addTopic(String uri) {
        topics.add(URI.create(uri));
    }

    @Override
    public void removeTopic(String uri) {
        topics.remove(URI.create(uri));
    }

    public Collection<URI> getTopics() {
        return Collections.unmodifiableCollection(topics);
    }

    @Override
    public void setQueueSize(int size) {
        queueSize = size;
        messageQueue = new ArrayBlockingQueue<Message>(queueSize, true, messageQueue);
    }

    @Override
    public int getQueueSize() {
        return queueSize;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void detach() {
        subscriber = null;
        if (isTransient()) {
            topics.clear();
            messageQueue.clear();
            dispatcher.removeSubscriber(serviceSymbolicName);
        }
    }

    @Override
    public void markTransient() {
        transientSubscriber = true;
        if (isDetached()) {
            topics.clear();
            messageQueue.clear();
            dispatcher.removeSubscriber(serviceSymbolicName);
        }
    }

    public void renew(Subscriber subscriber) {
        this.subscriber = subscriber;
        synchronized (monitor) {
            monitor.notify();
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isDetached() {
        return subscriber == null;
    }

    public boolean isTransient() {
        return transientSubscriber;
    }

    public String getServiceSymbolicName() {
        return serviceSymbolicName;
    }
}
