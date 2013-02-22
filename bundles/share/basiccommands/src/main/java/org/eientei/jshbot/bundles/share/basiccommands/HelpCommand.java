package org.eientei.jshbot.bundles.share.basiccommands;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.eientei.jshbot.bundles.api.consolecmd.ConsoleCommand;
import org.eientei.jshbot.bundles.api.consolecmd.ConsoleCommandMountPoint;
import org.eientei.jshbot.bundles.api.message.Dispatcher;
import org.eientei.jshbot.bundles.api.message.MessageType;
import org.eientei.jshbot.bundles.api.consolecmd.CommandContext;
import org.eientei.jshbot.bundles.utils.shell.ShellUtils;
import org.eientei.jshbot.bundles.utils.tree.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-22
 * Time: 19:46
 */
@Provides
@Instantiate
@Component
public class HelpCommand implements ConsoleCommand {
    @Requires
    private Dispatcher dispatcher;

    @Override
    public List<ConsoleCommandMountPoint> mountPoints() {
        List<ConsoleCommandMountPoint> mps = new ArrayList<ConsoleCommandMountPoint>();
        mps.add(new ConsoleCommandMountPoint("Prints brief help for all available commands", "help"));
        return mps;
    }

    @Override
    public void execute(List<String> arg0, List<String> argv) {
        Tree<String,CommandContext> tree = null;
        try {
            tree = dispatcher.send(null, new MessageType<Tree<String,CommandContext>>() { }, "console://commands/list").result();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        List<Tree.Node<String,CommandContext>> nodes = tree.flattern();
        StringBuilder sb = new StringBuilder();

        int maxlen = 0;

        for (Tree.Node<String,CommandContext> n : nodes) {
            String str = ShellUtils.concat(n.getPath());
            if (str.length() > maxlen) {
                maxlen = str.length();
            }
        }

        boolean first = true;

        for (Tree.Node<String,CommandContext> n : nodes) {
            if (first) {
                first = false;
            } else {
                sb.append("\n");
            }
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
            sb.append(n.getData().getMount().getDescription());
        }


        dispatcher.send(sb.toString(), new MessageType<String>() { }, "console://stdout");
    }
}
