package org.eientei.jshbot.bundles.protocols.console;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.*;
import org.eientei.jshbot.bundles.api.message.Dispatcher;
import org.eientei.jshbot.bundles.api.message.MessageType;
import org.eientei.jshbot.bundles.api.protomanager.ProtocolManager;
import org.osgi.framework.ServiceReference;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-18
 * Time: 03:09
 */
@Provides
@Instantiate
@Component
public class ConsoleProtocolManager implements ProtocolManager {
    @Requires(from = "ConsoleProtocolInstance")
    private Factory instanceFactory;

    @Requires
    private Dispatcher dispatcher;


    @PostRegistration
    public void postRegistration(ServiceReference ref) {
        try {
            URI consoleURI = URI.create("console://stdin");
            List<URI> list = dispatcher.send(new ArrayList<URI>(), new MessageType<ArrayList<URI>>() { }, "connection-manager://list-connections").result();
            if (!list.contains(consoleURI)) {
                dispatcher.send(consoleURI, new MessageType<URI>() { }, "connection-manager://connect");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getScheme() {
        return "console";
    }

    @Override
    public void newInstance(URI uri) {
        try {
            Properties props = new Properties();
            props.put("uri",uri);
            instanceFactory.createComponentInstance(props);
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            unacceptableConfiguration.printStackTrace();
        } catch (MissingHandlerException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
}
