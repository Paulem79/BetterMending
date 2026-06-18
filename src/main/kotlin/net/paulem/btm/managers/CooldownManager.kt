package net.paulem.btm.managers

import java.time.Duration
import java.time.Instant
import java.util.*

class CooldownManager(val defaultCooldown: Int) {
    private val map: MutableMap<UUID?, Instant> = HashMap<UUID?, Instant>()

    // Set cooldown
    fun setCooldown(key: UUID?, duration: Duration) {
        map.put(key, Instant.now().plus(duration))
    }

    // Check if cooldown has expired
    fun hasCooldown(key: UUID?): Boolean {
        val now = Instant.now()
        val cooldown = map.getOrDefault(key, now)
        return now.isBefore(cooldown)
    }

    // Remove cooldown
    fun removeCooldown(key: UUID?): Instant? {
        return map.remove(key)
    }

    // Get remaining cooldown time
    fun getRemainingCooldown(key: UUID?): Duration? {
        val now = Instant.now()
        val cooldown = map.getOrDefault(key, now)

        return if (now.isBefore(cooldown)) Duration.between(now, cooldown) else Duration.ZERO
    }
}