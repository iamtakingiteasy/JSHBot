package org.eientei.jshbot.api.tuiconsole;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 22:01
 */
public interface ConsoleCommandCompleter{
    public int complete(String buffer, int cursor, List<CharSequence> candidates);
}
