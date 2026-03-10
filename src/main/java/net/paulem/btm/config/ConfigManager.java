package net.paulem.btm.config;

import net.paulem.btm.BetterMending;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    public ConfigManager(){
    }

    public void migrate(){
        FileConfiguration config = BetterMending.getInstance().getConfig();

        int detectedVersion = config.getInt("version", 0);
        new ConfigUpdater().checkUpdate(detectedVersion);
    }
}
