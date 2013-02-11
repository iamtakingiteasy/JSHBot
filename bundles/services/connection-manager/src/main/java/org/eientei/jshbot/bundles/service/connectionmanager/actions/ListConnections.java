package org.eientei.jshbot.bundles.service.connectionmanager.actions;

import org.eientei.jshbot.bundles.api.message.Message;
import org.eientei.jshbot.bundles.api.message.Routing;
import org.eientei.jshbot.bundles.service.connectionmanager.ConnectionManager;
import org.eientei.jshbot.bundles.service.connectionmanager.ConnectionManagerAction;
import org.eientei.jshbot.bundles.service.connectionmanager.ProtocolInstanceContext;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 17:54
 */
public class ListConnections extends ConnectionManagerAction {
    public ListConnections(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public void perform(Routing routing, URI uri) {
        List<URI> uris = new ArrayList<URI>();
        for (List<ProtocolInstanceContext> instanceList : connectionManager.getInstances().values()) {
            for (ProtocolInstanceContext instance : instanceList) {
                uris.add(instance.getUri());
            }
        }
        Message<List<URI>> message = new Message<List<URI>>(URI.create("connection-manager://list-connections"), routing.getCurrentSender(), uris);
        connectionManager.getSubscriberContext().dispatch(message);
    }
}
