package org.eientei.jshbot.api.message;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-03
 * Time: 11:49
 */
public class Message {
    private URI source;
    private URI dest;
    private String text;
    private boolean delivered = false;

    public Message(String source, String dest, String text) {
        this.source = URI.create(source);
        this.dest = URI.create(dest);
        this.text = text;
    }

    public Message(URI source, URI dest, String text) {
        this.source = source;
        this.dest = dest;
        this.text = text;
    }

    public URI getSource() {
        return source;
    }

    public URI getDest() {
        return dest;
    }

    public String getText() {
        return text;
    }

    public void markDelivered() {
        delivered = true;
    }

    public boolean wasDelivered() {
        return delivered;
    }
}
