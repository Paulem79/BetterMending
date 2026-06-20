package net.paulem.btm.managers

import net.paulem.btm.BetterMending
import net.paulem.btm.utils.ReflectionUtils
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.sin

class ParticleManager {
    // DUST (1.21+), else REDSTONE
    private val dustParticle: Particle? = ReflectionUtils.getValueFromEnum(Particle::class, "DUST")
        ?: ReflectionUtils.getValueFromEnum(Particle::class, "REDSTONE")

    fun summonCircle(player: Player, size: Int) {
        val config = BetterMending.instance.config

        val location = player.location.add(
            config.getDouble("offset.x", 0.0),
            config.getDouble("offset.y", 0.0),
            config.getDouble("offset.z", 0.0)
        )

        val world = location.world ?: return

        val particleLoc = Location(world, location.x, location.y, location.z)
        val targetParticle = dustParticle ?: return

        for (d in 0..90) {
            particleLoc.x = location.x + cos(d.toDouble()) * size
            particleLoc.z = location.z + sin(d.toDouble()) * size

            val particleColor = Color.fromRGB(
                checkRGB(config.getInt("color.red", 144), 144),
                checkRGB(config.getInt("color.green", 238), 238),
                checkRGB(config.getInt("color.blue", 144), 144)
            )

            player.spawnParticle(
                targetParticle,
                particleLoc,
                0, 0.0, 0.0, 0.0, 1.0,
                Particle.DustOptions(particleColor, 1f)
            )
        }
    }

    private fun checkRGB(color: Int, defaultColor: Int): Int {
        return if (color in 0..255) color else defaultColor
    }
}