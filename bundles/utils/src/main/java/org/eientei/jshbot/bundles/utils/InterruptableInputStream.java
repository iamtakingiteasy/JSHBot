package org.eientei.jshbot.bundles.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 17:38
 */
public class InterruptableInputStream extends InputStream {
    private InputStream is;
    private int timeout;
    private boolean interruptState = false;
    private boolean returnState = false;

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
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                return 0;
            }
        }
        if (interruptState) {
            return 0;
        }
        if (returnState) {
            returnState = false;
            return -1;
        }
        return is.read();
    }

    public void interrupt() {
        interruptState = true;
    }

    public void makeReturn() {
        returnState = true;
    }
}
