package org.eientei.jshbot.bundles.api.consolecommand;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-11
 * Time: 14:43
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MountPoint {
    String mount();
    String description();
}
