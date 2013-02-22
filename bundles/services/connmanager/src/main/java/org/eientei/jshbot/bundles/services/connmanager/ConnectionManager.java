package org.eientei.jshbot.bundles.services.connmanager;

import org.apache.felix.ipojo.annotations.*;
import org.eientei.jshbot.bundles.api.message.*;
import org.eientei.jshbot.bundles.api.protomanager.ProtocolInstance;
import org.eientei.jshbot.bundles.api.protomanager.ProtocolManager;
import org.eientei.jshbot.bundles.utils.uri.UriUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-18
 * Time: 00:20
 */

@Provides
@Instantiate
@Component
public class ConnectionManager implements Subscriber {
    private Map<String,ProtocolManager> protocolManagers = new HashMap<String, ProtocolManager>();
    private List<ProtocolInstance> protocolInstances = new ArrayList<ProtocolInstance>();

    @Requires
    private Dispatcher dispatcher;

    @Bind(aggregate = true, optional = true)
    private void bindProtocolManager(ProtocolManager manager) {
        if (protocolManagers.get(manager.getScheme()) == null) {
            protocolManagers.put(manager.getScheme(),manager);
        }
    }

    @Unbind
    private void unbindProtocolManager(ProtocolManager manager) {
        protocolManagers.remove(manager.getScheme());
    }

    @Bind(aggregate = true, optional = true)
    private void bindProtocolInstance(ProtocolInstance instance) {
        protocolInstances.add(instance);
    }

    @Unbind
    private void unbindProtocolInstance(ProtocolInstance instance) {
        protocolInstances.remove(instance);
    }

    @Receives(value = {
            "connection-manager://connect"
    }, typeToken = UriToken.class)
    public void connectAction(URI dest) {
        ProtocolManager manager = protocolManagers.get(dest.getScheme());
        if (manager != null) {
            manager.newInstance(dest);
        }
    }

    @Receives(value = {
            "connection-manager://disconnect"
    }, typeToken = UriToken.class)
    public void disconnectAction(URI mask) {
        List<ProtocolInstance> instances = new ArrayList<ProtocolInstance>();
        for (ProtocolInstance i : protocolInstances) {
            if (UriUtils.match(i.getUri(),mask)) {
                instances.add(i);
            }
        }

        for (ProtocolInstance i : instances) {
            protocolInstances.remove(i);
            i.disconnect();
        }
    }

    @Receives(value = {
            "connection-manager://list-connections"
    }, typeToken = UriListToken.class)
    public void listConnectionsAction(Message<List<URI>> message) {
        List<URI> list = message.data();
        for (ProtocolInstance i : protocolInstances) {
            list.add(i.getUri());
        }
        message.returnResult(list);
    }

    @Receives(value = {
            "connection-manager://list-protomanagers"
    }, typeToken = StringListToken.class)
    public void listProtocolsAction(Message<List<String>> message) {
        List<String> list = message.data();
        list.addAll(protocolManagers.keySet());
        message.returnResult(list);
    }

    private static class UriToken extends MessageType<URI> { }
    private static class UriListToken extends MessageType<List<URI>> { }
    private static class StringListToken extends MessageType<List<String>> { }
}
