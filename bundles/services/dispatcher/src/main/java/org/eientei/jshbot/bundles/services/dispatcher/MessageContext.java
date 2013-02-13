package org.eientei.jshbot.bundles.services.dispatcher;

import org.eientei.jshbot.bundles.api.message.Message;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 14:44
 */
public class MessageContext {
    private final Message message;
    private final long recievedTime;

    public MessageContext(Message message) {
        this.message = message;
        recievedTime = new Date().getTime();
    }

    public Message getMessage() {
        return message;
    }

    public long getRecievedTime() {
        return recievedTime;
    }
}
