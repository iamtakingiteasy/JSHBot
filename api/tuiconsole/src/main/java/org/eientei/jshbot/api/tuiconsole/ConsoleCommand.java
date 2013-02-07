package org.eientei.jshbot.api.tuiconsole;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 20:18
 */
public interface ConsoleCommand {
    void setup(ConsoleCommandContext context);
    void execute(List<String> cmd, List<String> arguments);
}
