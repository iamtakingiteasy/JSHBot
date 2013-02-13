package org.eientei.jshbot.bundles.services.connectionmanager.actions;

import org.eientei.jshbot.bundles.api.message.Routing;
import org.eientei.jshbot.bundles.api.protocol.ProtocolManager;
import org.eientei.jshbot.bundles.services.connectionmanager.ConnectionManager;
import org.eientei.jshbot.bundles.services.connectionmanager.ConnectionManagerAction;
import org.eientei.jshbot.bundles.services.connectionmanager.ProtocolInstanceContext;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 17:14
 */
public class ConnectAction extends ConnectionManagerAction {
    public ConnectAction(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public void perform(Routing routing, URI uri) {

        String scheme = uri.getScheme();
        ProtocolManager proto = connectionManager.getProtocol(scheme);
        if (proto != null) {
            ProtocolInstanceContext instance = new ProtocolInstanceContext(uri, proto.createInstance(uri));
            if (instance != null) {
                connectionManager.addInstance(instance);
                instance.getProtocolInstance().connect();
            }
        }
    }
}
