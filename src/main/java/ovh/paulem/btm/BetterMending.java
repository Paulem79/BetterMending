package ovh.paulem.btm;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import ovh.paulem.btm.commands.CommandBTM;
import ovh.paulem.btm.config.PlayerDataConfig;
import ovh.paulem.btm.libs.bstats.Metrics;
import ovh.paulem.btm.listeners.MendingUseListener;
import ovh.paulem.btm.listeners.PreventDestroyListener;
import ovh.paulem.btm.config.ConfigManager;
import ovh.paulem.btm.damage.DamageManager;
import ovh.paulem.btm.damage.LegacyDamage;
import ovh.paulem.btm.managers.RepairManager;
import ovh.paulem.btm.damage.NewerDamage;
import ovh.paulem.btm.versioning.Versioning;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterMending extends JavaPlugin {
    public PlayerDataConfig playerDataConfig;

    @Override
    public void onEnable() {
        if(!Versioning.isPost9()) {
            getLogger().severe("You need to use a 1.9+ server! Mending isn't present in older versions!");
            setEnabled(false);
            return;
        }

        saveDefaultConfig();
        FileConfiguration config = getConfig();

        new ConfigManager(this).migrate();

        playerDataConfig = new PlayerDataConfig(this);

        final DamageManager damageManager = Versioning.isPost17() ? new NewerDamage() : new LegacyDamage();

        final RepairManager repairManager = new RepairManager(this, config, damageManager);

        final String SPIGOT_RESOURCE_ID = "112248";
        new UpdateChecker(this, UpdateCheckSource.SPIGET, SPIGOT_RESOURCE_ID) // You can also use Spiget instead of Spigot - Spiget's API is usually much faster up to date.
                .checkEveryXHours(24) // Check every 24 hours
                .setChangelogLink(SPIGOT_RESOURCE_ID)
                .setNotifyOpsOnJoin(true)
                .checkNow(); // And check right now

        getServer().getPluginManager().registerEvents(new MendingUseListener(config, damageManager, repairManager, playerDataConfig), this);
        getServer().getPluginManager().registerEvents(new PreventDestroyListener(config, damageManager, repairManager), this);

        final CommandBTM commandBTM = new CommandBTM(config.getInt("version", 0), playerDataConfig, getDescription().getVersion());
        getCommand("btm").setExecutor(commandBTM);
        getCommand("btm").setTabCompleter(commandBTM);

        getLogger().info("Enabled!");

        if(config.getBoolean("auto-repair", false))
            repairManager.initAutoRepair();

        if(config.getBoolean("bstat", true)){
            new Metrics(this, 21472);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled! See you later!");
    }
}
