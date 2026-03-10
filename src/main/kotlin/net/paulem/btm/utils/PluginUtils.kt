package net.paulem.btm.utils

import net.paulem.btm.BetterMending
import net.paulem.btm.listeners.extendables.ManagersListener

object PluginUtils {
    @JvmStatic
    fun reloadConfig() {
        BetterMending.instance.reloadConfig()

        BetterMending.playerConfig.reload()

        ManagersListener.reloadConfig(BetterMending.instance.getConfig())
    }
}