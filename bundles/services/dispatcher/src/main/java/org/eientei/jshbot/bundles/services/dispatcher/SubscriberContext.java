package org.eientei.jshbot.bundles.services.dispatcher;

import org.eientei.jshbot.bundles.api.message.Message;
import org.eientei.jshbot.bundles.api.message.Receives;
import org.eientei.jshbot.bundles.api.message.Subscriber;
import org.eientei.jshbot.bundles.utils.ithread.InterruptableThread;
import org.eientei.jshbot.bundles.utils.uri.UriUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-17
 * Time: 15:59
 */
public class SubscriberContext extends InterruptableThread {
    private final List<MethodContext> receivers = new ArrayList<MethodContext>();
    private final Queue<MessageImpl> messageQueue = new LinkedBlockingQueue<MessageImpl>();
    private ExecutorService pool;
    private boolean paused = true;
    private Subscriber subscriber;
    private DispatcherImpl dispatcher;
    private long pausedAt = new Date().getTime();

    public SubscriberContext(DispatcherImpl dispatcher) {
        this.dispatcher = dispatcher;
        setName("Subscriber context thread");
        setTimeoutable(true);
        setTimeout(100);
    }

    private void readClass(Class clazz) {
        receivers.clear();
        for (Method m : clazz.getMethods()) {
            String[] rtopics;
            Class dataTypeClass = null;

            if (m.isAnnotationPresent(Receives.class)) {
                Receives rann = m.getAnnotation(Receives.class);
                rtopics = rann.value();
                dataTypeClass = rann.typeToken();
            } else {
                continue;
            }

            Class[] args = m.getParameterTypes();

            List<MethodArgType> argTypes = new ArrayList<MethodArgType>();
            List<URI> topics = new ArrayList<URI>();

            boolean illegalState = false;

            for (Class a : args) {
                if (a.getName().equals(Message.class.getName())) {
                    argTypes.add(MethodArgType.MESSAGE);
                } else {
                    if (dataTypeClass == null) {
                        dataTypeClass = a;
                    } else {
                        Type t = ((ParameterizedType)dataTypeClass.getGenericSuperclass()).getActualTypeArguments()[0];
                        if (!((t instanceof Class) && ((Class) t).getName().equals(a.getName())) &&
                                !(t instanceof ParameterizedType &&  ((Class)((ParameterizedType) t).getRawType()).getName().equals(a.getName()))) {
                            illegalState = true;
                            break;
                        }
                        //if (!((t instanceof Class && ((Class) t).getName().equals(a.getName())) ||
//                                (t instanceof ParameterizedType &&  ((Class)((ParameterizedType) t).getRawType()).getName().equals(a.getName()))))  {

//                        }
                    }
                    argTypes.add(MethodArgType.DATA);
                }
            }

            if (illegalState) continue;

            if (rtopics != null) {
                for (String t : rtopics) {
                    topics.add(URI.create(t));
                }
            }

            receivers.add(new MethodContext(m,dataTypeClass,argTypes,topics));
        }
    }

    public boolean matches(MessageImpl message) {
        for (MethodContext context : receivers) {
            if (context.matches(message)) {
                return true;
            }
        }
        return false;
    }

    public void enqueue(MessageImpl message) {
        messageQueue.offer(message);
        wakeup();
    }

    @Override
    protected void job() throws Exception {
        MessageImpl message;
        while (!isPaused() && (message = messageQueue.poll()) != null) {
            dispatch(message);
        }
    }

    @Override
    protected void deinitialize() {
        receivers.clear();
        pause();
    }


    public void unpause(Subscriber subscriber) {
        this.subscriber = subscriber;
        readClass(subscriber.getClass());
        pool = Executors.newCachedThreadPool();
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public long pausedFor() {
        return new Date().getTime() - pausedAt;
    }

    public void pause() {
        paused = true;
        pausedAt = new Date().getTime();
        pool.shutdown();
        pool = null;
        subscriber = null;
    }

    private void dispatch(final MessageImpl message) {
        boolean delivered = false;
        for (final MethodContext context : receivers) {
            if (context.matches(message)) {
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        context.apply(message);
                    }
                });
                delivered = true;
            }
        }
        if (!delivered && message.timeLeft() > 0) {
            dispatcher.enqueue(message);
        }
    }

    private enum MethodArgType {
        DATA,
        MESSAGE
    }

    private class MethodContext {
        private final Method method;
        private final Class dataType;
        private final List<MethodArgType> argTypes;
        private final List<URI> topics;


        private MethodContext(Method method, Class dataType, List<MethodArgType> argTypes, List<URI> topics) {
            this.method = method;
            this.dataType = dataType;
            this.argTypes = argTypes;
            this.topics = topics;
        }

        public boolean matches(MessageImpl message) {
            for (URI t : topics) {
                if (UriUtils.match(message.topic(),t)) {
                    if (message.getDataTypeToken().isAssignableTo(dataType) || argTypes.isEmpty()) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void apply(MessageImpl message) {
            List<Object> params = new ArrayList<Object>(argTypes.size());
            for (MethodArgType arg : argTypes) {
                switch (arg) {
                    case DATA:
                        params.add(message.data());
                        break;
                    case MESSAGE:
                        params.add(message);
                        break;
                }
            }
            try {
                method.invoke(subscriber,params.toArray(new Object[params.size()]));
                message.markDelivered();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
