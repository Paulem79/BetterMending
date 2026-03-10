package net.paulem.btm.utils

object ReflectionUtils {
    /**
     * Retrieves a value from an enum class based on its string representation.
     *
     * @param cls The enum class to search for the desired value.
     * @param name The name of the enum constant to look for. The search is case-insensitive.
     * @return The matching enum constant of type T, or null if no match is found or if an error occurs.
     */
    @JvmStatic
    fun <T> getValueFromEnum(cls: Class<T?>, name: String?): T? {
        try {
            for (obj in cls.getEnumConstants()) {
                if (obj.toString().equals(name, ignoreCase = true)) {
                    return obj
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }
}