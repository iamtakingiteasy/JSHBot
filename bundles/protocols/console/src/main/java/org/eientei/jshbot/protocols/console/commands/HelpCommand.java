package org.eientei.jshbot.protocols.console.commands;

import org.eientei.jshbot.api.tuiconsole.ConsoleCommand;
import org.eientei.jshbot.api.tuiconsole.ConsoleCommandContext;
import org.eientei.jshbot.api.tuiconsole.MountPoint;
import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.api.message.Message;
import org.eientei.jshbot.tui.utils.GenericSingularServiceListener;
import org.eientei.jshbot.protocols.console.ShellUtils;
import org.eientei.jshbot.protocols.console.Tree;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 16:24
 */
public class HelpCommand implements ConsoleCommand {
    private GenericSingularServiceListener<Dispatcher> dispatcherService;
    private Tree<String,ConsoleCommandContext> commands;

    public HelpCommand(GenericSingularServiceListener<Dispatcher> dispatcherService, Tree<String, ConsoleCommandContext> commands) {
        this.dispatcherService = dispatcherService;
        this.commands = commands;
    }


    @Override
    public void setup(ConsoleCommandContext context) {
        context.addMountPoint(new MountPoint("Prints this help message",
                null,
                false,
                "help"));
    }

    @Override
    public void execute(List<String> arguments) {
        List<Tree.Node<String,ConsoleCommandContext>> nodes = new ArrayList<Tree.Node<String, ConsoleCommandContext>>();
        traverse(nodes,commands.getRoot());
        StringBuilder sb = new StringBuilder();

        int maxlen = 0;

        for (Tree.Node<String,ConsoleCommandContext> n : nodes) {
            String str = ShellUtils.concat(n.getPath());
            if (str.length() > maxlen) {
                maxlen = str.length();
            }
        }

        for (Tree.Node<String,ConsoleCommandContext> n : nodes) {
            sb.append("    ");
            int m = 0;
            for (String p : n.getPath()) {
                m += p.length() + 1;
                sb.append(p);
                sb.append(" ");
            }

            for (int i = m; i <= maxlen; i++) {
                sb.append(" ");
            }

            sb.append("    ");
            sb.append(n.getData().getMountPoint(n.getPath()).getDescription());
            sb.append("\n");
        }

        Message message = new Message(URI.create("console://stdin"), URI.create("console://stdout"), sb.toString());

        dispatcherService.getOrWaitForServiceInstance().dispatch(message);
    }

    private void traverse(List<Tree.Node<String,ConsoleCommandContext>> nodes, Tree.Node<String,ConsoleCommandContext> root) {
        for (Tree.Node<String,ConsoleCommandContext> n : root) {
            if (!n.isEmpty()) {
                nodes.add(n);
            }
            traverse(nodes,n);
        }
    }
}
