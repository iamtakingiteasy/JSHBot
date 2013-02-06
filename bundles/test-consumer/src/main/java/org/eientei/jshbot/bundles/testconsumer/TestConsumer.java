package org.eientei.jshbot.bundles.testconsumer;

import org.eientei.jshbot.api.dispatcher.Subscriber;
import org.eientei.jshbot.api.dispatcher.SubscriberContext;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.bundles.utils.GenericActivatorThread;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-03
 * Time: 18:43
 */
public class TestConsumer extends GenericActivatorThread implements Subscriber {
    private ServiceRegistration<Subscriber> service;
    private URI local = URI.create("console://test-consumer");

    public TestConsumer(BundleContext bundleContext) {
        super(bundleContext,true);
    }

    @Override
    protected void initialize() {
        service = bundleContext.registerService(Subscriber.class,this,null);
    }

    @Override
    protected void deinitialize() {
        service.unregister();
    }

    @Override
    protected void doIterativeJob() {

    }

    @Override
    public void consume(Message message) {
        System.out.println(message.getText());
    }

    @Override
    public void registration(SubscriberContext subscriberContext) {
        subscriberContext.addTopic(URI.create("console://test-consumer"));
    }
}
