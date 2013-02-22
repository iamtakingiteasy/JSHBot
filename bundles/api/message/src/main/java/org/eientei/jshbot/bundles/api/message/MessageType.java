package org.eientei.jshbot.bundles.api.message;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-18
 * Time: 21:20
 */
public abstract class MessageType<T> {
    private final Type type;

    public MessageType() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new RuntimeException("Missing type parameter");
        }
        Type candidate = ((ParameterizedType)superClass).getActualTypeArguments()[0];
        if (candidate instanceof ParameterizedType) {
            type = candidate;
        } else {
            type = superClass;
        }
    }


    public final boolean isAssignableTo(Class clazz) {
        Type otherType = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

        if (otherType instanceof Class) {
            return ((Class) otherType).isAssignableFrom((Class) ((ParameterizedType) type).getActualTypeArguments()[0]);
        }



        if (((Class)((ParameterizedType) otherType).getRawType()).isAssignableFrom((Class)((ParameterizedType) type).getRawType())) {
            if (type instanceof ParameterizedType) {
                Type[] fromArgs = ((ParameterizedType)type).getActualTypeArguments();
                Type[] toArgs = ((ParameterizedType)otherType).getActualTypeArguments();

                for (int i = 0; i < fromArgs.length; i++) {
                    if (!fromArgs[i].equals(toArgs[i])) {
                        return false;
                    }
                }
            }
            return true;
        }

        return false;
    }


}


