package org.eientei.jshbot.bundles.api.consolecmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-20
 * Time: 21:31
 */
public class ConsoleCommandMountPoint {
    private final List<String> path = new ArrayList<String>();
    private final String description;
    private final ConsoleCommandCompleter completer;

    public ConsoleCommandMountPoint(String description, String... path) {
        Collections.addAll(this.path,path);
        this.description = description;
        this.completer = new ConsoleCommandCompleter() {
            @Override
            public int complete(List<String> arg0, String argvFlat, int argvPos, List<CharSequence> candidates) {
                return -1;
            }
        };
    }

    public ConsoleCommandMountPoint(String description, ConsoleCommandCompleter completer, String... path) {
        Collections.addAll(this.path,path);
        this.description = description;
        this.completer = completer;
    }

    public List<String> getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public ConsoleCommandCompleter getCompleter() {
        return completer;
    }
}
