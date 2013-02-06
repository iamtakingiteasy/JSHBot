package org.eientei.jshbot.api.dispatcher;

import org.eientei.jshbot.api.message.Message;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-03
 * Time: 15:42
 */
public interface Subscriber {
    void consume(Message message);
    void registration(SubscriberContext subscriberContext);
}
