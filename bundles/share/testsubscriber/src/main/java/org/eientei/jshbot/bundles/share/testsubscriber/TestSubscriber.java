package org.eientei.jshbot.bundles.share.testsubscriber;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.PostRegistration;
import org.apache.felix.ipojo.annotations.Provides;
import org.eientei.jshbot.bundles.api.message.Message;
import org.eientei.jshbot.bundles.api.message.Subscriber;
import org.eientei.jshbot.bundles.api.message.SubscriberContext;
import org.eientei.jshbot.bundles.api.message.annotations.Recieves;
import org.osgi.framework.ServiceReference;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-09
 * Time: 20:36
 */
@Provides
@Instantiate
@Component
public class TestSubscriber implements Subscriber {
    SubscriberContext subscriberContext;

    @PostRegistration
    public void registration(ServiceReference ref) {
        Message<URI> message1 = new Message<URI>("test://test", "connection-manager://connect/transient", URI.create("irc://somewhere.net"));
        Message<URI> message2 = new Message<URI>("test://test", "connection-manager://connect/transient/lol", URI.create("irc://somewhere2.net"));
        Message<URI> message3 = new Message<URI>("test://test", "connection-manager://connect", URI.create("irc://somewhere3.net"));

        subscriberContext.dispatch(message1);
        subscriberContext.dispatch(message2);
        subscriberContext.dispatch(message3);

        Message<String> baka1 = new Message<String>("test://test","test://test", "Hello!");
        subscriberContext.dispatch(baka1);
    }

    @Recieves("test://test")
    public void accept(String message) {
        System.out.println(message);
    }

    @Override
    public void setSubscriberContext(SubscriberContext context) {
        subscriberContext = context;
    }
}
