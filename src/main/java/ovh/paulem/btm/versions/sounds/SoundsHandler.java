package ovh.paulem.btm.versions.sounds;

import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;
import ovh.paulem.btm.versions.Versioning;

public interface SoundsHandler {
    @Nullable Sound getEndermanTeleportSound();

    static SoundsHandler getSoundHandler() {
        return Versioning.isLegacy() ? new SoundsLegacy() : new SoundsNewer();
    }
}
