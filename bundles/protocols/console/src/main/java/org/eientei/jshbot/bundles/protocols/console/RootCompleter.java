package org.eientei.jshbot.bundles.protocols.console;

import jline.console.completer.Completer;
import org.eientei.jshbot.bundles.api.consolecmd.CommandContext;
import org.eientei.jshbot.bundles.utils.shell.ShellUtils;
import org.eientei.jshbot.bundles.utils.tree.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-22
 * Time: 19:15
 */
public class RootCompleter implements Completer {
    private Tree<String,CommandContext> commands;

    public RootCompleter(Tree<String, CommandContext> commands) {
        this.commands = commands;
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        List<String> toks = ShellUtils.shellSplit(buffer);
        final List<String> arg0 = new ArrayList<String>();
        final List<String> argv = new ArrayList<String>();
        Tree.Node<String,CommandContext> n = commands.getRoot();
        boolean traversing = true;
        int retpos = 0;

        for (String a : toks) {
            if (traversing) {
                Tree.Node<String,CommandContext> c = n.getChild(a);
                if (c == null) {
                    traversing = false;
                    argv.add(a);
                } else {
                    n = c;
                    arg0.add(a);
                    retpos += a.length() + 1;
                }
            } else {
                argv.add(a);
            }
        }

        if (argv.size() <= 1) {
            String beg = "";
            if (!argv.isEmpty()) {
                beg = argv.get(0);
            }
            for (String match : n.tailMap(beg).keySet()) {
                if (!match.startsWith(beg)) {
                    break;
                }
                candidates.add(match + " ");
            }
        }

        int ret = -1;

        if (n.getData() != null) {
            int pos = 0;
            ret = n.getData().getMount().getCompleter().complete(arg0,ShellUtils.concat(argv), pos, candidates);
        }

        if (ret > -1) retpos += ret;
        return candidates.isEmpty() ? -1 : retpos;
    }
}
