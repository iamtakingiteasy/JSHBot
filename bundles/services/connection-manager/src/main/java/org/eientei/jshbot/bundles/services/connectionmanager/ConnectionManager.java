package org.eientei.jshbot.bundles.services.connectionmanager;

import org.apache.felix.ipojo.annotations.*;
import org.eientei.jshbot.bundles.api.message.Routing;
import org.eientei.jshbot.bundles.api.message.Subscriber;
import org.eientei.jshbot.bundles.api.message.SubscriberContext;
import org.eientei.jshbot.bundles.api.message.annotations.Recieves;
import org.eientei.jshbot.bundles.api.protocol.ProtocolManager;
import org.eientei.jshbot.bundles.services.connectionmanager.actions.ConnectAction;
import org.eientei.jshbot.bundles.services.connectionmanager.actions.DisconnectAction;
import org.eientei.jshbot.bundles.services.connectionmanager.actions.ListConnections;
import org.eientei.jshbot.bundles.utils.ithread.InterruptableThread;
import org.eientei.jshbot.bundles.utils.uri.UriUtils;
import org.osgi.framework.ServiceReference;

import java.net.URI;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 14:08
 */
@Provides
@Instantiate
@Component
public class ConnectionManager extends InterruptableThread implements Subscriber {
    private SubscriberContext subscriberContext;
    private Queue<Action> messageQueue = new LinkedBlockingQueue<Action>();
    private Map<String, ProtocolManager> protocols = new HashMap<String, ProtocolManager>();
    private Map<String, ConnectionManagerAction> actions = new HashMap<String, ConnectionManagerAction>();
    private Map<String, List<ProtocolInstanceContext>> instances = new HashMap<String, List<ProtocolInstanceContext>>();

    public ConnectionManager() {
        actions.put("connect", new ConnectAction(this));
        actions.put("disconnect", new DisconnectAction(this));
        actions.put("list-connections", new ListConnections(this));
    }

    @Bind(aggregate = true, optional = true)
    private void bindProtocol(ProtocolManager protocolManager) {
        protocols.put(protocolManager.getScheme(), protocolManager);
        String[] uris = protocolManager.autoConnectURIs();
        if (uris != null && uris.length > 0) {
            for (String uri : uris) {
                messageQueue.add(new Action(new Routing("connection-manager://autoconnect", "connection-manager://connect"), URI.create(uri)));
            }
            wakeup();
        }
    }

    @Unbind
    private void unbindProtocol(ProtocolManager protocolManager) {
        ProtocolManager proto = protocols.remove(protocolManager.getScheme());
        List<ProtocolInstanceContext> pil = instances.remove(proto.getScheme());
        if (pil != null) {
            for (ProtocolInstanceContext pi : pil) {
                pi.getProtocolInstance().disconnect();
            }
        }
    }

    @PostRegistration
    public void registration(ServiceReference ref) {
        start();
    }

    @PostUnregistration
    public void unregistration(ServiceReference ref) {
        terminate();
    }

    @Override
    protected synchronized void job() throws Exception {
        Action message;
        while ((message = messageQueue.poll()) != null) {
            String actionName = message.getRouting().getDest().getAuthority();
            ConnectionManagerAction action = actions.get(actionName);
            if (action != null) {
                action.perform(message.getRouting(), message.getData());
            }
        }
    }

    @Recieves(value = {
            "connection-manager://connect/*",
            "connection-manager://disconnect/*",
            "connection-manager://list-connections/*"
    })
    public void accept(URI message, Routing routing) {
        messageQueue.offer(new Action(routing,message));
        wakeup();
    }

    public ProtocolManager getProtocol(String scheme) {
        return protocols.get(scheme);
    }

    public void addInstance(ProtocolInstanceContext instance) {
        List<ProtocolInstanceContext> pil = instances.get(instance.getUri().getScheme());
        if (pil == null) {
            pil = new ArrayList<ProtocolInstanceContext>();
            instances.put(instance.getUri().getScheme(), pil);
        }
        pil.add(instance);
    }

    public void removeInstance(URI uri) {
        for (List<ProtocolInstanceContext> pil : instances.values()) {
            List<ProtocolInstanceContext> toRemove = new ArrayList<ProtocolInstanceContext>();
            for (ProtocolInstanceContext pi : pil) {
                if (UriUtils.match(pi.getUri(), uri)) {
                    pi.getProtocolInstance().disconnect();
                    toRemove.add(pi);
                }
            }
            pil.removeAll(toRemove);
        }
    }

    public Map<String, List<ProtocolInstanceContext>> getInstances() {
        return Collections.unmodifiableMap(instances);
    }

    @Override
    public void setSubscriberContext(SubscriberContext context) {
        subscriberContext = context;
    }

    public SubscriberContext getSubscriberContext() {
        return subscriberContext;
    }

    private class Action {
        private final URI data;
        private final Routing routing;

        private Action(Routing routing, URI data) {
            this.data = data;
            this.routing = routing;
        }

        public URI getData() {
            return data;
        }

        public Routing getRouting() {
            return routing;
        }
    }
}

