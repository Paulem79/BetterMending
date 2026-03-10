package net.paulem.btm.utils

import org.bukkit.entity.Player
import kotlin.math.pow

object ExperienceUtils {
    @JvmStatic
    fun changePlayerExp(player: Player, exp: Int) {
        val currentExp: Int = getPlayerXP(player)

        player.exp = 0f
        player.level = 0

        player.giveExp(currentExp + exp)
    }

    @JvmStatic
    fun getPlayerXP(player: Player): Int {
        return (getExperienceForLevel(player.level) + (player.exp * player.expToLevel)).toInt()
    }

    @JvmStatic
    fun getExperienceForLevel(level: Int): Int {
        if (level == 0) return 0

        return when (level) {
            in 1..<16 -> (level.toDouble().pow(2.0) + 6 * level).toInt()
            in 16..<32 -> (2.5 * level.toDouble().pow(2.0) - 40.5 * level + 360).toInt()
            else -> (4.5 * level.toDouble().pow(2.0) - 162.5 * level + 2220).toInt()
        }
    }
}