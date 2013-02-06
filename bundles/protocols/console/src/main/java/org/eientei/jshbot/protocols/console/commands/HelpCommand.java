package org.eientei.jshbot.protocols.console.commands;

import jline.console.completer.Completer;
import org.eientei.jshbot.api.dispatcher.Dispatcher;
import org.eientei.jshbot.bundles.utils.GenericSingularServiceListener;
import org.eientei.jshbot.protocols.console.ConsoleCommandContextImpl;
import org.eientei.jshbot.protocols.console.Tree;
import org.eientei.jshbot.protocols.console.api.ConsoleCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 16:24
 */
public class HelpCommand implements ConsoleCommand {
    private GenericSingularServiceListener<Dispatcher> dispatcherService;
    private Tree<String,ConsoleCommandContextImpl> commands;

    public HelpCommand(GenericSingularServiceListener<Dispatcher> dispatcherService, Tree<String, ConsoleCommandContextImpl> commands) {
        this.dispatcherService = dispatcherService;
        this.commands = commands;
    }

    @Override
    public String[][] getMountPoints() {
        return new String[][] {
                new String[] { "help" }
        };
    }

    @Override
    public String getDesc() {
        return "Prints this help message";
    }

    @Override
    public Collection<Completer> getCompleters() {
        return new ArrayList<Completer>();
    }

    @Override
    public void execute(List<String> arguments) {

    }
}
