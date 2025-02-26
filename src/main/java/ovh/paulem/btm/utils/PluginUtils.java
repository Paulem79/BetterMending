package ovh.paulem.btm.utils;

import org.bukkit.configuration.file.FileConfiguration;
import ovh.paulem.btm.BetterMending;
import ovh.paulem.btm.listeners.extendables.ManagersListener;

public class PluginUtils {
    public static void reloadConfig(BetterMending plugin) {
        plugin.reloadConfig();

        FileConfiguration reloadedConfig = plugin.getConfig();
        if(plugin.playerConfigHandler != null) plugin.playerConfigHandler.reload();
        if(plugin.mainRepairManager != null) plugin.mainRepairManager.setConfig(reloadedConfig);

        ManagersListener.reloadConfig(reloadedConfig);
    }
}
