package net.paulem.btm.utils

import net.objecthunter.exp4j.ExpressionBuilder
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min

object MathUtils {
    @JvmStatic
    fun constrainToRange(value: Int, min: Int, max: Int): Int {
        return min(max(value, min), max)
    }

    @JvmStatic
    fun evaluate(js: String, player: Player): Double {
        val expression = ExpressionBuilder(js)
            .variables("x")
            .build()
            .setVariable("x", ExperienceUtils.getPlayerXP(player).toDouble())
        return expression.evaluate()
    }
}