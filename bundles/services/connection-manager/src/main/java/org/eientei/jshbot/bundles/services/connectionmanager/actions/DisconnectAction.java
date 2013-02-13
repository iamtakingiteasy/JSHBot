package org.eientei.jshbot.bundles.services.connectionmanager.actions;

import org.eientei.jshbot.bundles.api.message.Routing;
import org.eientei.jshbot.bundles.services.connectionmanager.ConnectionManager;
import org.eientei.jshbot.bundles.services.connectionmanager.ConnectionManagerAction;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 17:26
 */
public class DisconnectAction extends ConnectionManagerAction {
    public DisconnectAction(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public void perform(Routing routing, URI uri) {
        connectionManager.removeInstance(uri);
    }
}
