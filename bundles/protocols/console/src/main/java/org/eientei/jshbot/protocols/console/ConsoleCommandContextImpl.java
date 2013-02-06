package org.eientei.jshbot.protocols.console;

import jline.console.completer.Completer;
import org.eientei.jshbot.protocols.console.api.ConsoleCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 20:27
 */
public class ConsoleCommandContextImpl {
    private List<Completer> completers = new ArrayList<Completer>();
    private String commandDesc;
    private List<List<String>> commandMountPoints;
    private ConsoleCommand command;

    public ConsoleCommandContextImpl(ConsoleCommand command) {
        if (command.getCompleters() != null) {
            for (Completer c : command.getCompleters()) {
                completers.add(c);
            }
        }
        commandDesc = command.getDesc();

        commandMountPoints = new ArrayList<List<String>>();
        for (String[] ss : command.getMountPoints()) {
            commandMountPoints.add(Arrays.asList(ss));
        }

        this.command = command;
    }

    public List<Completer> getCompleters() {
        return completers;
    }


    public String getCommandDesc() {
        return commandDesc;
    }

    public List<List<String>> getCommandMountPoints() {
        return commandMountPoints;
    }

    public ConsoleCommand getCommand() {
        return command;
    }
}
