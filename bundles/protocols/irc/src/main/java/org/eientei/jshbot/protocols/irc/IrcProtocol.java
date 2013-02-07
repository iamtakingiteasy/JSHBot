package org.eientei.jshbot.protocols.irc;

import org.eientei.jshbot.api.dispatcher.Subscriber;
import org.eientei.jshbot.api.dispatcher.SubscriberContext;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.bundles.utils.GenericActivatorThread;
import org.osgi.framework.BundleContext;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-07
 * Time: 18:05
 */
public class IrcProtocol extends GenericActivatorThread implements Subscriber {
    public IrcProtocol(BundleContext bundleContext) {
        super(bundleContext, false);
    }

    @Override
    protected void initialize() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void deinitialize() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void doIterativeJob() {

    }

    @Override
    public void consume(Message message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void registration(SubscriberContext subscriberContext) {
    }
}
