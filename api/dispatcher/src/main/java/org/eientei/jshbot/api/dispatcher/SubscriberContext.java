package org.eientei.jshbot.api.dispatcher;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 10:45
 */
public interface SubscriberContext {
    void addTopic(URI uri);
    void removeTopic(URI uri);
}
