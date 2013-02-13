package org.eientei.jshbot.bundles.api.protocol;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 16:29
 */
public interface ProtocolManager {
    String[] autoConnectURIs();
    String getScheme();
    ProtocolInstance createInstance(URI uri);
}
