package ovh.paulem.btm.versions.sounds;

import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

public class SoundsNewer implements SoundsHandler {
    @Override
    public @Nullable Sound getEndermanTeleportSound() {
        return Sound.ENTITY_ENDERMAN_TELEPORT;
    }
}
