package ovh.paulem.btm.versions.sounds;

import org.bukkit.Sound;

public class SoundsNewer implements SoundsHandler {
    @Override
    public Sound getEndermanTeleportSound() {
        return Sound.ENTITY_ENDERMAN_TELEPORT;
    }
}
