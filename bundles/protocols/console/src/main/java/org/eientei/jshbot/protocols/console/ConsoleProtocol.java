package org.eientei.jshbot.protocols.console;

import jline.console.ConsoleReader;
import jline.console.history.History;
import org.eientei.jshbot.api.dispatcher.Subscriber;
import org.eientei.jshbot.api.dispatcher.SubscriberContext;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandContext;
import org.eientei.jshbot.api.tuiconsole.MountPoint;
import org.eientei.jshbot.bundles.utils.GenericProducerThread;
import org.eientei.jshbot.bundles.utils.GenericServiceListener;
import org.eientei.jshbot.bundles.utils.InterruptableInputStream;
import org.eientei.jshbot.protocols.console.commands.EchoCommand;
import org.eientei.jshbot.protocols.console.commands.HelpCommand;
import org.osgi.framework.BundleContext;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 15:31
 */
public class ConsoleProtocol extends GenericProducerThread implements Subscriber {
    private ConsoleCommandServiceListener consoleCommandsListener;
    private InterruptableInputStream inputStream = new InterruptableInputStream(System.in);
    private String prompt = "JSHBot> ";
    private ConsoleReader consoleReader;
    private Queue<Message> messagequeue = new LinkedBlockingQueue<Message>();
    private Tree<String,ConsoleCommandContext> commandTree = new Tree<String, ConsoleCommandContext>();
    private Map<ConsoleCommand,ConsoleCommandContext> commandTreePath = new HashMap<ConsoleCommand, ConsoleCommandContext>();
    private SubscriberContext subscriberContext;

    public ConsoleProtocol(BundleContext bundleContext) {
        super(bundleContext, false);
    }

    @Override
    protected void initialize() {
        bundleContext.registerService(Subscriber.class, this, null);
        consoleCommandsListener = new ConsoleCommandServiceListener(bundleContext);
        consoleCommandsListener.registerServiceListener();
        consoleCommandsListener.fetchAllAvailableServices();


        bundleContext.registerService(ConsoleCommand.class, new EchoCommand(dispatcherService), null);
        bundleContext.registerService(ConsoleCommand.class, new HelpCommand(dispatcherService, commandTree), null);

        try {
            consoleReader= new ConsoleReader(inputStream, System.out);
            consoleReader.setPrompt("");
            consoleReader.resetPromptLine("","",0);
            consoleReader.setPrompt(prompt);
            consoleReader.setExpandEvents(false);
            consoleReader.addCompleter(new RootCompleter(commandTree));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File historyFile = bundleContext.getDataFile("history");
        if (!historyFile.exists()) {
            try {
                historyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(historyFile));
            String line;
            while ((line = reader.readLine()) != null) {
                consoleReader.getHistory().add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void deinitialize() {
        subscriberContext.detach();

        while (drainMessageQueue());

        consoleCommandsListener.unregisterServiceListener();
        consoleCommandsListener.ungetAllServices();
        File historyFile = bundleContext.getDataFile("history");
        BufferedWriter writer = null;
        try {
             writer = new BufferedWriter(new FileWriter(historyFile));
            for (History.Entry item : consoleReader.getHistory()) {
                writer.write(item.value().toString() + "\n");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doIterativeJob() {
        while (isRunning()) {
            try {
                String line = consoleReader.readLine();

                boolean wasMessages = drainMessageQueue();

                if (line != null && !line.isEmpty()) {
                    try {
                        evaluate(line);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } else if (line == null && !wasMessages) {
                    consoleReader.println();
                }

            } catch (IOException e1) {
            } catch (UnsupportedOperationException e2) {
            }
        }
    }

    private void inlineMessage(String text) {
        try {
            consoleReader.resetPromptLine("","",0);
            consoleReader.println(text);
            consoleReader.setPrompt(prompt);
            consoleReader.flush();
        } catch (IOException e) {
        }
    }

    private boolean drainMessageQueue() {
        boolean wasMessages = false;
        Message message;
        while ((message = messagequeue.poll()) != null) {
            wasMessages = true;
            inlineMessage(message.getText());
            message.markDelivered();
        }
        return wasMessages;
    }

    private void evaluate(String line) {
        List<String> keys = ShellUtils.shellSplit(line);
        Tree.Node<String,ConsoleCommandContext> n = commandTree.getRoot();
        boolean traversing = true;
        final List<String> arguments = new ArrayList<String>();

        for (String key : keys) {
            if (traversing) {
                Tree.Node<String,ConsoleCommandContext> c = n.getChild(key);
                if (c == null) {
                    traversing = false;
                    arguments.add(key);
                } else {
                    n = c;
                }
            } else {
                arguments.add(key);
            }
        }

        if (n.getData() != null) {
            if (n.getData().getMountPoint(n.getPath()).isAutoThreading()) {
                final Tree.Node<String, ConsoleCommandContext> finalN = n;
                new Thread() {
                    @Override
                    public void run() {
                        finalN.getData().getCommand().execute(finalN.getPath(),arguments);
                    }
                }.start();
            } else {
                n.getData().getCommand().execute(n.getPath(), arguments);
            }
        } else {
            String commandLine = ShellUtils.concat(keys);
            Message message = new Message("console://stdin", "console://stdout", "No such command: " + commandLine);

            dispatcherService.getOrWaitForServiceInstance().dispatch(message);
        }
    }

    @Override
    public void consume(Message message) {
        messagequeue.offer(message);
        inputStream.makeReturn();
    }

    @Override
    public void registration(SubscriberContext subscriberContext) {
        this.subscriberContext = subscriberContext;
        subscriberContext.addTopic("console://stdout");
        subscriberContext.addTopic("console://stderr");
    }

    @Override
    protected void termination() {
        inputStream.interrupt();
        consoleReader.shutdown();
    }

    private class ConsoleCommandServiceListener extends GenericServiceListener<ConsoleCommand> {

        public ConsoleCommandServiceListener(BundleContext bundleContext) {
            super(ConsoleCommand.class.getName(), bundleContext);
        }

        @Override
        protected void removeService(ConsoleCommand service, String serviceSymbolicName) {
            ConsoleCommandContext context = commandTreePath.remove(service);

            for (MountPoint p : context.getAllMountPoints()) {
                commandTree.remove(p.getMountPoint());
            }

        }

        @Override
        protected void addService(ConsoleCommand service, String serviceSymbolicName) {
            ConsoleCommandContext context = new ConsoleCommandContextImpl(service,commandTree);
            commandTreePath.put(service,context);
            service.setup(context);
        }
    }
}
