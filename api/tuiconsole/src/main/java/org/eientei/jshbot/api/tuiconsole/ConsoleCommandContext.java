package org.eientei.jshbot.api.tuiconsole;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 19:24
 */
public interface ConsoleCommandContext {
    void addMountPoint(MountPoint point);
    MountPoint getMountPoint(List<String> path);
    Collection<MountPoint> getAllMountPoints();
    ConsoleCommand getCommand();
}
