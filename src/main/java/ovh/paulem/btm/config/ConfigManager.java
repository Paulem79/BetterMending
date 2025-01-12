package ovh.paulem.btm.config;

import ovh.paulem.btm.BetterMending;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class ConfigManager {
    private final BetterMending plugin;

    public ConfigManager(BetterMending plugin){
        this.plugin = plugin;
    }

    public void migrate(){
        FileConfiguration config = plugin.getConfig();
        FileConfiguration embeddedConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));

        int detectedVersion = config.getInt("version", 0);
        int embeddedDetectedVersion = embeddedConfig.getInt("version", 0);

        plugin.getLogger().info("Detected version: " + (detectedVersion != 0 ? detectedVersion : "none"));
        plugin.getLogger().info("Embedded version: " + (embeddedDetectedVersion != 0 ? embeddedDetectedVersion : "none"));

        if(embeddedDetectedVersion == detectedVersion) return;

        plugin.getLogger().warning("Your configuration is outdated! Upgrading...");

        config.options().copyDefaults(true);
        config.setDefaults(embeddedConfig);
        config.set("version", embeddedDetectedVersion);
        plugin.saveConfig();

        plugin.getLogger().info("Your configuration has been updated! You can find more informations about new option on the plugin resource page!");
    }
}
