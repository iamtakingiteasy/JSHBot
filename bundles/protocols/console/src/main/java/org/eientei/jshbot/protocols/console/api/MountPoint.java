package org.eientei.jshbot.protocols.console.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 18:54
 */
public class MountPoint {
    private final boolean autoThreading;
    private final List<String> mountPoint;
    private final String description;
    private final List<ConsoleCommandCompleter> completers = new ArrayList<ConsoleCommandCompleter>();

    public MountPoint(String description, String ... mountPoint) {
        this.mountPoint = Arrays.asList(mountPoint);
        this.description = description;
        this.autoThreading = false;
    }

    public MountPoint(String description, List<ConsoleCommandCompleter> completers, String ... mountPoint) {
        this.description = description;
        this.mountPoint = Arrays.asList(mountPoint);
        if (completers != null) {
            this.completers.addAll(completers);
        }
        this.autoThreading = false;
    }

    public MountPoint(String description, List<ConsoleCommandCompleter> completers, boolean autoThreading, String ... mountPoint) {
        this.description = description;
        this.mountPoint = Arrays.asList(mountPoint);
        if (completers != null) {
            this.completers.addAll(completers);
        }
        this.autoThreading = autoThreading;
    }

    public List<String> getMountPoint() {
        return mountPoint;
    }

    public String getDescription() {
        return description;
    }

    public List<ConsoleCommandCompleter> getCompleters() {
        return completers;
    }

    public boolean isAutoThreading() {
        return autoThreading;
    }
}
