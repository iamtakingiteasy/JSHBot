package org.eientei.jshbot.bundles.protocols.console;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.api.PrimitiveComponentType;
import org.apache.felix.ipojo.api.Service;
import org.eientei.jshbot.bundles.api.protocol.ProtocolInstance;
import org.eientei.jshbot.bundles.api.protocol.ProtocolManager;
import org.osgi.framework.BundleContext;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-11
 * Time: 12:29
 */
@Provides
@Instantiate
@Component
public class ConsoleProtocolManager implements ProtocolManager {
    private ConsoleProtocolInstance instance = null;
    private BundleContext bundleContext = null;

    public ConsoleProtocolManager(BundleContext context) {
        bundleContext = context;
    }

    @Override
    public String[] autoConnectURIs() {
        return new String[] { getScheme() + "://stdin" };
    }

    @Override
    public String getScheme() {
        return "console";
    }

    @Override
    public ProtocolInstance createInstance(URI uri) {
        if (instance == null) {
            //instance = new ConsoleProtocolInstance(bundleContext);
            try {
                new PrimitiveComponentType()
                        .setBundleContext(bundleContext)
                        .setClassName(ConsoleProtocolInstance.class.getName())
                        .addService(new Service())
                        .createInstance().getFactory();
            } catch (UnacceptableConfiguration unacceptableConfiguration) {
                unacceptableConfiguration.printStackTrace();
            } catch (MissingHandlerException e) {
                e.printStackTrace();
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
