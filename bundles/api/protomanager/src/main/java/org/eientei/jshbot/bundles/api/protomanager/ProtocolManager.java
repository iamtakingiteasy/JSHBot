package org.eientei.jshbot.bundles.api.protomanager;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-18
 * Time: 00:50
 */
public interface ProtocolManager {
    String getScheme();
    void newInstance(URI uri);
}
