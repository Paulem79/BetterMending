package net.paulem.btm

import com.jeff_media.updatechecker.UpdateCheckSource
import com.jeff_media.updatechecker.UpdateChecker
import net.paulem.btm.commands.CommandBTM
import net.paulem.btm.compat.BTMPlaceholderCompat
import net.paulem.btm.compat.OraxenCompat
import net.paulem.btm.compat.OraxenDefaultCompat
import net.paulem.btm.config.ConfigBlacklist
import net.paulem.btm.config.ConfigManager
import net.paulem.btm.libs.bstats.Metrics
import net.paulem.btm.listeners.MendingUseListener
import net.paulem.btm.listeners.PreventDestroyListener
import net.paulem.btm.managers.RepairManager
import net.paulem.btm.translation.LanguageManager
import net.paulem.btm.utils.PluginUtils
import net.paulem.btm.versioned.Versioning
import net.paulem.btm.versioned.damage.DamageHandler
import net.paulem.btm.versioned.damage.DamageLegacy
import net.paulem.btm.versioned.damage.DamageNewer
import net.paulem.btm.versioned.playerconfig.PlayerConfigHandler
import net.paulem.btm.versioned.playerconfig.PlayerConfigLegacy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class BetterMending : JavaPlugin() {
    companion object {
        const val SPIGOT_RESOURCE_ID = 112248

        lateinit var instance: BetterMending
        lateinit var playerConfig: PlayerConfigHandler
        lateinit var damageHandler: DamageHandler
        lateinit var repairManager: RepairManager
        lateinit var configBlacklist: ConfigBlacklist
        lateinit var oraxenCompat: OraxenDefaultCompat
        lateinit var languageManager: LanguageManager
    }

    override fun onEnable() {
        super.onEnable()

        instance = this

        if (!Versioning.isPost9()) {
            logger.severe("You need to use a 1.9+ server! Mending isn't present in older versions!")
            isEnabled = false
            return
        }

        saveDefaultConfig()
        ConfigManager().migrate()

        languageManager = LanguageManager(this)

        playerConfig = PlayerConfigHandler.of()
        damageHandler = if (Versioning.isPost17()) DamageNewer() else DamageLegacy()
        repairManager = RepairManager()

        reloadModifiables()

        UpdateChecker(this, UpdateCheckSource.SPIGET, SPIGOT_RESOURCE_ID.toString())
            .checkEveryXHours(24.0)
            .setNotifyOpsOnJoin(true)
            .setDownloadLink(SPIGOT_RESOURCE_ID)
            .checkNow() // And check right now

        server.pluginManager.registerEvents(MendingUseListener(), this)
        server.pluginManager.registerEvents(PreventDestroyListener(), this)

        val commandBTM = CommandBTM(playerConfig)
        getCommand("btm")!!.setExecutor(commandBTM)
        getCommand("btm")!!.tabCompleter = commandBTM

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            val registered = BTMPlaceholderCompat().register()
            if (!registered) {
                logger.warning("Unable to pass plugin to PlaceholderAPI! Better Mending placeholders may not work!")
            }
        }

        oraxenCompat = if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            OraxenCompat()
        } else {
            OraxenDefaultCompat()
        }

        PluginUtils.reloadConfig()

        logger.info("Enabled!")
    }

    override fun onDisable() {
        logger.info("Disabled! See you later!")
    }

    /**
     * Init the methods linked to config values (so reloadable)
     */
    fun reloadModifiables() {
        configBlacklist = ConfigBlacklist()

        // Copy and merge to langs/
        languageManager.init()

        if (config.getBoolean("auto-repair", false)) {
            repairManager.initAutoRepair()
        } else {
            repairManager.stopOldAutoRepair()
        }

        if (config.getBoolean("bstat", true)) {
            val metrics = Metrics(this, 21472)

            metrics.addCustomChart(
                Metrics.SimplePie(
                    "file_based_config"
                ) { (playerConfig is PlayerConfigLegacy).toString() }
            )
            metrics.addCustomChart(
                Metrics.SimplePie(
                    "auto_repair"
                ) { config.getBoolean("auto-repair", false).toString() }
            )
        }
    }

    fun getConfigVersion(): Int {
        return config.getInt("version", 0)
    }
}