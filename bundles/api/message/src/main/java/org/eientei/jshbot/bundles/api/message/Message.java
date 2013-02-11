package org.eientei.jshbot.bundles.api.message;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 18:19
 */
public class Message<T>  {
    private final Routing routing;
    private final T instance;

    public Message(String origin, String dest, T object) {
        this.routing = new Routing(URI.create(origin), URI.create(dest));
        instance = object;
    }

    public Message(URI origin, URI dest, T object) {
        this.routing = new Routing(origin, dest);
        instance = object;
    }

    public Message(T object) {
        this.routing = new Routing((URI)null, (URI)null);
        instance = object;
    }

    public Routing getRouting() {
        return routing;
    }

    public T getData() {
        return instance;
    }
}
