package org.eientei.jshbot.protocols.console;

import jline.console.completer.Completer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-05
 * Time: 12:31
 */
public class RootCompleter implements Completer {
    private Tree<String,ConsoleCommandContextImpl> commands;

    public RootCompleter(Tree<String, ConsoleCommandContextImpl> commands) {
        this.commands = commands;
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        List<String> keys;
        List<String> arguments = new ArrayList<String>();
        if (buffer == null) {
            keys = new ArrayList<String>();
        } else {
            keys = ShellUtils.shellSplit(buffer);
        }
        boolean traversing = true;
        String currentCommand = "";
        int retpos = 0;
        Tree.Node<String, ConsoleCommandContextImpl> n = commands.getRoot();

        for (String key : keys) {
            if (traversing) {
                Tree.Node<String,ConsoleCommandContextImpl> c = n.getChild(key);
                if (c == null) {
                    traversing = false;
                    currentCommand = key;
                } else {
                    retpos += key.length() + 1;
                    n = c;
                }
            } else {
                arguments.add(key);
            }
        }

        if (arguments.isEmpty()) {
            for (String match : n.tailMap(currentCommand).keySet()) {
                if (!match.startsWith(currentCommand)) {
                    break;
                }
                candidates.add(match + " ");
            }
        }

        String completionBuffer = (currentCommand + " " + ShellUtils.concat(arguments)).trim();
        int completionCursor = completionBuffer.length() - 1;

        if (n.getData() != null) {
            for (Completer completer : n.getData().getCompleters()) {
                completer.complete(completionBuffer, completionCursor, candidates);
            }
        }

        return candidates.isEmpty() ? -1 : retpos;
    }
}
