package org.eientei.jshbot.bundles.api.message;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-17
 * Time: 01:13
 */
public interface Result<T> {
    /**
     * Waits for /message/ delivery to receiver queue.
     * Return of this method without exceptions means only that message was delivered, whether it will data processed
     * or not.
     *
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException if message wasn't delivered in TTL period
     */
    void waitForDelivery() throws InterruptedException, TimeoutException;

    /**
     * Same as above, only you may specify the timeout which indefinitely will be lesser or equal to global Message TTL
     * setting.
     *
     * @param timeout millis
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException if message wasn't delivered in TTL period
     */
    void waitForDelivery(long timeout) throws InterruptedException, TimeoutException;

    /**
     * Fetches the result of previously sent message
     *
     * @return Message with result of request
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException if message wasn't delivered in TTL period
     */
    T result() throws InterruptedException, ExecutionException, TimeoutException;

    /**
     * Same as above, only you may specify the timeout which indefinitely will be lesser or equal to global Message TTL
     * setting.
     *
     * @param timeout millis
     * @return Message with result of request
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException if message wasn't processed in TTL period
     */
    T result(long timeout) throws InterruptedException, ExecutionException, TimeoutException;

    /**
     * Gets delivery status
     *
     * @return true if message was delivered
     */
    boolean isDelivered();

    /**
     * Gets result response availability status
     *
     * @return true if result is ready
     */
    boolean isDone();


    /**
     * Gets timeout status
     *
     * @return true if operation timed out
     */
    boolean isTimedOut();
}

