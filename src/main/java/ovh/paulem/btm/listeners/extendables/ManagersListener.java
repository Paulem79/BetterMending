package ovh.paulem.btm.listeners.extendables;

import ovh.paulem.btm.damage.DamageManager;
import ovh.paulem.btm.managers.CooldownManager;
import ovh.paulem.btm.managers.RepairManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ManagersListener implements Listener {
    private static final List<ManagersListener> MANAGERS_LISTENERS = new ArrayList<>();

    protected FileConfiguration config;
    protected final DamageManager damageManager;
    protected final RepairManager repairManager;
    protected CooldownManager cooldownManager;

    public ManagersListener(@NotNull FileConfiguration config, DamageManager damageManager, RepairManager repairManager) {
        this.config = config;

        this.damageManager = damageManager;
        this.repairManager = repairManager;
        this.cooldownManager = new CooldownManager(config.getInt("cooldown.time", 0));

        MANAGERS_LISTENERS.add(this);
    }

    public static void reloadConfig(FileConfiguration config) {
        MANAGERS_LISTENERS.forEach(listener -> {
            listener.config = config;
            listener.cooldownManager = new CooldownManager(config.getInt("cooldown.time", 0));
        });
    }
}
