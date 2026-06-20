package net.paulem.btm.versioned;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Versioning {
    private Versioning() {
        /* This utility class should not be instantiated */
    }

    // cache lazily the parsed parts
    private static String[] mcParts;

    private static synchronized String[] getMcParts() {
        if (mcParts != null) return mcParts;

        String version = Bukkit.getVersion();

        int start = version.indexOf("MC: ");
        int end = -1;
        if (start >= 0) {
            end = version.indexOf(')', start);
        }
        if (start < 0) {
            // fallback to whole string
            start = 0;
            end = version.length();
        } else if (end < 0) {
            end = version.length();
        }

        String verToken = version.substring(start + 4, end).trim();

        // strip suffixes like "-snapshot-1" or any non-digit/dot trailing content
        Pattern p = Pattern.compile("^(\\d+(?:\\.\\d+)*).*$");
        Matcher m = p.matcher(verToken);
        if (m.matches()) {
            verToken = m.group(1);
        }

        String[] parts = verToken.split("\\.");

        // Ensure we have at least 3 parts for easier indexing. If missing, pad with "0".
        if (parts.length == 1) {
            parts = new String[]{parts[0], "0", "0"};
        } else if (parts.length == 2) {
            parts = new String[]{parts[0], parts[1], "0"};
        }

        mcParts = parts;
        return mcParts;
    }

    public static boolean isPost17() {
        return isPost(17);
    }

    public static boolean isPost9() {
        return isPost(9);
    }

    public static boolean isPost(int v) {
        String[] mcParts = getMcParts();
        // If major is 1 (classic scheme), compare minor/patch as before.
        int major = safeParse(mcParts[0]);
        int minor = safeParse(mcParts[1]);
        int patch = safeParse(mcParts[2]);

        if (major == 1) {
            return minor > v || (minor == v && patch >= 1);
        } else {
            // Year-based scheme: treat `v` as the major/year number.
            return major > v || (major == v && minor >= 1);
        }
    }

    public static boolean isPost(int v, int r) {
        String[] mcParts = getMcParts();
        int major = safeParse(mcParts[0]);
        int minor = safeParse(mcParts[1]);
        int patch = safeParse(mcParts[2]);

        if (major == 1) {
            return minor > v || (minor == v && patch > r);
        } else {
            // Year-based: compare major then minor
            return major > v || (major == v && minor > r);
        }
    }

    public static boolean hasPDC() {
        try {
            Class.forName("org.bukkit.persistence.PersistentDataContainer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static int safeParse(String s) {
        if (s == null) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            // try to strip non-digits
            String digits = s.replaceAll("\\D", "");
            if (digits.isEmpty()) return 0;
            try {
                return Integer.parseInt(digits);
            } catch (NumberFormatException ex) {
                return 0;
            }
        }
    }
}
