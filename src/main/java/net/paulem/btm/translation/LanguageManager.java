package net.paulem.btm.translation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.paulem.btm.BetterMending;
import net.paulem.btm.adventure.MiniMessageToLegacy;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LanguageManager {
    private static final Locale fallbackLocale = Locale.US;

    private final BetterMending plugin;
    private final Map<Locale, Properties> locales = new HashMap<>();

    private final NamespacedKey pdcLangKey;

    public LanguageManager(BetterMending plugin) {
        this.plugin = plugin;
        this.pdcLangKey = new NamespacedKey(plugin, "player_language");
    }

    /**
     * Initializes, copies, and updates language files.
     */
    public void init() {
        locales.clear();
        File langFolder = new File(plugin.getDataFolder(), "langs");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        // Use a LinkedHashMap to preserve order and map Locales to their specific file on disk if found
        Map<Locale, File> localesToLoad = new LinkedHashMap<>();

        // Scan internal locales from the plugin's JAR file
        try {
            URL jarUrl = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
            try (ZipInputStream zip = new ZipInputStream(jarUrl.openStream())) {
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    String name = entry.getName();
                    // Look for files inside the 'langs/' folder inside the jar
                    if (name.startsWith("langs/") && name.endsWith(".properties")) {
                        String localeStr = name.substring(6, name.length() - 11); // "langs/".length() = 6, ".properties".length() = 11
                        if (!localeStr.isEmpty()) {
                            Locale locale = parseLocale(localeStr);
                            if (!locale.getLanguage().isEmpty()) {
                                localesToLoad.put(locale, null);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to scan internal language files from JAR: " + e.getMessage());
        }

        // Discover and add external locales from the langs folder
        File[] files = langFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".properties"));
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String localeStr = fileName.substring(0, fileName.length() - 11); // Remove ".properties"
                Locale locale = parseLocale(localeStr);

                // Skip files that do not resolve to a valid language format
                if (locale.getLanguage().isEmpty()) {
                    continue;
                }

                // If it already exists (internal), it associates the external file with it.
                // If it's a new custom locale added by the user, it registers it.
                localesToLoad.put(locale, file);
            }
        }

        // Load and merge all gathered locales
        for (Map.Entry<Locale, File> entry : localesToLoad.entrySet()) {
            loadAndMergeLocale(langFolder, entry.getKey(), entry.getValue());
        }

        plugin.getLogger().info("I18n initialized with " + locales.size() + " languages.");
    }

    /**
     * Loads the external file and adds missing keys from the internal file (Versioning).
     */
    private void loadAndMergeLocale(File langFolder, Locale locale, File extFile) {
        String localeName = locale.toString(); // e.g., "en_US" or "fr_FR"

        // If no explicit file was provided by the folder scanner, fall back to the default standard name
        if (extFile == null) {
            extFile = new File(langFolder, localeName + ".properties");
        }

        Properties properties = new Properties();

        // Load the external file if it exists
        if (extFile.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(extFile), StandardCharsets.UTF_8)) {
                properties.load(reader);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to read " + extFile.getName());
            }
        }

        // Load the internal file from the .jar (resources/langs/...)
        InputStream internalStream = plugin.getResource("langs/" + localeName + ".properties");
        if (internalStream != null) {
            Properties internalProps = new Properties();
            try (InputStreamReader reader = new InputStreamReader(internalStream, StandardCharsets.UTF_8)) {
                internalProps.load(reader);

                boolean modified = false;
                // Check for missing keys (Versioning system)
                for (String key : internalProps.stringPropertyNames()) {
                    if (!properties.containsKey(key)) {
                        properties.setProperty(key, internalProps.getProperty(key));
                        modified = true;
                    }
                }

                // Save if the file did not exist or if keys were added
                if (modified || !extFile.exists()) {
                    try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(extFile), StandardCharsets.UTF_8)) {
                        properties.store(writer, "Translation file: " + localeName);
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Error while updating " + localeName);
            }
        }

        locales.put(locale, properties);
    }

    /**
     * Allows the player to force their language via command (saved in the PDC).
     */
    public void setPlayerLanguage(Player player, Locale locale) {
        player.getPersistentDataContainer().set(pdcLangKey, PersistentDataType.STRING, locale.toString());
    }

    public void removePlayerLanguage(Player player) {
        player.getPersistentDataContainer().remove(pdcLangKey);
    }

    /**
     * Determines the final language to use for a player based on your criteria.
     */
    public Locale resolveLocale(Player player) {
        // Priority to the player's manual choice via PDC
        if (player.getPersistentDataContainer().has(pdcLangKey, PersistentDataType.STRING)) {
            String pdcLangStr = player.getPersistentDataContainer().get(pdcLangKey, PersistentDataType.STRING);
            Locale pdcLocale = parseLocale(pdcLangStr);
            if (locales.containsKey(pdcLocale)) return pdcLocale;
        }

        // Retrieve from config
        boolean universalDefault = plugin.getConfig().getBoolean("language.force-universal-default", false);
        String configDefaultStr = plugin.getConfig().getString("language.default", "en_US");
        Locale configDefault = parseLocale(configDefaultStr);

        // If the server enforces a universal language
        if (universalDefault) {
            return locales.containsKey(configDefault) ? configDefault : fallbackLocale;
        }

        // The player's Minecraft client language (e.g., "fr_fr")
        Locale clientLocale = parseLocale(player.getLocale());
        if (locales.containsKey(clientLocale)) {
            return clientLocale;
        }

        // Fallback to the default language from the config
        if (locales.containsKey(configDefault)) {
            return configDefault;
        }

        // Final fallback
        return fallbackLocale;
    }

    /**
     * Retrieves the raw string from the file.
     */
    private String getRawMessage(Locale locale, String key) {
        Properties props = locales.getOrDefault(locale, locales.get(fallbackLocale));

        if (props != null && props.containsKey(key)) {
            return props.getProperty(key);
        }

        if (locale == fallbackLocale) {
            return "<red>Missing translation key: " + key;
        }

        return getRawMessage(fallbackLocale, key);
    }

    /**
     * Sends a formatted message using MiniMessage to a CommandSender.
     * Uses TagResolver to insert variables.
     */
    public void sendMessage(CommandSender sender, String key, TagResolver... placeholders) {
        Locale locale;

        if (sender instanceof Player player) {
            locale = resolveLocale(player);
        } else {
            // For the console, use the default from the config
            String configDefaultStr = plugin.getConfig().getString("language.default", "en_US");
            locale = parseLocale(configDefaultStr);
        }

        String rawString = getRawMessage(locale, key);

        // MiniMessage parses the string into a Component
        Component component = MiniMessage.miniMessage().deserialize(rawString, placeholders);
        String result = MiniMessage.miniMessage().serialize(component);

        if (sender instanceof Player player) {
            player.sendMessage(MiniMessageToLegacy.toNativeSection(result));
        } else {
            sender.sendMessage(MiniMessageToLegacy.toNativeSection(result));
        }
    }

    /**
     * Helper method to parse a string representation of a locale safely.
     * Handles formats like "en_US", "en_us", and "en-US".
     */
    public Locale parseLocale(String localeStr) {
        if (localeStr == null || localeStr.isEmpty()) {
            return fallbackLocale;
        }

        // Locale.forLanguageTag expects hyphens instead of underscores
        return Locale.forLanguageTag(localeStr.replace('_', '-'));
    }

    public Set<Locale> getLocales() {
        return locales.keySet();
    }
}