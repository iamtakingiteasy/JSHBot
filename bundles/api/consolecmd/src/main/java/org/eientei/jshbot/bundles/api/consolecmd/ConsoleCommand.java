package org.eientei.jshbot.bundles.api.consolecmd;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-20
 * Time: 21:15
 */
public interface ConsoleCommand {
    List<ConsoleCommandMountPoint> mountPoints();
    void execute(List<String> arg0, List<String> argv);
}
