package net.paulem.btm.config;

import net.paulem.btm.BetterMending;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final BetterMending plugin;

    public ConfigManager(BetterMending plugin){
        this.plugin = plugin;
    }

    public void migrate(){
        FileConfiguration config = plugin.getConfig();

        int detectedVersion = config.getInt("version", 0);
        new ConfigUpdater(plugin).checkUpdate(detectedVersion);
    }
}
