package org.eientei.jshbot.api.dispatcher;

import java.net.URI;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 10:45
 */
public interface SubscriberContext {
    void addTopic(String uri);
    void removeTopic(String uri);
    Collection<URI> getTopics();
    void setQueueSize(int size);
    int getQueueSize();
    void detach();
    void markTransient();
}
