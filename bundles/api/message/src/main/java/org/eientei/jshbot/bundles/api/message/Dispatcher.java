package org.eientei.jshbot.bundles.api.message;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-17
 * Time: 02:29
 */
public interface Dispatcher {
    /**
     * Dispatches data chunk
     *
     * @param data to dispatch
     * @param token specifying type of data
     * @param topic on which message would be published
     * @return Result corresponding to sent message
     */
    <T> Result<T> send(T data, MessageType<T> token, String topic);

    /**
     * Dispatches data chunk
     *
     * @param data to dispatch
     * @param token specifying type of data
     * @param topic on which message would be published
     * @param ttl time to live in millis, lifespan of message, would not be greater then global TTL setting
     * @return Result corresponding to sent message
     */
    <T> Result<T> send(T data, MessageType<T> token, String topic, long ttl);
}
