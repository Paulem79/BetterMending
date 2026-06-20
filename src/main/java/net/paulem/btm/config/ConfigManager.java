package net.paulem.btm.config;

import net.paulem.btm.BetterMending;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    public ConfigManager(){
    }

    public void migrate(){
        int detectedVersion = BetterMending.instance.getConfigVersion();
        new ConfigUpdater().checkUpdate(detectedVersion);
    }
}
