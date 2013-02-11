package org.eientei.jshbot.bundles.service.dispatcher;

import org.eientei.jshbot.bundles.api.message.Message;

import java.net.URI;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 19:15
 */
public class ResolvedMessage {
    private final Message message;
    private final List<URI> matchedTopics;

    public ResolvedMessage(List<URI> matchedTopics, Message message) {
        this.matchedTopics = matchedTopics;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public List<URI> getTopics() {
        return matchedTopics;
    }
}
