package org.eientei.jshbot.connectors.manager;

import org.eientei.jshbot.api.dispatcher.Subscriber;
import org.eientei.jshbot.api.dispatcher.SubscriberContext;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.bundles.utils.GenericProducerThread;
import org.eientei.jshbot.bundles.utils.InterruptableInputStream;
import org.osgi.framework.BundleContext;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-08
 * Time: 01:03
 */
public class PlainConnector extends GenericProducerThread implements Subscriber {
    private URI serverUri;
    private BundleContext bundleContext;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private SubscriberContext subscriberContext;
    private Queue<Message> messages = new LinkedBlockingQueue<Message>();
    private InterruptableInputStream interruptableStream;

    public PlainConnector(URI serverUri, BundleContext bundleContext) {
        super(bundleContext,false);
        this.serverUri = serverUri;
        this.bundleContext = bundleContext;
    }


    @Override
    protected void initialize() {
        if (!connect()) {
            Message message = new Message(serverUri.toString(),"irc-manager://disconnect",serverUri.toString());
            dispatcherService.getOrWaitForServiceInstance().dispatch(message);
        } else {
            bundleContext.registerService(Subscriber.class, this, null);
        }
    }

    private boolean connect() {
        try {
            socket = new Socket(serverUri.getHost(),serverUri.getPort());
            socket.setReuseAddress(true);
            interruptableStream = new InterruptableInputStream(socket.getInputStream());
            reader = new BufferedReader(new InputStreamReader(interruptableStream));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void deinitialize() {
        subscriberContext.detach();
    }

    @Override
    protected void doIterativeJob() {
        while (isRunning()) {
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
            }

            if (line != null) {
                Message message = new Message(serverUri,null,line);
                dispatcherService.getOrWaitForServiceInstance().dispatch(message);
            }

            Message message = null;
            while ((message = messages.poll()) != null) {
                try {
                    writer.write(message.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void consume(Message message) {
        messages.offer(message);
        if (interruptableStream != null) {
            interruptableStream.makeReturn();
        }
    }

    @Override
    protected void termination() {
        interruptableStream.interrupt();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void registration(SubscriberContext subscriberContext) {
        this.subscriberContext = subscriberContext;
        subscriberContext.addTopic(serverUri.getScheme() + "-connector://" + serverUri.getAuthority());
    }
}
