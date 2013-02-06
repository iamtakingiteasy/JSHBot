package org.eientei.jshbot.protocols.console;

import jline.console.ConsoleReader;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandContext;
import org.eientei.jshbot.api.tuiconsole.MountPoint;
import org.eientei.jshbot.api.dispatcher.Subscriber;
import org.eientei.jshbot.api.dispatcher.SubscriberContext;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.tui.utils.GenericActivatorThread;
import org.eientei.jshbot.tui.utils.GenericServiceListener;
import org.eientei.jshbot.protocols.console.commands.EchoCommand;
import org.eientei.jshbot.protocols.console.commands.HelpCommand;
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
    private Tree<String,ConsoleCommandContext> commandTree = new Tree<String, ConsoleCommandContext>();
    private Map<ConsoleCommand,ConsoleCommandContext> commandTreePath = new HashMap<ConsoleCommand, ConsoleCommandContext>();

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

        inlineMessage("Shell started");
    }

    @Override
    protected void deinitialize() {
        consoleCommandsListener.unregisterServiceListener();
        consoleCommandsListener.ungetAllServices();
        inlineMessage("Shell stopped");
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

    private void inlineMessage(String text) {
        try {
            consoleReader.resetPromptLine("","",0);
            consoleReader.println(text);
            consoleReader.setPrompt(prompt);
            consoleReader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean drainMessageQueue() {
        boolean wasMessages = false;
        Message message;
        while ((message = messagequeue.poll()) != null) {
            wasMessages = true;
            inlineMessage(message.getText());
        }
        return wasMessages;
    }

    private void evluate(String line) {
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
                        finalN.getData().getCommand().execute(arguments);
                    }
                }.start();
            } else {
                n.getData().getCommand().execute(arguments);
            }
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
            ConsoleCommandContext context = commandTreePath.remove(service);

            for (MountPoint p : context.getAllMountPoints()) {
                commandTree.remove(p.getMountPoint());
            }

        }

        @Override
        protected void addService(ConsoleCommand service) {
            ConsoleCommandContext context = new ConsoleCommandContextImpl(service,commandTree);
            commandTreePath.put(service,context);
            service.setup(context);
        }
    }
}
