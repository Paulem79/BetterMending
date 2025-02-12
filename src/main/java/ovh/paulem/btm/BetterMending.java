package ovh.paulem.btm;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import org.bstats.bukkit.Metrics;
import ovh.paulem.btm.commands.CommandBTM;
import ovh.paulem.btm.config.PlayerDataConfig;
import ovh.paulem.btm.listeners.MendingUseListener;
import ovh.paulem.btm.listeners.PreventDestroyListener;
import ovh.paulem.btm.config.ConfigManager;
import ovh.paulem.btm.damage.DamageManager;
import ovh.paulem.btm.damage.LegacyDamage;
import ovh.paulem.btm.managers.RepairManager;
import ovh.paulem.btm.damage.NewerDamage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.paulem.btm.utils.PluginUtils;
import ovh.paulem.btm.versions.Versioning;

public class BetterMending extends JavaPlugin {
    public PlayerDataConfig playerDataConfig;
    public RepairManager mainRepairManager;

    @Override
    public void onEnable() {
        if(!Versioning.isPost9()) {
            getLogger().severe("You need to use a 1.9+ server! Mending isn't present in older versions!");
            setEnabled(false);
            return;
        }

        saveDefaultConfig();
        new ConfigManager(this).migrate();

        playerDataConfig = new PlayerDataConfig(this);

        FileConfiguration config = getConfig();

        final DamageManager damageManager = Versioning.isPost17() ? new NewerDamage() : new LegacyDamage();

        mainRepairManager = new RepairManager(this, config, damageManager);

        final int SPIGOT_RESOURCE_ID = 112248;
        new UpdateChecker(this, UpdateCheckSource.SPIGET, String.valueOf(SPIGOT_RESOURCE_ID))
                .checkEveryXHours(24)
                .setNotifyOpsOnJoin(true)
                .setDownloadLink(SPIGOT_RESOURCE_ID)
                .checkNow(); // And check right now

        getServer().getPluginManager().registerEvents(new MendingUseListener(config, damageManager, mainRepairManager, playerDataConfig), this);
        getServer().getPluginManager().registerEvents(new PreventDestroyListener(config, damageManager, mainRepairManager), this);

        final CommandBTM commandBTM = new CommandBTM(this, config.getInt("version", 0), playerDataConfig);
        getCommand("btm").setExecutor(commandBTM);
        getCommand("btm").setTabCompleter(commandBTM);

        getLogger().info("Enabled!");

        if(config.getBoolean("auto-repair", false))
            mainRepairManager.initAutoRepair();

        if(config.getBoolean("bstat", true)){
            new Metrics(this, 21472);
        }

        PluginUtils.reloadConfig(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled! See you later!");
    }
}
