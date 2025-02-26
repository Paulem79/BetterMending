package ovh.paulem.btm;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import ovh.paulem.btm.addons.BTMPlaceholder;
import ovh.paulem.btm.commands.CommandBTM;
import ovh.paulem.btm.listeners.MendingUseListener;
import ovh.paulem.btm.listeners.PreventDestroyListener;
import ovh.paulem.btm.config.ConfigManager;
import ovh.paulem.btm.versions.damage.DamageHandler;
import ovh.paulem.btm.versions.damage.DamageLegacy;
import ovh.paulem.btm.managers.RepairManager;
import ovh.paulem.btm.versions.damage.DamageNewer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.paulem.btm.utils.PluginUtils;
import ovh.paulem.btm.versions.Versioning;
import ovh.paulem.btm.versions.playerconfig.PlayerConfigHandler;
import ovh.paulem.btm.versions.playerconfig.PlayerConfigLegacy;

public class BetterMending extends JavaPlugin {
    public PlayerConfigHandler playerConfigHandler;
    public RepairManager mainRepairManager;
    public DamageHandler damageHandler;

    @Override
    public void onEnable() {
        if(!Versioning.isPost9()) {
            getLogger().severe("You need to use a 1.9+ server! Mending isn't present in older versions!");
            setEnabled(false);
            return;
        }

        saveDefaultConfig();
        new ConfigManager(this).migrate();

        FileConfiguration config = getConfig();

        playerConfigHandler = PlayerConfigHandler.of(this);

        damageHandler = Versioning.isPost17() ? new DamageNewer() : new DamageLegacy();

        mainRepairManager = new RepairManager(this, config, damageHandler);

        final int SPIGOT_RESOURCE_ID = 112248;
        new UpdateChecker(this, UpdateCheckSource.SPIGET, String.valueOf(SPIGOT_RESOURCE_ID))
                .checkEveryXHours(24)
                .setNotifyOpsOnJoin(true)
                .setDownloadLink(SPIGOT_RESOURCE_ID)
                .checkNow(); // And check right now

        getServer().getPluginManager().registerEvents(new MendingUseListener(config, damageHandler, mainRepairManager, playerConfigHandler), this);
        getServer().getPluginManager().registerEvents(new PreventDestroyListener(config, damageHandler, mainRepairManager), this);

        final CommandBTM commandBTM = new CommandBTM(this, config.getInt("version", 0), playerConfigHandler);
        getCommand("btm").setExecutor(commandBTM);
        getCommand("btm").setTabCompleter(commandBTM);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new BTMPlaceholder(this).register();
        }

        if(config.getBoolean("auto-repair", false))
            mainRepairManager.initAutoRepair();

        if(config.getBoolean("bstat", true)){
            Metrics metrics = new Metrics(this, 21472);
            metrics.addCustomChart(new SimplePie("file_based_config", () -> String.valueOf(playerConfigHandler instanceof PlayerConfigLegacy)));
            metrics.addCustomChart(new SimplePie("auto_repair", () -> String.valueOf(config.getBoolean("auto-repair", false))));
        }

        PluginUtils.reloadConfig(this);

        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled! See you later!");
    }
}
