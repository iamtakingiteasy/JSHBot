package org.eientei.jshbot.bundles.service.connectionmanager;

import org.eientei.jshbot.bundles.api.message.Routing;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 17:12
 */
public abstract class ConnectionManagerAction {
    protected ConnectionManager connectionManager;

    public ConnectionManagerAction(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public abstract void perform(Routing routing, URI data);
}
