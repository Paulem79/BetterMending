package ovh.paulem.btm.config;

import ovh.paulem.btm.BetterMending;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class PlayerDataConfig {
    private final File dataFile;
    private final YamlConfiguration data;

    public PlayerDataConfig(BetterMending plugin) {
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        try {
            this.dataFile.createNewFile();
        } catch (IOException e) {
            plugin.getLogger().severe("Error creating data.yml file !");
        }
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public boolean getPlayer(Player player) {
        return this.data.getBoolean(player.getUniqueId().toString());
    }

    public<T> T getPlayerOrDefault(Player player, T def) {
        Object returned = this.data.get(player.getUniqueId().toString());

        if(returned == null) return def;

        return (T) returned;
    }

    public boolean getPlayerOrCreate(Player player, boolean enabled) throws IOException {
        Object returned = this.data.get(player.getUniqueId().toString());

        if(returned == null) return setPlayer(player, enabled);

        return getPlayer(player);
    }

    public boolean setPlayer(Player player, boolean enabled) throws IOException {
        this.data.set(player.getUniqueId().toString(), enabled);

        this.data.save(this.dataFile);

        return getPlayer(player);
    }
}
