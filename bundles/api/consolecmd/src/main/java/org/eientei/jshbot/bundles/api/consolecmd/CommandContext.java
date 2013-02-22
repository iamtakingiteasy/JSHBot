package org.eientei.jshbot.bundles.api.consolecmd;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-20
 * Time: 15:30
 */
public class CommandContext {
    private ConsoleCommand command;
    private ConsoleCommandMountPoint mount;

    public CommandContext(ConsoleCommand command, ConsoleCommandMountPoint mount) {
        this.command = command;
        this.mount = mount;
    }

    public ConsoleCommand getCommand() {
        return command;
    }

    public ConsoleCommandMountPoint getMount() {
        return mount;
    }
}
