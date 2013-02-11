package org.eientei.jshbot.bundles.service.connectionmanager.actions;

import org.eientei.jshbot.bundles.api.message.Routing;
import org.eientei.jshbot.bundles.api.protocol.Protocol;
import org.eientei.jshbot.bundles.service.connectionmanager.ConnectionManager;
import org.eientei.jshbot.bundles.service.connectionmanager.ConnectionManagerAction;
import org.eientei.jshbot.bundles.service.connectionmanager.ProtocolInstanceContext;

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
        Protocol proto = connectionManager.getProtocol(scheme);
        if (proto != null) {
            ProtocolInstanceContext instance = new ProtocolInstanceContext(uri, proto.createInstance(uri));
            if (instance != null) {
                connectionManager.addInstance(instance);
                instance.getProtocolInstance().connect();
            }
        }
    }
}
