package net.paulem.btm.versioned.sounds;

import net.paulem.btm.utils.ReflectionUtils;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

public class SoundsLegacy implements SoundsHandler {
    @Override
    public @Nullable Sound getEndermanTeleportSound() {
        return ReflectionUtils.getValueFromEnum(Sound.class, "ENTITY_ENDERMEN_TELEPORT");
    }
}
