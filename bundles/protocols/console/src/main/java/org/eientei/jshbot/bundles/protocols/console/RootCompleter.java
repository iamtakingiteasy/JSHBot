package org.eientei.jshbot.bundles.protocols.console;

import jline.console.completer.Completer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-11
 * Time: 14:35
 */
public class RootCompleter implements Completer {

    public RootCompleter(Tree<String, ConsoleCommandContext> commandTree) {
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
