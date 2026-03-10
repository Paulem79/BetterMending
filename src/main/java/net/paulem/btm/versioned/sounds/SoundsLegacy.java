package net.paulem.btm.versioned.sounds;

import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;
import net.paulem.btm.utils.ReflectionUtils;

public class SoundsLegacy implements SoundsHandler {
    @Override
    public @Nullable Sound getEndermanTeleportSound() {
        return ReflectionUtils.getValueFromEnum(Sound.class, "ENTITY_ENDERMEN_TELEPORT");
    }
}
