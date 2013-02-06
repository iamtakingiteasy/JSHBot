package org.eientei.jshbot.protocols.console;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-06
 * Time: 10:18
 */
public class ShellUtils {
    public static List<String> shellSplit(String line) {
        List<String> result = new LinkedList<String>();
        char[] chars = line.trim().toCharArray();
        int i = 0;
        boolean doubleQ = false;
        boolean singleQ = false;

        while (i < chars.length) {
            StringBuilder sb = new StringBuilder();

            if (chars[i] == '"') {
                i++;
                char prevChar = 0;
                while (i < chars.length && (chars[i] != '"' || prevChar == '\\')) {
                    if (prevChar == '\\' && chars[i] == '"') {
                        sb.deleteCharAt(sb.length()-1);
                    }
                    sb.append(chars[i]);
                    prevChar = chars[i];
                    i++;
                }
                i++;
            } else if (chars[i] == '\'') {
                i++;
                char prevChar = 0;
                while (i < chars.length && (chars[i] != '\'' || prevChar == '\\')) {
                    if (prevChar == '\\' && chars[i] == '\'') {
                        sb.deleteCharAt(sb.length()-1);
                    }
                    sb.append(chars[i]);
                    prevChar = chars[i];
                    i++;
                }
                i++;
            } else {
                while (i < chars.length && !Character.isSpaceChar(chars[i])) {
                    sb.append(chars[i]);
                    i++;
                }
            }
            while (i < chars.length && Character.isSpaceChar(chars[i])) {
                i++;
            }

            result.add(sb.toString());
        }

        return result;
    }

    public static String concat(List<String> ss) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : ss) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
