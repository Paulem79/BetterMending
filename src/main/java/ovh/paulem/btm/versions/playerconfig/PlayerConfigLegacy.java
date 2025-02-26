package ovh.paulem.btm.versions.playerconfig;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import ovh.paulem.btm.BetterMending;

import java.io.IOException;

public class PlayerConfigLegacy extends PlayerConfigHandler {
    protected final YamlConfiguration data;

    PlayerConfigLegacy(BetterMending plugin) {
        super(plugin);

        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    @Nullable
    public Boolean getPlayer(Player player) {
        return (Boolean) this.data.get(player.getUniqueId().toString());
    }

    @Override
    public boolean setPlayer(Player player, boolean enabled) {
        this.data.set(player.getUniqueId().toString(), enabled);

        try {
            this.data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().throwing(PlayerConfigLegacy.class.getName(), "setPlayer", e);
        }

        return enabled;
    }

    @Override
    public void reload() {
        try {
            this.data.load(dataFile);
        } catch (Exception e) {
            plugin.getLogger().throwing(PlayerConfigLegacy.class.getName(), "reload", e);
        }
    }
}
