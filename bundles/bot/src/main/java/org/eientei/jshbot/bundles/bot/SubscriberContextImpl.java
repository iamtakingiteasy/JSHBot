package org.eientei.jshbot.bundles.bot;

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
