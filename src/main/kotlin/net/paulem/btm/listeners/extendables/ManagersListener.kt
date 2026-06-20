package net.paulem.btm.listeners.extendables

import net.paulem.btm.BetterMending
import net.paulem.btm.managers.CooldownManager
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.Listener

open class ManagersListener : Listener {
    protected var cooldownManager: CooldownManager

    init {
        cooldownManager = CooldownManager(BetterMending.instance.config.getInt("cooldown.time", 0))
        managersListeners.add(this)
    }

    companion object {
        private val managersListeners = mutableListOf<ManagersListener>()

        @JvmStatic
        fun reloadConfig(config: FileConfiguration) {
            managersListeners.forEach { listener ->
                listener.cooldownManager = CooldownManager(config.getInt("cooldown.time", 0))
            }
        }
    }
}