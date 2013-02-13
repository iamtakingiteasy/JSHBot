package org.eientei.jshbot.bundles.protocols.console;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-12
 * Time: 00:33
 */
public class MontPointContext {
    private List<String> monutPoint;
    private String description;

    public MontPointContext(List<String> monutPoint, String description) {
        this.monutPoint = monutPoint;
        this.description = description;
    }

    public List<String> getMonutPoint() {
        return monutPoint;
    }

    public String getDescription() {
        return description;
    }
}
