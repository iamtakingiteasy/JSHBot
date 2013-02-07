package org.eientei.jshbot.bundles.utils;

import org.osgi.framework.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-03
 * Time: 18:14
 */
public abstract class GenericServiceListener<T> implements ServiceListener {
    private ConcurrentMap<ServiceReference<?>, T> map = new ConcurrentHashMap<ServiceReference<?>, T>();
    private BundleContext bundleContext;
    private String serviceClassName;

    public GenericServiceListener(String serviceClassName, BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.serviceClassName = serviceClassName;
    }

    public void registerServiceListener() {
        try {
            bundleContext.addServiceListener(this,"(objectClass=" + serviceClassName + ")");
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
            return;
        }
    }

    public void unregisterServiceListener() {
        bundleContext.removeServiceListener(this);
    }

    public void fetchAllAvailableServices() {
        ungetAllServices();
        try {
            ServiceReference<?>[] serviceRefs = bundleContext.getAllServiceReferences(serviceClassName,null);
            if (serviceRefs != null) {
                for (ServiceReference<?> ref : serviceRefs) {
                    getService(ref);
                }
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void ungetAllServices() {
        for (ServiceReference<?> ref : map.keySet()) {
            ungetService(ref);
        }
    }

    private void ungetService(ServiceReference<?> serviceReference) {
        if (map.containsKey(serviceReference)) {
            T service = map.get(serviceReference);
            String serviceSymbolicName = serviceReference.getBundle().getSymbolicName() + "|" + service.getClass().getName();
            removeService(service, serviceSymbolicName);
            map.remove(serviceReference);
            bundleContext.ungetService(serviceReference);
        }
    }

    private void getService(ServiceReference<?> serviceReference) {
        if (!map.containsKey(serviceReference)) {
            T service = (T) bundleContext.getService(serviceReference);
            if (service != null) {
                map.put(serviceReference,service);
                String serviceSymbolicName = serviceReference.getBundle().getSymbolicName() + "|" + service.getClass().getName();
                addService(service, serviceSymbolicName);
            }
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        switch (event.getType()) {
            case ServiceEvent.MODIFIED:
                ungetService(event.getServiceReference());
            case ServiceEvent.REGISTERED:
                getService(event.getServiceReference());
                break;

            case ServiceEvent.MODIFIED_ENDMATCH:
            case ServiceEvent.UNREGISTERING:
                ungetService(event.getServiceReference());
                break;
        }
    }


    protected abstract void removeService(T service, String serviceSymbolicName);
    protected abstract void addService(T service, String serviceSymbolicName);
}
