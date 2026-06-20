package net.paulem.btm.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.regex.Pattern;

public class MiniMessageToLegacy {
    private MiniMessageToLegacy() {
        /* This utility class should not be instantiated */
    }


    /**
     * Convertit une chaîne MiniMessage en chaîne de caractères Legacy.
     *
     * @param miniMessage   La chaîne MiniMessage source (ex: "<red>Hello <b>World")
     * @param legacyChar    Le caractère de couleur à utiliser (généralement '&' ou '§')
     * @param supportRgb    Activer ou non le support des couleurs Hexadécimales (RGB)
     * @param useHashFormat Si vrai, utilise le format "&#rrggbb". Si faux, utilise le format standard BungeeCord "&x&r&r&g&g&b&b"
     * @return La chaîne convertie au format legacy
     */
    public static String convert(String miniMessage, char legacyChar, boolean supportRgb, boolean useHashFormat) {
        if (miniMessage == null || miniMessage.isEmpty()) {
            return miniMessage;
        }

        // 1. On utilise le parser MiniMessage pour obtenir un Component clean
        Component component = MiniMessage.miniMessage().deserialize(miniMessage);

        // 2. On configure le sérialiseur Legacy d'Adventure
        LegacyComponentSerializer.Builder builder = LegacyComponentSerializer.builder()
                .character(legacyChar);

        if (supportRgb) {
            builder.hexColors(); // Active le support RGB d'Adventure
        }

        LegacyComponentSerializer serializer = builder.build();
        String legacyText = serializer.serialize(component);

        // 3. Post-traitement optionnel pour correspondre exactement à votre format JS (&#rrggbb)
        // Par défaut, Adventure sérialise le RGB au format natif BungeeCord (ex: &x&f&f&f&f&0&0)
        if (supportRgb && useHashFormat) {
            legacyText = convertBungeeHexToHash(legacyText, legacyChar);
        }

        return legacyText;
    }

    /**
     * Méthode utilitaire interne pour transformer le format RGB Bungee (&x&1&2&3&4&5&6)
     * vers le format court de votre script JS (&#123456)
     */
    private static String convertBungeeHexToHash(String text, char legacyChar) {
        String escapedChar = Pattern.quote(String.valueOf(legacyChar));
        String regex = escapedChar + "x"
                + escapedChar + "([0-9a-fA-F])"
                + escapedChar + "([0-9a-fA-F])"
                + escapedChar + "([0-9a-fA-F])"
                + escapedChar + "([0-9a-fA-F])"
                + escapedChar + "([0-9a-fA-F])"
                + escapedChar + "([0-9a-fA-F])";

        return text.replaceAll(regex, legacyChar + "#$1$2$3$4$5$6");
    }

    // --- Surcharges pratiques pour simplifier l'utilisation courante ---

    /** Utilise le caractère '&' standard et supporte le RGB au format court (&#rrggbb) */
    public static String toAmpersand(String miniMessage) {
        return convert(miniMessage, LegacyComponentSerializer.AMPERSAND_CHAR, true, true);
    }

    /** Utilise le caractère de section '§' natif de Minecraft avec le format RGB de BungeeCord */
    public static String toNativeSection(String miniMessage) {
        return convert(miniMessage, LegacyComponentSerializer.SECTION_CHAR, true, false);
    }
}