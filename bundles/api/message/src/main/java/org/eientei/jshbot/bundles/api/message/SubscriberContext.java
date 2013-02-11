package org.eientei.jshbot.bundles.api.message;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-11
 * Time: 00:41
 */
public interface SubscriberContext {
    public void dispatch(Message message);
}
