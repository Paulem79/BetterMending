package net.paulem.btm.config

import net.paulem.btm.BetterMending
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ConfigBlacklist {
    val blacklistedPlayers: List<String>
    val blacklistedItems: List<String>

    init {
        val config = BetterMending.instance.config

        blacklistedPlayers = config.getStringList("blacklisted-players")
            .map { it.lowercase() }

        blacklistedItems = config.getStringList("blacklisted-items")
    }

    fun isBlacklisted(player: Player): Boolean {
        return blacklistedPlayers.contains(player.name.lowercase())
    }

    fun isBlacklisted(stack: ItemStack): Boolean {
        return blacklistedItems.any { it.equals(stack.type.name, ignoreCase = true) }
                || BetterMending.oraxenCompat.isBlacklisted(stack)
    }
}