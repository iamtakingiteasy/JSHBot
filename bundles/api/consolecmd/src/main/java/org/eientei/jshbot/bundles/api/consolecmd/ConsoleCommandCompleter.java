package org.eientei.jshbot.bundles.api.consolecmd;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-20
 * Time: 21:37
 */
public interface ConsoleCommandCompleter {
    int complete(List<String> arg0, String argvFlat, int argvPos, List<CharSequence> candidates);
}
