package org.eientei.jshbot.bundles.router;

import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.dispatcher.Subscriber;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.bundles.router.tuicommands.SendMessage;
import org.eientei.jshbot.bundles.router.tuicommands.SubscriberList;
import org.eientei.jshbot.bundles.utils.GenericProducerThread;
import org.eientei.jshbot.bundles.utils.GenericServiceListener;
import org.osgi.framework.BundleContext;

import java.net.URI;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-03
 * Time: 06:41
 */
public class DispatcherImpl extends GenericProducerThread implements Dispatcher {
    private Queue<Message> messageQueue = new ArrayBlockingQueue<Message>(32);
    private ConcurrentMap<String, SubscriberContextImpl> subscribers = new ConcurrentHashMap<String, SubscriberContextImpl>();
    private SubscriberServiceListener subscriberListener;
    private Set<UUID> usedUUID = new ConcurrentSkipListSet<UUID>();


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
        bundleContext.registerService(Dispatcher.class,this,null);
        bundleContext.registerService(ConsoleCommand.class, new SubscriberList(subscribers,this), null);
        bundleContext.registerService(ConsoleCommand.class, new SendMessage(this), null);
    }

    @Override
    protected void deinitialize() {
        subscriberListener.ungetAllServices();
        subscriberListener.unregisterServiceListener();
    }

    @Override
    protected void doIterativeJob() {
        Message message;
        while ((message = messageQueue.poll()) != null) {
            for (SubscriberContextImpl context : subscribers.values()) {
                boolean sendMessage = false;
                for (URI topic : context.getTopics()) {
                    if  (      (message.getDest() == null && uriMatch(message.getSource(), topic))
                            || (message.getDest() != null && uriMatch(topic, message.getDest()))
                            ) {
                        sendMessage = true;
                        break;
                    }
                }
                if (sendMessage) {
                    context.addMessage(message);
                }
            }

            if (!message.wasDelivered()) {
                // do anouncing message which wasn't delivered
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
        protected void removeService(Subscriber service, String serviceSymbolicName) {
            subscribers.get(serviceSymbolicName).detach();
        }

        @Override
        protected void addService(Subscriber service, String serviceSymbolicName) {
            SubscriberContextImpl  context = subscribers.get(serviceSymbolicName);

            if (context == null) {
                UUID uuid = UUID.nameUUIDFromBytes(serviceSymbolicName.getBytes());

                while (usedUUID.contains(uuid)) {
                    uuid = UUID.randomUUID();
                }

                context = new SubscriberContextImpl(service,uuid);
                subscribers.put(serviceSymbolicName, context);
            } else {
                context.renew(service);
            }
            context.addTopic("mailbox://" + context.getUuid().toString());
            service.registration(context);
        }
    }
}
