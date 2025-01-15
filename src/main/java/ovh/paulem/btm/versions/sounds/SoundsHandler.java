package ovh.paulem.btm.versions.sounds;

import org.bukkit.Sound;
import ovh.paulem.btm.versions.Versioning;

public interface SoundsHandler {
    Sound getEndermanTeleportSound();

    static SoundsHandler getSoundHandler() {
        return Versioning.isLegacy() ? new SoundsLegacy() : new SoundsNewer();
    }
}
