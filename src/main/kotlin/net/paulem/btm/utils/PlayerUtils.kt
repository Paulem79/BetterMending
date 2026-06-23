package net.paulem.btm.utils

import net.paulem.btm.BetterMending
import org.bukkit.entity.Player

object PlayerUtils {
    @JvmStatic
    fun canUseBtm(player: Player): Boolean {
        return player.hasPermission("btm.use") && !BetterMending.configBlacklist.isBlacklisted(player)
    }

    @JvmStatic
    fun canUseBtmAutoRepair(player: Player): Boolean {
        return player.hasPermission("btm.use.auto-repair") && !BetterMending.configBlacklist.isBlacklisted(player)
    }
}