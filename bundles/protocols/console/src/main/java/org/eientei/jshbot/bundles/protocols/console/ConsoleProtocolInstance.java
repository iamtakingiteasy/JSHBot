package org.eientei.jshbot.bundles.protocols.console;

import jline.console.ConsoleReader;
import jline.console.history.History;
import org.apache.felix.ipojo.annotations.*;
import org.eientei.jshbot.bundles.api.consolecommand.ConsoleCommand;
import org.eientei.jshbot.bundles.api.message.Subscriber;
import org.eientei.jshbot.bundles.api.message.SubscriberContext;
import org.eientei.jshbot.bundles.api.protocol.ProtocolInstance;
import org.eientei.jshbot.bundles.utils.istreams.InterruptableInputStream;
import org.eientei.jshbot.bundles.utils.ithread.InterruptableThread;
import org.osgi.framework.BundleContext;

import java.io.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-11
 * Time: 12:32
 */
@Provides
@Component
public class ConsoleProtocolInstance extends InterruptableThread implements ProtocolInstance, Subscriber {
    private Queue<String> messages = new LinkedBlockingQueue<String>();
    private SubscriberContext subscriberContext;
    private InterruptableInputStream is;
    private ConsoleReader consoleReader;
    private String prompt = "JSHBot> ";
    private BundleContext bundleContext;
    private String historyFileName = "history.txt";
    private Tree<String,ConsoleCommandContext> commandTree = new Tree<String, ConsoleCommandContext>();


    public ConsoleProtocolInstance(BundleContext context) {
        bundleContext = context;
        System.out.println("Instance context");
    }

    @Bind(aggregate = true, optional = true)
    private void bindConsoleCommand(ConsoleCommand command) {
        List<MontPointContext> mounts = ReflectionUtils.getMounts(command.getClass());
        List<Method> completers = ReflectionUtils.getCompletors(command.getClass());
        ConsoleCommandContext context = new ConsoleCommandContext(command, completers);
        for (MontPointContext mpc : mounts) {
            commandTree.insert(mpc.getMonutPoint(), context);
        }
    }

    @Unbind
    private void unbindConsoleCommand(ConsoleCommand command) {
        List<MontPointContext> mounts = ReflectionUtils.getMounts(command.getClass());
        for (MontPointContext mpc : mounts) {
            commandTree.remove(mpc.getMonutPoint());
        }
    }

    @Override
    protected void initialize() {
        setTimeoutable(false);
        is = new InterruptableInputStream(System.in);
        try {
            consoleReader = new ConsoleReader(is, System.out);
            while (consoleReader.backspace());
            consoleReader.setPrompt(prompt);
            consoleReader.flush();
            consoleReader.setExpandEvents(false);
            consoleReader.addCompleter(new RootCompleter(commandTree));

            File historyFile = bundleContext.getDataFile(historyFileName);
            if (!historyFile.exists()) {
                historyFile.createNewFile();
            }

            BufferedReader reader = new BufferedReader(new FileReader(historyFile));
            String line;
            while ((line = reader.readLine()) != null) {
                consoleReader.getHistory().add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    protected void deinitialize() {
        subscriberContext.detach();
        is.makeStop();
        try {
            File historyFile = bundleContext.getDataFile(historyFileName);
            if (!historyFile.exists()) {
                historyFile.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(historyFile));
            for (History.Entry item : consoleReader.getHistory()) {
                writer.write(item.value().toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        processMessages();
        consoleReader.shutdown();
    }

    @Override
    protected void job() throws Exception {
        while (isRunning()) {
            String line = consoleReader.readLine();
            if (line != null && !line.isEmpty()) {
                evaluate(line);
            } else {
                processMessages();
            }
        }
    }

    private void evaluate(String line) {
        printMessage(line);
    }

    private void processMessages() {
        String message;
        while ((message = messages.poll()) != null) {
            printMessage(message);
        }
    }

    private void printMessage(String text) {
        try {
            while (consoleReader.backspace());
            System.out.println(text);
            consoleReader.setPrompt(prompt);
            consoleReader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void connect() {
        start();
    }

    @Override
    public void disconnect() {
        terminate();
    }

    @Override
    public void setSubscriberContext(SubscriberContext context) {
        System.out.println("Setuped context");
        this.subscriberContext = context;
    }
}
