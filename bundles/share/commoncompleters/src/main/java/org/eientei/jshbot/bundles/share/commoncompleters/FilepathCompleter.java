package org.eientei.jshbot.bundles.share.commoncompleters;

import org.eientei.jshbot.bundles.api.consolecmd.ConsoleCommandCompleter;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-22
 * Time: 22:19
 */
public class FilepathCompleter implements ConsoleCommandCompleter {
    private static final boolean OS_IS_WINDOWS = false;

    protected String separator() {
        return File.separator;
    }

    protected File getUserHome() {
        return new File(System.getProperty("user.home"));
    }

    protected File getUserDir() {
        return new File(".");
    }

    protected int matchFiles(final String buffer, final String translated, final File[] files, final List<CharSequence> candidates) {
        if (files == null) {
            return -1;
        }

        int matches = 0;

        // first pass: just count the matches
        for (File file : files) {
            if (file.getAbsolutePath().startsWith(translated)) {
                matches++;
            }
        }
        for (File file : files) {
            if (file.getAbsolutePath().startsWith(translated)) {
                CharSequence name = file.getName() + (matches == 1 && file.isDirectory() ? separator() : " ");
                candidates.add(render(file, name).toString());
            }
        }

        final int index = buffer.lastIndexOf(separator());

        return index + separator().length();
    }

    protected CharSequence render(final File file, final CharSequence name) {
        return name;
    }

    @Override
    public int complete(List<String> arg0, String argvFlat, int argvPos, List<CharSequence> candidates) {
        if (argvFlat == null) {
            argvFlat = "";
        }

        if (OS_IS_WINDOWS) {
            argvFlat = argvFlat.replace('/', '\\');
        }

        String translated = argvFlat;

        File homeDir = getUserHome();

        // Special character: ~ maps to the user's home directory
        if (translated.startsWith("~" + separator())) {
            translated = homeDir.getPath() + translated.substring(1);
        }
        else if (translated.startsWith("~")) {
            translated = homeDir.getParentFile().getAbsolutePath();
        }
        else if (!(translated.startsWith(separator()))) {
            String cwd = getUserDir().getAbsolutePath();
            translated = cwd + separator() + translated;
        }

        File file = new File(translated);
        final File dir;

        if (translated.endsWith(separator())) {
            dir = file;
        }
        else {
            dir = file.getParentFile();
        }

        File[] entries = dir == null ? new File[0] : dir.listFiles();

        return matchFiles(argvFlat, translated, entries, candidates);
    }
}
