package ovh.paulem.btm.versions.playerconfig;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import ovh.paulem.btm.BetterMending;
import ovh.paulem.btm.listeners.ConfigMigrationListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerConfigNewer extends PlayerConfigHandler {
    private final NamespacedKey playerConfigKey = new NamespacedKey(plugin, "playerConfig");
    private static final PersistentDataType<Byte, Boolean> type = PersistentDataType.BOOLEAN;

    @Nullable
    private YamlConfiguration data = null;
    private Map<UUID, Boolean> toMigrate = new HashMap<>();

    PlayerConfigNewer(BetterMending plugin) {
        super(plugin);
    }

    PlayerConfigNewer(BetterMending plugin, Map<UUID, Boolean> toMigrate) {
        this(plugin);
        this.toMigrate = toMigrate;

        plugin.getServer().getPluginManager().registerEvents(new ConfigMigrationListener(plugin.getConfig(), plugin.damageHandler, plugin.mainRepairManager, this), plugin);
    }

    public void migratePlayer(Player player) {
        if(toMigrate.isEmpty()) {
            return;
        }

        if(data == null) {
            data = YamlConfiguration.loadConfiguration(dataFile);
        }

        plugin.getLogger().info(player.getUniqueId().toString());
        if(toMigrate.containsKey(player.getUniqueId())) {
            setPlayer(player, toMigrate.get(player.getUniqueId()));

            data.set(player.getUniqueId().toString(), null);
            try {
                data.save(dataFile);
            } catch (IOException e) {
                plugin.getLogger().throwing(PlayerConfigNewer.class.getName(), "migratePlayer", e);
            }

            toMigrate.remove(player.getUniqueId());
        }

        if(toMigrate.isEmpty()) {
            PlayerConfigHandler.dataFile.delete();
            plugin.getLogger().info("Migration complete!");
        }
    }

    @Override
    @Nullable
    public Boolean getPlayer(Player player) {
        return player.getPersistentDataContainer().get(playerConfigKey, type);
    }

    @Override
    public boolean setPlayer(Player player, boolean enabled) {
        player.getPersistentDataContainer().set(playerConfigKey, type, enabled);
        return enabled;
    }

    @Override
    public void reload() {}
}
