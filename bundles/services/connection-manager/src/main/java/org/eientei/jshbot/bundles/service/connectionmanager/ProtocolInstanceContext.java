package org.eientei.jshbot.bundles.service.connectionmanager;

import org.eientei.jshbot.bundles.api.protocol.ProtocolInstance;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 17:45
 */
public class ProtocolInstanceContext {
    private URI uri;
    private ProtocolInstance protocolInstance;

    public ProtocolInstanceContext(URI uri, ProtocolInstance protocolInstance) {
        this.uri = uri;
        this.protocolInstance = protocolInstance;
    }

    public ProtocolInstance getProtocolInstance() {
        return protocolInstance;
    }

    public URI getUri() {
        return uri;
    }
}
