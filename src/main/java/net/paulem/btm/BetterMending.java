package net.paulem.btm;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import lombok.Getter;
import org.bukkit.Bukkit;
import net.paulem.btm.compat.BTMPlaceholderCompat;
import net.paulem.btm.commands.CommandBTM;
import net.paulem.btm.compat.OraxenCompat;
import net.paulem.btm.compat.OraxenDefaultCompat;
import net.paulem.btm.config.ConfigBlacklist;
import net.paulem.btm.libs.bstats.Metrics;
import net.paulem.btm.listeners.MendingUseListener;
import net.paulem.btm.listeners.PreventDestroyListener;
import net.paulem.btm.config.ConfigManager;
import net.paulem.btm.versioned.damage.DamageHandler;
import net.paulem.btm.versioned.damage.DamageLegacy;
import net.paulem.btm.managers.RepairManager;
import net.paulem.btm.versioned.damage.DamageNewer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import net.paulem.btm.utils.PluginUtils;
import net.paulem.btm.versioned.Versioning;
import net.paulem.btm.versioned.playerconfig.PlayerConfigHandler;
import net.paulem.btm.versioned.playerconfig.PlayerConfigLegacy;

public class BetterMending extends JavaPlugin {
    @Getter
    private static BetterMending instance;

    private PlayerConfigHandler playerConfig;
    private RepairManager repairManager;
    private DamageHandler damageHandler;
    private ConfigBlacklist configBlacklist;

    @Getter
    private OraxenDefaultCompat oraxenCompat;

    @Override
    public void onEnable() {
        instance = this;

        if(!Versioning.isPost9()) {
            getLogger().severe("You need to use a 1.9+ server! Mending isn't present in older versions!");
            setEnabled(false);
            return;
        }

        saveDefaultConfig();
        new ConfigManager().migrate();

        FileConfiguration config = getConfig();

        playerConfig = PlayerConfigHandler.of();
        damageHandler = Versioning.isPost17() ? new DamageNewer() : new DamageLegacy();
        repairManager = new RepairManager();
        configBlacklist = new ConfigBlacklist();

        final int SPIGOT_RESOURCE_ID = 112248;
        new UpdateChecker(this, UpdateCheckSource.SPIGET, String.valueOf(SPIGOT_RESOURCE_ID))
                .checkEveryXHours(24)
                .setNotifyOpsOnJoin(true)
                .setDownloadLink(SPIGOT_RESOURCE_ID)
                .checkNow(); // And check right now

        getServer().getPluginManager().registerEvents(new MendingUseListener(), this);
        getServer().getPluginManager().registerEvents(new PreventDestroyListener(), this);

        final CommandBTM commandBTM = new CommandBTM(this, config.getInt("version", 0), playerConfig);
        getCommand("btm").setExecutor(commandBTM);
        getCommand("btm").setTabCompleter(commandBTM);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            boolean registered = new BTMPlaceholderCompat().register();
            if(!registered) {
                getLogger().warning("Unable to pass plugin to PlaceholderAPI! Better Mending placeholders may not work!");
            }
        }

        if(Bukkit.getPluginManager().getPlugin("Oraxen") != null){
            oraxenCompat = new OraxenCompat();
        } else {
            oraxenCompat = new OraxenDefaultCompat();
        }

        if(config.getBoolean("auto-repair", false)) {
            repairManager.initAutoRepair();
        }

        if(config.getBoolean("bstat", true)){
            Metrics metrics = new Metrics(this, 21472);

            metrics.addCustomChart(new Metrics.SimplePie("file_based_config", () -> String.valueOf(playerConfig instanceof PlayerConfigLegacy)));
            metrics.addCustomChart(new Metrics.SimplePie("auto_repair", () -> String.valueOf(config.getBoolean("auto-repair", false))));
        }

        PluginUtils.reloadConfig();

        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled! See you later!");
    }

    public static FileConfiguration getConf() {
        return getInstance().getConfig();
    }

    public static ConfigBlacklist getConfigBlacklist() {
        return getInstance().configBlacklist;
    }

    public static DamageHandler getDamageHandler() {
        return getInstance().damageHandler;
    }

    public static PlayerConfigHandler getPlayerConfig() {
        return getInstance().playerConfig;
    }

    public static RepairManager getRepairManager() {
        return getInstance().repairManager;
    }
}
