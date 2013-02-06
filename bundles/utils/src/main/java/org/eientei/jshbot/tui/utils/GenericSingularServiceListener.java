package org.eientei.jshbot.tui.utils;

import org.osgi.framework.*;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 14:16
 */
public class GenericSingularServiceListener<T> implements ServiceListener {
    private BundleContext bundleContext;
    private T service;
    private final Object monitor = new Object();
    private String serviceClassName;
    private ServiceReference<?> serviceReference;

    public GenericSingularServiceListener(String serviceClassName, BundleContext bundleContext) {
        this.serviceClassName = serviceClassName;
        this.bundleContext = bundleContext;
    }

    public void registerAsServiceListener() {
        try {
            bundleContext.addServiceListener(this,"(objectClass=" + serviceClassName + ")");
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
            return;
        }
        ServiceReference<?> ref = bundleContext.getServiceReference(serviceClassName);
        if (ref != null) {
            getService(ref);
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        serviceReference = event.getServiceReference();
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

    public void ungetService() {
        if (service != null) {
            removeService(service);
            service = null;
            bundleContext.ungetService(serviceReference);
        }
    }

    public void ungetService(ServiceReference reference) {
        if (service != null) {
            removeService(service);
            service = null;
            bundleContext.ungetService(reference);
        }
    }

    public void getService(ServiceReference<?> serviceReference) {
        if (service == null) {
            this.serviceReference = serviceReference;
            service = (T) bundleContext.getService(serviceReference);
            addService(service);
            synchronized (monitor) {
                monitor.notifyAll();
            }
        }
    }

    protected void addService(T service) {

    }
    protected void removeService(T service) {

    }

    public T getServiceInstance() {
        return service;
    }

    public T getOrWaitForServiceInstance() {
        while (service == null) {
            synchronized (monitor) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }
        }
        return service;
    }

    public void unregisterServiceListener() {
        bundleContext.removeServiceListener(this);
    }
}


