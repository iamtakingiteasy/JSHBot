package org.eientei.jshbot.bundles.share.basiccommands;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.eientei.jshbot.bundles.api.consolecmd.ConsoleCommand;
import org.eientei.jshbot.bundles.api.consolecmd.ConsoleCommandMountPoint;
import org.eientei.jshbot.bundles.api.message.Dispatcher;
import org.eientei.jshbot.bundles.api.message.MessageType;
import org.eientei.jshbot.bundles.utils.shell.ShellUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-22
 * Time: 18:43
 */

@Provides
@Instantiate
@Component
public class EchoCommand implements ConsoleCommand {
    @Requires
    private Dispatcher dispatcher;

    @Override
    public List<ConsoleCommandMountPoint> mountPoints() {
        List<ConsoleCommandMountPoint> mps = new ArrayList<ConsoleCommandMountPoint>();
        mps.add(new ConsoleCommandMountPoint("Echoes it's arguments", "echo"));
        return mps;
    }

    @Override
    public void execute(List<String> arg0, List<String> argv) {
        dispatcher.send(ShellUtils.concat(argv), new MessageType<String>() { }, "console://stdout");
    }
}
