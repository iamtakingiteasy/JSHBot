package org.eientei.jshbot.bundles.api.message;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-17
 * Time: 01:10
 */
public interface Message<T> {
    /**
     * Fetches topic of this message
     *
     * @return URI of topic
     */
    URI topic();

    /**
     * Returns some data to sender of this message
     *
     * @param data to return back to sender
     * @return corresponding Result for return message
     */
    void returnResult(T data);

    /**
     * Returns an exception to sender
     *
     * @param t exception to throw at sender
     * @return corresponding Result for return message
     */
    void returnException(Throwable t);

}