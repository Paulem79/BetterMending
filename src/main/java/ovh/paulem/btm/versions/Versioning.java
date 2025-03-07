package ovh.paulem.btm.versions;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Versioning {
    public static boolean isPost17() {
        return isPost(17);
    }

    public static boolean isPost9() {
        return isPost(9);
    }

    public static boolean isLegacy() {
        return !isPost(12, 2);
    }

    public static boolean isIn13() {
        return isPost(12, 2) && !isPost(13, 2);
    }

    public static boolean isPost(int v) {
        String[] mcParts = getMcParts();
        return Integer.parseInt(mcParts[1]) > v || (Integer.parseInt(mcParts[1]) == v && Integer.parseInt(mcParts[2]) >= 1);
    }

    public static boolean isPost(int v, int r) {
        String[] mcParts = getMcParts();
        return Integer.parseInt(mcParts[1]) > v || (Integer.parseInt(mcParts[1]) == v && Integer.parseInt(mcParts[2]) > r);
    }

    private static String[] getMcParts() {
        String version = Bukkit.getVersion();
        String[] mcParts = version.substring(version.indexOf("MC: ") + 4, version.length() - 1).split("\\.");

        if (mcParts.length < 3) {
            Set<String> collect = Arrays.stream(mcParts).collect(Collectors.toSet());
            collect.add("0");
            mcParts = collect.toArray(new String[0]);
        }

        return mcParts;
    }

    public static boolean hasPDC() {
        try {
            Class.forName("org.bukkit.persistence.PersistentDataContainer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
