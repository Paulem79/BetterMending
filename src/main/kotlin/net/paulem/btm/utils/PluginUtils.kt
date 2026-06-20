package net.paulem.btm.utils

import net.paulem.btm.BetterMending
import net.paulem.btm.listeners.extendables.ManagersListener

object PluginUtils {
    @JvmStatic
    fun reloadConfig() {
        BetterMending.instance.reloadConfig()
        BetterMending.instance.reloadModifiables()

        BetterMending.playerConfig.reload()

        ManagersListener.reloadConfig(BetterMending.instance.getConfig())
    }

    @JvmStatic
    fun parseConfigText(path: String, def: String): String {
        return BetterMending.instance.getConfig().getString(
            path,
            def
        )!!.replace("&", "§")
    }
}