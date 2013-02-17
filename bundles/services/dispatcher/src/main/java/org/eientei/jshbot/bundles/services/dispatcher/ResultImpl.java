package org.eientei.jshbot.bundles.services.dispatcher;

import org.eientei.jshbot.bundles.api.message.Result;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-17
 * Time: 12:59
 */
public class ResultImpl<T> implements Result<T> {
    private final long creationTime;
    private final long TTL;
    private final Object senderMonitor = new Object();
    private boolean delivered = false;
    private boolean timedOut = false;
    private T resultData;
    private Throwable exception;

    public ResultImpl(long ttl) {
        creationTime = new Date().getTime();
        TTL = ttl;
    }


    @Override
    public void waitForDelivery() throws InterruptedException, TimeoutException {
        waitForDelivery(TTL);
    }

    @Override
    public void waitForDelivery(long timeout) throws InterruptedException, TimeoutException {
        if (delivered) return;
        long tl = timeLeft();
        if (timeout > tl) timeout = tl;
        long waitBegin = new Date().getTime();
        synchronized (senderMonitor) {
            while (!delivered) {
                timeout = timeout - (new Date().getTime() - waitBegin);
                if (timeout <= 0) {
                    timedOut = true;
                    throw new TimeoutException();
                }
                senderMonitor.wait(timeout);
            }
        }
    }

    @Override
    public T result() throws InterruptedException, ExecutionException, TimeoutException {
        return result(TTL);
    }

    @Override
    public T result(long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        if (isDone()) {
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            return resultData;
        }
        long tl = timeLeft();
        if (timeout > tl) timeout = tl;
        long waitBegin = new Date().getTime();
        waitForDelivery(timeout);
        synchronized (senderMonitor) {
            while (!isDone()) {
                timeout = timeout - (new Date().getTime() - waitBegin);
                if (timeout <= 0) {
                    timedOut = true;
                    throw new TimeoutException();
                }
                senderMonitor.wait(timeout);
            }
        }
        if (exception != null) {
            throw new ExecutionException(exception);
        }
        return resultData;
    }

    @Override
    public boolean isDelivered() {
        return delivered;
    }

    @Override
    public boolean isDone() {
        return resultData != null || exception != null;
    }

    @Override
    public boolean isTimedOut() {
        return timedOut;
    }

    public long timeLeft() {
        return TTL - (new Date().getTime() - creationTime);
    }

    public void delivered() {
        delivered = true;
        wakeSender();
    }

    private void wakeSender() {
        synchronized (senderMonitor) {
            senderMonitor.notifyAll();
        }
    }

    public void setResultData(T data) {
        resultData = data;
        wakeSender();
    }

    public void setException(Throwable t) {
        exception = t;
        wakeSender();
    }
}
