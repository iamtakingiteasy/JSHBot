package org.eientei.jshbot.bundles.api.message;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 20:34
 */
public class Routing {
    private URI origin;
    private URI dest;
    private List<URI> hops = new ArrayList<URI>();
    private URI sender;

    public Routing(String origin, String dest) {
        this.origin = URI.create(origin);
        this.dest = URI.create(dest);
    }

    public Routing(URI origin, URI dest) {
        this.origin = origin;
        this.dest = dest;
    }

    public URI getOrigin() {
        return origin;
    }

    public URI getDest() {
        return dest;
    }

    public void setCurrentSender(URI sender) {
        this.sender = sender;
        hops.add(sender);
    }

    public URI getCurrentSender() {
        return sender;
    }

    public List<URI> getHops() {
        return Collections.unmodifiableList(hops);
    }
}
