package org.eientei.jshbot.bundles.services.dispatcher;

import org.eientei.jshbot.bundles.api.message.Message;
import org.eientei.jshbot.bundles.api.message.MessageType;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-17
 * Time: 12:49
 */
public class MessageImpl<T> implements Message<T> {
    private final T messageData;
    private final MessageType<T> type;
    private final URI topicURI;
    private final ResultImpl<T> senderResult;


    public MessageImpl(T messageData, MessageType<T> type, URI topicURI, ResultImpl<T> senderResult) {
        this.messageData = messageData;
        this.type = type;
        this.topicURI = topicURI;
        this.senderResult = senderResult;
    }

    public MessageType<T> getDataTypeToken() {
        return type;
    }

    public void markDelivered() {
        senderResult.delivered();
    }

    public T data() {
        return messageData;
    }

    public long timeLeft() {
        return senderResult.timeLeft();
    }

    @Override
    public URI topic() {
        return topicURI;
    }

    @Override
    public void returnResult(T data) {
         senderResult.setResultData(data);
    }

    @Override
    public void returnException(Throwable t) {
        senderResult.setException(t);
    }
}
