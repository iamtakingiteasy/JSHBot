package org.eientei.jshbot.bundles.utils.istreams;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-11
 * Time: 12:40
 */
public class InterruptableInputStream extends InputStream {
    private InputStream is;
    private int timeout;
    private boolean interruptState = false;
    private boolean returnState = false;
    private final Object monitor = new Object();
    private final static int EOS =  0;
    private final static int EOF = -1;

    public InterruptableInputStream(InputStream is) {
        this.is = is;
        this.timeout = 100;
    }

    public InterruptableInputStream(InputStream is, int timeout) {
        this.is = is;
        this.timeout = timeout;
    }

    @Override
    public int read() throws IOException {
        while (is.available() == 0 && !interruptState && !returnState) {
            try {
                synchronized (monitor) {
                    monitor.wait(timeout);
                }
            } catch (InterruptedException e) {
                return EOF;
            }
        }
        if (interruptState) {
            return EOS;
        }
        if (returnState) {
            returnState = false;
            return EOF;
        }
        return is.read();
    }

    public void makeStop() {
        interruptState = true;
        synchronized (monitor) {
            monitor.notify();
        }
    }

    public void makeReturn() {
        returnState = true;
        synchronized (monitor) {
            monitor.notify();
        }
    }

}
