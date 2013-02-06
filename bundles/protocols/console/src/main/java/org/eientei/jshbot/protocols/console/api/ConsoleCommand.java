package org.eientei.jshbot.protocols.console.api;

import jline.console.completer.Completer;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 20:18
 */
public interface ConsoleCommand {
    String[][] getMountPoints();
    String getDesc();
    Collection<Completer> getCompleters();
    void execute(List<String> arguments);
}
