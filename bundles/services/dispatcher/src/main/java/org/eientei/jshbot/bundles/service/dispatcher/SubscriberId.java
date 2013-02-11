package org.eientei.jshbot.bundles.service.dispatcher;

import org.eientei.jshbot.bundles.api.message.Subscriber;
import org.osgi.framework.ServiceReference;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-09
 * Time: 18:56
 */
public class SubscriberId {
    private String value;

    public SubscriberId(Subscriber subscriber, ServiceReference<Subscriber> serviceReference) {
        value = subscriber.getClass().getName() + " | " + serviceReference.getBundle().getSymbolicName();
    }

    public String getValue() {
        return value;
    }
}
