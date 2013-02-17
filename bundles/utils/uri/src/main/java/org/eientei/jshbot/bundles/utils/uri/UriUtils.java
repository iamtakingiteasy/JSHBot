package org.eientei.jshbot.bundles.utils.uri;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-10
 * Time: 17:41
 */
public class UriUtils {
    public static boolean match(URI src, URI dst) {
        if (src == null || dst == null) return false;

        return  src.getPort() == dst.getPort()
                && uriComponentMatch(src.getScheme(), dst.getScheme())
                && uriComponentMatch(src.getAuthority(), dst.getAuthority())
                && uriPathComponentMatch(src.getPath(), dst.getPath());
    }

    private static boolean uriPathComponentMatch(String src, String dst) {
        if (dst == null) {
            return false;
        } else {
            String[] splitDst = dst.split("/+");
            String[] splitSrc;
            if (src == null) {
                splitSrc = new String[0];
            } else {
                splitSrc = src.split("/+");
            }
            for (int i = 1; i < splitDst.length; i++) {
                String d = splitDst[i];
                if (d.equals("*")) return true;
                if (i >= splitSrc.length) return false;
                if (!d.equals(splitSrc[i])) return false;
            }
            return true;
        }
    }

    private static boolean uriComponentMatch(String src, String dst) {
        if (dst == null) {
            return false;
        } else {
            return dst.equals("*") || dst.equals(src);
        }
    }
}
