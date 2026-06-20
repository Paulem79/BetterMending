package net.paulem.btm.versioned.playerconfig;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import net.paulem.btm.BetterMending;
import net.paulem.btm.listeners.ConfigMigrationListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerConfigNewer extends PlayerConfigHandler {
    private final NamespacedKey playerConfigKey = new NamespacedKey(BetterMending.instance, "playerConfig");
    private static final PersistentDataType<Byte, Byte> type = PersistentDataType.BYTE;

    @Nullable
    private YamlConfiguration data = null;
    private final Map<UUID, Boolean> toMigrate;

    public PlayerConfigNewer() {
        this.toMigrate = new HashMap<>();
    }

    PlayerConfigNewer(Map<UUID, Boolean> toMigrate) {
        this.toMigrate = toMigrate;

        BetterMending.instance.getServer().getPluginManager().registerEvents(new ConfigMigrationListener(), BetterMending.instance);
    }

    public void migratePlayer(Player player) {
        if(toMigrate.isEmpty()) {
            return;
        }

        if(data == null) {
            data = YamlConfiguration.loadConfiguration(dataFile);
        }

        BetterMending.instance.getLogger().info(player.getUniqueId().toString());
        if(toMigrate.containsKey(player.getUniqueId())) {
            setPlayer(player, toMigrate.get(player.getUniqueId()));

            data.set(player.getUniqueId().toString(), null);
            try {
                data.save(dataFile);
            } catch (IOException e) {
                BetterMending.instance.getLogger().throwing(PlayerConfigNewer.class.getName(), "migratePlayer", e);
            }

            toMigrate.remove(player.getUniqueId());
        }

        if(toMigrate.isEmpty()) {
            PlayerConfigHandler.dataFile.delete();
            BetterMending.instance.getLogger().info("Migration complete!");
        }
    }

    @Override
    @Nullable
    public Boolean getPlayer(Player player) {
        Byte playerConfigValue = player.getPersistentDataContainer().get(playerConfigKey, type);

        if(playerConfigValue == null) return null;

        return playerConfigValue == 1;
    }

    @Override
    public boolean setPlayer(Player player, boolean enabled) {
        player.getPersistentDataContainer().set(playerConfigKey, type, enabled ? (byte) 1 : (byte) 0);
        return enabled;
    }

    @Override
    public void reload() {}
}
