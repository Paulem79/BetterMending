package net.paulem.btm.utils

import kotlin.reflect.KClass

object ReflectionUtils {
    /**
     * Retrieves a value from an enum class based on its string representation.
     */
    fun <T : Enum<T>> getValueFromEnum(cls: KClass<T>, name: String?): T? {
        if (name == null) return null
        return try {
            cls.java.enumConstants?.find { it.name.equals(name, ignoreCase = true) }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Retrieves a value from an enum class based on its string representation.
     */
    @Deprecated(message = "Kotlin alternative is the only one used currently")
    @JvmStatic
    fun <T : Enum<T>> getValueFromEnum(cls: Class<T>, name: String?): T? {
        if (name == null) return null
        return try {
            cls.enumConstants?.find { it.name.equals(name, ignoreCase = true) }
        } catch (_: Exception) {
            null
        }
    }
}