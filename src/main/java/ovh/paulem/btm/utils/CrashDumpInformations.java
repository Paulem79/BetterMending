package ovh.paulem.btm.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

public class CrashDumpInformations {
    /**
     * Construct crash dump informations
     */
    public static String buildVersionString(Plugin plugin) {
        String serverType = detectServerType();
        String serverVersion = safeString(Bukkit.getVersion());
        if (isBlank(serverVersion)) {
            String sv = safeString(Bukkit.getServer().getVersion());
            serverVersion = isBlank(sv) ? "unknown" : sv;
        }

        String pluginName = "BetterMending";
        String pluginVersion = "unknown";
        if (plugin != null) {
            try {
                if (!isBlank(plugin.getDescription().getName())) {
                    pluginName = plugin.getDescription().getName();
                }
                String v = plugin.getDescription().getVersion();
                if (!isBlank(v)) {
                    pluginVersion = v;
                } else {
                    // fallback: lire plugin.yml depuis le jar
                    String byYaml = readVersionFromPluginYml(plugin);
                    if (!isBlank(byYaml)) pluginVersion = byYaml;
                }
            } catch (Throwable ignored) {
                // ne pas faire échouer la récupération d'infos
            }
        }

        return String.format("%s %s — %s v%s", serverType, serverVersion, pluginName, pluginVersion);
    }

    private static String detectServerType() {
        try {
            String name = safeString(Bukkit.getName()).toLowerCase(Locale.ROOT);
            if (!isBlank(name)) {
                return name;
            }

            String impl = safeString(Bukkit.getServer().getClass().getName()).toLowerCase(Locale.ROOT);
            if (!isBlank(impl)) {
                return impl;
            }
        } catch (Throwable ignored) {
        }
        return "UnknownServer";
    }

    private static String readVersionFromPluginYml(Plugin plugin) {
        try (InputStream is = plugin.getResource("plugin.yml")) {
            if (is == null) return null;
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(reader);
                String v = cfg.getString("version");
                return isBlank(v) ? null : v;
            }
        } catch (Throwable t) {
            return null;
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String safeString(String s) {
        return s == null ? "" : s;
    }

    public static String decodeWebhookUrl(String webhook) {
        byte[] decoded = Base64.getDecoder().decode(webhook);
        byte[] key = new byte[]{0x13, 0x37, 0x42, (byte) 0x99};
        byte[] xored = new byte[decoded.length];
        for (int i = 0; i < decoded.length; i++) {
            xored[i] = (byte) (decoded[i] ^ key[i % key.length]);
        }
        // reverse
        for (int i = 0, j = xored.length - 1; i < j; i++, j--) {
            byte tmp = xored[i];
            xored[i] = xored[j];
            xored[j] = tmp;
        }
        return new String(xored, StandardCharsets.UTF_8);
    }
}
