package ovh.paulem.btm.listeners.extendables;

import ovh.paulem.btm.damage.DamageManager;
import ovh.paulem.btm.managers.CooldownManager;
import ovh.paulem.btm.managers.RepairManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ManagersListener implements Listener {
    protected final FileConfiguration config;
    protected final DamageManager damageManager;
    protected final RepairManager repairManager;
    protected final CooldownManager cooldownManager;

    public ManagersListener(@NotNull FileConfiguration config, DamageManager damageManager, RepairManager repairManager){
        this.config = config;

        this.damageManager = damageManager;
        this.repairManager = repairManager;
        this.cooldownManager = new CooldownManager(config.getInt("cooldown.time", 0));
    }
}
