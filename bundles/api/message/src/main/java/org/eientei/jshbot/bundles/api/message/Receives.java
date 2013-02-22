package org.eientei.jshbot.bundles.api.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-17
 * Time: 12:33
 *
 * method signature could be any of following:
 *
 * void name();
 * void name(T data);
 * void name(Message<T> message)
 * void name(T data, Message<T> message)
 * void name(Message<T> message, T data)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Receives {
    String[] value();
    Class typeToken();
}