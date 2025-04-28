package ovh.paulem.btm.config;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ovh.paulem.btm.BetterMending;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigBlacklist {
    private final List<String> blacklistedPlayers;
    private final List<Material> blacklistedItems;

    public ConfigBlacklist() {
        blacklistedPlayers = BetterMending.getConf().getStringList("blacklisted-players")
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        blacklistedItems = BetterMending.getConf().getStringList("blacklisted-items")
                .stream()
                .map(s -> Material.getMaterial(s.toUpperCase()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public boolean isBlacklisted(Player player) {
        return blacklistedPlayers.contains(player.getName().toLowerCase());
    }

    // TODO : Add support for Oraxen items
    public boolean isBlacklisted(Material material) {
        return blacklistedItems.contains(material);
    }
}
