package org.eientei.jshbot.bundles.protocols.console;

import jline.console.ConsoleReader;
import org.apache.felix.ipojo.annotations.*;
import org.eientei.jshbot.bundles.api.consolecmd.CommandContext;
import org.eientei.jshbot.bundles.api.consolecmd.ConsoleCommand;
import org.eientei.jshbot.bundles.api.consolecmd.ConsoleCommandMountPoint;
import org.eientei.jshbot.bundles.api.message.*;
import org.eientei.jshbot.bundles.api.protomanager.ProtocolInstance;
import org.eientei.jshbot.bundles.utils.istream.InterruptableInputStream;
import org.eientei.jshbot.bundles.utils.ithread.InterruptableThread;
import org.eientei.jshbot.bundles.utils.shell.ShellUtils;
import org.eientei.jshbot.bundles.utils.tree.Tree;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-18
 * Time: 03:10
 */
@Provides
@Component(name = "ConsoleProtocolInstance")
public class ConsoleProtocolInstance extends InterruptableThread implements ProtocolInstance, Subscriber {
    private static final String prompt = "JSHBot> ";
    private Queue<String> messageQueue = new LinkedBlockingQueue<String>();
    private ConsoleReader consoleReader;
    private InterruptableInputStream inputStream = new InterruptableInputStream(System.in);
    private Tree<String,CommandContext> commands = new Tree<String, CommandContext>();
    private ExecutorService pool;

    @Requires
    private Dispatcher dispatcher;

    @ServiceProperty(mandatory = true)
    private URI uri;

    @PostRegistration
    private void postRegistration(ServiceReference ref) {
        connect();

    }

    @PostUnregistration
    private void postUnregistration(ServiceReference ref) {
        disconnect();
    }

    @Bind(aggregate = true, optional = true)
    public void bindConsoleCommand(ConsoleCommand command) {
        for (ConsoleCommandMountPoint mp : command.mountPoints()) {
            CommandContext context = new CommandContext(command,mp);
            commands.insert(mp.getPath(),context);
        }
    }

    @Unbind
    public void unbindConsoleCommand(ConsoleCommand command) {
        for (ConsoleCommandMountPoint mp : command.mountPoints()) {
            commands.remove(mp.getPath());
        }
    }


    private void connect() {
        try {
            consoleReader = new ConsoleReader(inputStream,System.out);
            while (consoleReader.backspace());
            consoleReader.setPrompt(prompt);
            consoleReader.flush();
            consoleReader.setExpandEvents(false);
            pool = Executors.newCachedThreadPool();
            consoleReader.addCompleter(new RootCompleter(commands));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setTimeoutable(false);
        start();
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public void disconnect() {
        requestStop();
        inputStream.makeReturn();
        terminate();
        consoleReader.shutdown();
        pool.shutdown();
        pool = null;
    }

    @Override
    protected void job() throws Exception {
        while (isRunning()) {
            String line = consoleReader.readLine();
            if (line == null) {
                processMessages();
            } else {
                evalLine(line);
            }
        }
    }

    private void evalLine(String line) {
        List<String> toks = ShellUtils.shellSplit(line);
        final List<String> arg0 = new ArrayList<String>();
        final List<String> argv = new ArrayList<String>();
        Tree.Node<String,CommandContext> n = commands.getRoot();
        boolean traversing = true;

        for (String a : toks) {
            if (traversing) {
                Tree.Node<String,CommandContext> c = n.getChild(a);
                if (c == null) {
                    traversing = false;
                    argv.add(a);
                } else {
                    n = c;
                    arg0.add(a);
                }
            } else {
                argv.add(a);
            }
        }

        if (n.getData() != null) {
            final CommandContext context = n.getData();
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    context.getCommand().execute(arg0, argv);
                }
            });
        }
    }

    private void processMessages() {
        String message;
        boolean wasPrint = false;
        while ((message = messageQueue.poll()) != null) {
            wasPrint = true;
            printStdoutMessage(message);
        }
        if (!wasPrint && isRunning()) {
            System.out.println();
        }
    }

    private void printStdoutMessage(String s) {
        try {
            consoleReader.resetPromptLine("","",0);
            System.out.println(s);
            consoleReader.setPrompt(prompt);
            consoleReader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Receives(value = {
            "console://stdout"
    }, typeToken = StringToken.class)
    public void enqueueMessage(String message) {
        messageQueue.offer(message);
        inputStream.makeReturn();
    }

    @Receives(value = {
            "console://commands/list"
    }, typeToken = CommandTreeToken.class)
    public void getCommandTree(Message<Tree<String,CommandContext>> message) {
        Tree<String,CommandContext> tree = new Tree<String, CommandContext>();
        tree.addAll(commands);
        message.returnResult(tree);
    }

    public Tree<String,CommandContext> commandTree() {
        return commands;
    }

    private static class StringToken extends MessageType<String> { }
    private static class CommandTreeToken extends MessageType<Tree<String,CommandContext>> { }
}
