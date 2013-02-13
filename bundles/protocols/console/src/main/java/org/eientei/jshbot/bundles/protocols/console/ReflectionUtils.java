package org.eientei.jshbot.bundles.protocols.console;

import org.eientei.jshbot.bundles.api.consolecommand.Completor;
import org.eientei.jshbot.bundles.api.consolecommand.Mount;
import org.eientei.jshbot.bundles.api.consolecommand.MountPoint;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-12
 * Time: 00:31
 */
public class ReflectionUtils {
    public static List<MontPointContext> getMounts(Class clazz) {
        List<MontPointContext> result = new ArrayList<MontPointContext>();

        if (!clazz.isAnnotationPresent(Mount.class)) return result;

        Mount mount = (Mount) clazz.getAnnotation(Mount.class);

        if (mount.value() == null) return result;

        for (MountPoint mp : mount.value()) {
            String[] rawPath = mp.mount().split(" +");
            List<String> listPath = new ArrayList<String>();
            for (String path : rawPath) {
                listPath.add(path);
            }
            result.add(new MontPointContext(listPath, mp.description()));
        }

        return result;
    }

    public static List<Method> getCompletors(Class clazz) {
        List<Method> result = new ArrayList<Method>();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (!method.isAnnotationPresent(Completor.class)) continue;
            Class[] types = method.getParameterTypes();
            if (types.length != 3) continue;
            if (!types[0].getName().equals(String.class.getName())) continue;
            if (!types[1].getName().equals(int.class.getName()))    continue;
            if (!types[2].getName().equals(List.class.getName()))   continue;
            result.add(method);
        }
        return result;
    }
}
