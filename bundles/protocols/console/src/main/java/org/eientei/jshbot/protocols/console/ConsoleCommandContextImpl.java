package org.eientei.jshbot.protocols.console;

import org.eientei.jshbot.protocols.console.api.ConsoleCommand;
import org.eientei.jshbot.protocols.console.api.ConsoleCommandContext;
import org.eientei.jshbot.protocols.console.api.MountPoint;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-04
 * Time: 20:27
 */
public class ConsoleCommandContextImpl implements ConsoleCommandContext {
    private ConcurrentMap<List<String>,MountPoint> mountPoints = new ConcurrentHashMap<List<String>, MountPoint>();
    private ConsoleCommand command;
    private Tree<String, ConsoleCommandContext> commandTree;

    public ConsoleCommandContextImpl(ConsoleCommand command, Tree<String, ConsoleCommandContext> commandTree) {
        this.command = command;
        this.commandTree = commandTree;
    }

    public ConsoleCommand getCommand() {
        return command;
    }

    @Override
    public void addMountPoint(MountPoint point) {
        mountPoints.put(point.getMountPoint(),point);
        commandTree.insert(this,point.getMountPoint());
    }

    @Override
    public MountPoint getMountPoint(List<String> path) {
        return mountPoints.get(path);
    }

    @Override
    public Collection<MountPoint> getAllMountPoints() {
        return mountPoints.values();
    }


}
