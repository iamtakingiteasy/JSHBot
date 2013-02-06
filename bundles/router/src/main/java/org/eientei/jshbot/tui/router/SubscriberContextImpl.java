package org.eientei.jshbot.tui.router;

import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.dispatcher.SubscriberContext;

import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 11:19
 */
public class SubscriberContextImpl implements SubscriberContext {
    private CopyOnWriteArrayList<URI> topics = new CopyOnWriteArrayList<URI>();
    private Dispatcher dispatcher;

    public SubscriberContextImpl(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }


    @Override
    public void addTopic(URI uri) {
        topics.add(uri);
    }

    @Override
    public void removeTopic(URI uri) {
        topics.remove(uri);
    }

    public CopyOnWriteArrayList<URI> getTopics() {
        return topics;
    }
}
