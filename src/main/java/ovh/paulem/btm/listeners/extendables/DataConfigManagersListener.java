package ovh.paulem.btm.listeners.extendables;

import ovh.paulem.btm.versions.damage.DamageHandler;
import ovh.paulem.btm.managers.RepairManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import ovh.paulem.btm.versions.playerconfig.PlayerConfigHandler;

public class DataConfigManagersListener extends ManagersListener {
    protected final PlayerConfigHandler playerConfigHandler;

    public DataConfigManagersListener(@NotNull FileConfiguration config, DamageHandler damageHandler, RepairManager repairManager, PlayerConfigHandler playerConfigHandler) {
        super(config, damageHandler, repairManager);

        this.playerConfigHandler = playerConfigHandler;
    }
}
