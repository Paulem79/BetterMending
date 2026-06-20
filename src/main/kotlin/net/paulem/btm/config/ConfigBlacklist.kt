package net.paulem.btm.config

import net.paulem.btm.BetterMending
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ConfigBlacklist {
    val blacklistedPlayers: List<String>
    val blacklistedItems: List<String>
    val blacklistedLores: List<String>

    init {
        val config = BetterMending.instance.config

        blacklistedPlayers = config.getStringList("blacklisted-players")
            .map { it.lowercase() }

        blacklistedItems = config.getStringList("blacklisted-items")

        blacklistedLores = config.getStringList("blacklisted-lores")
            .map { it.lowercase() }
    }

    fun isBlacklisted(player: Player): Boolean {
        return blacklistedPlayers.contains(player.name.lowercase())
    }

    fun isBlacklisted(stack: ItemStack): Boolean {
        return BetterMending.oraxenCompat.isBlacklisted(stack)
    }
}