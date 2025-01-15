package ovh.paulem.btm.versions.sounds;

import org.bukkit.Sound;

public class SoundsLegacy implements SoundsHandler{
    @Override
    public Sound getEndermanTeleportSound() {
        return Sound.valueOf("ENTITY_ENDERMEN_TELEPORT");
    }
}
