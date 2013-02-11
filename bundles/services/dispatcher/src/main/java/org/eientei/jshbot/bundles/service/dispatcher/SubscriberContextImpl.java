package org.eientei.jshbot.bundles.service.dispatcher;

import org.eientei.jshbot.bundles.api.message.Message;
import org.eientei.jshbot.bundles.api.message.Routing;
import org.eientei.jshbot.bundles.api.message.Subscriber;
import org.eientei.jshbot.bundles.api.message.SubscriberContext;
import org.eientei.jshbot.bundles.api.message.annotations.Recieves;
import org.eientei.jshbot.bundles.api.message.annotations.SubscriberConfig;
import org.eientei.jshbot.bundles.utils.ithread.InterruptableThread;
import org.eientei.jshbot.bundles.utils.uri.UriUtils;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-09
 * Time: 18:57
 */
public class SubscriberContextImpl extends InterruptableThread implements SubscriberContext {
    private DispatcherImpl dispatcher;

    private Set<URI> topics = new HashSet<URI>();
    private URI mailbox;
    private int queueSize = 32;
    private Subscriber subscriber;
    private boolean transientState = false;
    private UUID id;
    private Queue<ResolvedMessage> messageQueue = new ArrayBlockingQueue<ResolvedMessage>(queueSize, true);
    private List<ListenContext> recievers = new ArrayList<ListenContext>();


    public SubscriberContextImpl(DispatcherImpl dispatcher, Subscriber subscriber, UUID id) {
        this.dispatcher = dispatcher;
        this.subscriber = subscriber;
        this.id = id;
        this.mailbox = URI.create("mailbox://" + id.toString());
        readClass();
    }

    @Override
    protected synchronized void job() throws Exception {
        ResolvedMessage resolvedMessage;
        while (subscriber != null && (resolvedMessage = messageQueue.poll()) != null) {
            Class clazz = resolvedMessage.getMessage().getData().getClass();
            List<URI> matchedTopics = resolvedMessage.getTopics();
            for (URI topic : matchedTopics) {
                for (ListenContext entry : recievers) {
                    for (URI t : entry.getDests()) {
                        if (UriUtils.match(t,topic)) {
                            if (!entry.getSources().isEmpty()) {
                                boolean sourceMatch = false;
                                for (URI s : entry.getSources()) {
                                    if (UriUtils.match(topic, s)) {
                                        sourceMatch = true;
                                        break;
                                    }
                                }
                                if (!sourceMatch) {
                                    continue;
                                }
                            }
                            MethodContext mc = entry.methodContext;
                            if (mc.getArgTypes().isEmpty() || mc.getArgumentClass().equals(clazz.getName())) {
                                List<Object> objects = new ArrayList<Object>();

                                for (ArgType at : mc.getArgTypes()) {
                                    switch (at) {
                                        case DATA:
                                            objects.add(resolvedMessage.getMessage().getData());
                                            break;
                                        case ROUTE:
                                            objects.add(resolvedMessage.getMessage().getRouting());
                                            break;
                                    }
                                }

                                mc.getMethod().invoke(subscriber, objects.toArray(new Object[objects.size()]));
                            }
                        }
                    }
                }
            }
        }
    }

    public Set<URI> getTopics() {
        return new HashSet<URI>(topics);
    }

    public void setQueueSize(int size) {
        queueSize = size;
        messageQueue = new ArrayBlockingQueue<ResolvedMessage>(queueSize, true, messageQueue);
    }

    public int getQueueSize() {
        return queueSize;
    }

    public boolean isTransient() {
        return transientState;
    }

    public boolean isDetached() {
        return subscriber == null;
    }

    public void checkRedundanty() {
        if (isTransient() && isDetached()) {
            dispatcher.removeSubscriber(getSubscriberId());
            reduce();
        }
    }

    public void reduce() {
        if (queueSize > 0) {
            queueSize = 0;
            messageQueue.clear();
        }
    }

    public URI getMailbox() {
        return mailbox;
    }

    public UUID getSubscriberId() {
        return id;
    }

    public void addMessage(ResolvedMessage message) {
        messageQueue.add(message);
        wakeup();
    }

    public synchronized void renew(Subscriber subscriber) {
        this.subscriber = subscriber;
        readClass();
        wakeup();
    }

    private void readClass() {
        if (subscriber.getClass().isAnnotationPresent(SubscriberConfig.class)) {
            SubscriberConfig config = subscriber.getClass().getAnnotation(SubscriberConfig.class);
            if (config.isTransient()) {
                transientState = true;
            }
            if (config.queueSize() != queueSize) {
                setQueueSize(config.queueSize());
            }
        }
        for (Method m : subscriber.getClass().getMethods()) {
            if (m.isAnnotationPresent(Recieves.class)) {
                Recieves r = m.getAnnotation(Recieves.class);
                String[] listenTopics = r.value();
                if (listenTopics != null) {
                    Class[] args = m.getParameterTypes();
                    Set<URI> uris = new HashSet<URI>();
                    String clazz = null;


                    if (args == null || args.length > 2) continue;

                    List<ArgType> argTypes = new ArrayList<ArgType>();
                    int dataCount = 0;

                    for (Class arg : args) {
                        if (arg.getName().equals(Routing.class.getName())) {
                            argTypes.add(ArgType.ROUTE);
                        } else {
                            argTypes.add(ArgType.DATA);
                            clazz = arg.getName();
                            dataCount++;
                        }
                    }

                    if (dataCount > 1) continue;


                    for (String topic : listenTopics) {
                        uris.add(URI.create(topic));
                    }

                    Set<URI> sourceUris = new HashSet<URI>();

                    if (r.source() != null) {
                        for (String s : r.source()) {
                            sourceUris.add(URI.create(s));
                        }
                    }

                    recievers.add(new ListenContext(uris,sourceUris,new MethodContext(m,clazz, argTypes)));
                    topics.addAll(uris);
                }
            }
        }
    }

    public synchronized void detach() {
        subscriber = null;
        recievers.clear();
    }

    @Override
    public void dispatch(Message message) {;
        message.getRouting().setCurrentSender(mailbox);
        MessageContext messageContext = new MessageContext(message);
        dispatcher.dispatch(messageContext);
    }

    private enum ArgType {
        DATA,
        ROUTE
    }

    private class MethodContext {

        private Method method;
        private String argumentClass;
        private List<ArgType> argTypes;

        private MethodContext(Method method, String argumentClass, List<ArgType> argTypes) {
            this.method = method;
            this.argumentClass = argumentClass;
            this.argTypes = argTypes;
        }

        public Method getMethod() {
            return method;
        }

        public String getArgumentClass() {
            return argumentClass;
        }

        public List<ArgType> getArgTypes() {
            return argTypes;
        }
    }

    private class ListenContext {
        private Set<URI> dests = new HashSet<URI>();
        private Set<URI> sources = new HashSet<URI>();
        private MethodContext methodContext;

        private ListenContext(Set<URI> dests, Set<URI> sources, MethodContext methodContext) {
            this.dests = dests;
            this.sources = sources;
            this.methodContext = methodContext;
        }

        public Set<URI> getDests() {
            return dests;
        }

        public Set<URI> getSources() {
            return sources;
        }

        public MethodContext getMethodContext() {
            return methodContext;
        }
    }
}
