package org.eientei.jshbot.bundles.api.consolecommand;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-11
 * Time: 14:40
 */
public interface ConsoleCommand {
    void execute(List<String> cmdPath, List<String> arguments);
}
