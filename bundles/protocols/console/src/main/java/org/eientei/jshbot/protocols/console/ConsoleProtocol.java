package org.eientei.jshbot.protocols.console;

import jline.console.ConsoleReader;
import org.eientei.jshbot.api.dispatcher.Subscriber;
import org.eientei.jshbot.api.dispatcher.SubscriberContext;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.bundles.utils.GenericActivatorThread;
import org.eientei.jshbot.bundles.utils.GenericServiceListener;
import org.eientei.jshbot.protocols.console.api.ConsoleCommand;
import org.eientei.jshbot.protocols.console.commands.EchoCommand;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 15:31
 */
public class ConsoleProtocol extends GenericActivatorThread implements Subscriber {
    private ConsoleCommandServiceListener consoleCommandsListener;
    private InterruptableInputStream inputStream = new InterruptableInputStream(System.in);
    private String prompt = "JSHBot> ";
    private ConsoleReader consoleReader;
    private Queue<Message> messagequeue = new LinkedBlockingQueue<Message>();
    private Tree<String,ConsoleCommandContextImpl> commandTree = new Tree<String, ConsoleCommandContextImpl>();
    private Map<ConsoleCommand,List<List<String>>> commandTreePath = new HashMap<ConsoleCommand, List<List<String>>>();

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
        // register commands
        /*
        builtinCommands.add(bundleContext.registerService(ConsoleCommand.class, new BundleCommand(dispatcherService, bundleContext), null));
        builtinCommands.add(bundleContext.registerService(ConsoleCommand.class, new HelpCommand(dispatcherService, consoleCommands), null));
        */

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
    }

    @Override
    protected void deinitialize() {
        consoleCommandsListener.unregisterServiceListener();
        consoleCommandsListener.ungetAllServices();
    }

    @Override
    protected void doIterativeJob() {
        while (isRunning()) {
            try {
                String line = consoleReader.readLine();

                boolean wasMessages = drainMessageQueue();

                if (line != null && !line.isEmpty()) {
                    try {
                        evluate(line);
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


    private boolean drainMessageQueue() {
        boolean wasMessages = false;
        Message message;
        while ((message = messagequeue.poll()) != null) {
            wasMessages = true;
            try {
                consoleReader.resetPromptLine("","",0);
                consoleReader.println(message.getText());
                consoleReader.setPrompt(prompt);
                //consoleReader.resetPromptLine(prompt,"",0);
                consoleReader.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wasMessages;
    }

    private void evluate(String line) {
        List<String> keys = ShellUtils.shellSplit(line);
        Tree.Node<String,ConsoleCommandContextImpl> n = commandTree.getRoot();
        boolean traversing = true;
        List<String> arguments = new ArrayList<String>();

        for (String key : keys) {
            if (traversing) {
                Tree.Node<String,ConsoleCommandContextImpl> c = n.getChild(key);
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
            n.getData().getCommand().execute(arguments);
        } else {
            String commandLine = ShellUtils.concat(keys);
            Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), "No such command: " + commandLine);

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
        subscriberContext.addTopic(URI.create("console://stdout"));
        subscriberContext.addTopic(URI.create("console://stderr"));
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
        protected void removeService(ConsoleCommand service) {
            List<List<String>> paths = commandTreePath.remove(service);
            commandTreePath.remove(service);
            for (List<String> p : paths) {
                commandTree.remove(p);
            }
        }

        @Override
        protected void addService(ConsoleCommand service) {
            ConsoleCommandContextImpl context = new ConsoleCommandContextImpl(service);
            commandTreePath.put(service, context.getCommandMountPoints());
            for (List<String> p : context.getCommandMountPoints()) {
                commandTree.insert(context,p);
            }
        }
    }
}
