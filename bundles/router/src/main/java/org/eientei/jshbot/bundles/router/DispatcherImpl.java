package org.eientei.jshbot.bundles.router;

import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.dispatcher.Subscriber;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.bundles.utils.GenericActivatorThread;
import org.eientei.jshbot.bundles.utils.GenericServiceListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.net.URI;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-03
 * Time: 06:41
 */
public class DispatcherImpl extends GenericActivatorThread implements Dispatcher {
    private ServiceRegistration<Dispatcher> service;
    private Queue<Message> messageQueue = new ArrayBlockingQueue<Message>(32);
    private ConcurrentMap<Subscriber, SubscriberContextImpl> subscribers = new ConcurrentHashMap<Subscriber, SubscriberContextImpl>();
    private SubscriberServiceListener subscriberListener;
    private final Dispatcher dispatcher = this;


    public DispatcherImpl(BundleContext context) {
        super(context);
        setName("DispatcherThread");
    }


    private boolean uriComponentMatch(String src, String dst) {
        if (src == null || dst == null) {
            return false;
        } else {
            return dst.equals("*") || dst.equals(src);
        }
    }

    private boolean uriMatch(URI src, URI dst) {
        if (src == null || dst == null) return false;

        return     uriComponentMatch(src.getScheme(), dst.getScheme())
                && uriComponentMatch(src.getAuthority(), dst.getAuthority())
                && uriComponentMatch(src.getPath(), dst.getPath());

    }

    @Override
    protected void initialize() {
        subscriberListener = new SubscriberServiceListener(bundleContext);
        service = bundleContext.registerService(Dispatcher.class,this,null);
    }

    @Override
    protected void deinitialize() {
        //service.unregister();
        subscriberListener.ungetAllServices();
        subscriberListener.unregisterServiceListener();
    }

    @Override
    protected void doIterativeJob() {
        Message message;
        while ((message = messageQueue.poll()) != null) {
            for (Map.Entry<Subscriber, SubscriberContextImpl> entry: subscribers.entrySet()) {
                boolean sendMessage = false;
                for (URI topic : entry.getValue().getTopics()) {
                    if  (              (message.getDest() == null && uriMatch(message.getSource(), topic))
                                    || (message.getDest() != null && uriMatch(topic, message.getDest()))
                            ) {
                        sendMessage = true;
                        break;
                    }
                }
                if (sendMessage) {
                    entry.getKey().consume(message);
                }
            }
        }
    }

    @Override
    public boolean dispatch(Message message) {
        URI source = message.getSource();
        if (source == null || source.getScheme() == null || source.getAuthority() == null) {
            throw new MessageDispatchException("Invalid source URI for message");
        }
        boolean result = messageQueue.offer(message);
        notifyMonitor();
        return result;
    }

    private class SubscriberServiceListener extends GenericServiceListener<Subscriber> {

        public SubscriberServiceListener(BundleContext bundleContext) {
            super(Subscriber.class.getName(), bundleContext);
            registerServiceListener();
            fetchAllAvailableServices();
        }

        @Override
        protected void removeService(Subscriber service) {
            subscribers.remove(service);
        }

        @Override
        protected void addService(Subscriber service) {
            SubscriberContextImpl context = new SubscriberContextImpl(dispatcher);
            subscribers.put(service, context);
            service.registration(context);
        }
    }
}
