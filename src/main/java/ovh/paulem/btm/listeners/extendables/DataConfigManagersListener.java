package ovh.paulem.btm.listeners.extendables;

import ovh.paulem.btm.config.PlayerDataConfig;
import ovh.paulem.btm.damage.DamageManager;
import ovh.paulem.btm.managers.RepairManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class DataConfigManagersListener extends ManagersListener {
    protected final PlayerDataConfig playerDataConfig;

    public DataConfigManagersListener(@NotNull FileConfiguration config, DamageManager damageManager, RepairManager repairManager, PlayerDataConfig playerDataConfig) {
        super(config, damageManager, repairManager);

        this.playerDataConfig = playerDataConfig;
    }
}
