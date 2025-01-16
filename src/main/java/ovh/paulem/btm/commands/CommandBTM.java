package ovh.paulem.btm.commands;

import org.bukkit.configuration.file.FileConfiguration;
import ovh.paulem.btm.BetterMending;
import ovh.paulem.btm.config.PlayerDataConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.paulem.btm.listeners.extendables.ManagersListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CommandBTM implements TabExecutor {
    public final BetterMending plugin;
    public final int configVersion;
    public final PlayerDataConfig playerDataConfig;
    public final String pluginVersion;

    public CommandBTM(BetterMending plugin, int configVersion, PlayerDataConfig playerDataConfig) {
        this.plugin = plugin;
        this.configVersion = configVersion;
        this.playerDataConfig = playerDataConfig;
        this.pluginVersion = plugin.getDescription().getVersion();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if((args.length == 0 || args[0].equalsIgnoreCase("toggle")) && sender instanceof Player) {
            Player player = (Player) sender;

            boolean enabled;
            try {
                enabled = playerDataConfig.getPlayerOrCreate(player, true);

                enabled = playerDataConfig.setPlayer(player, !enabled);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            player.sendMessage("Mending's ability has been successfully " + (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + " !");

            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("btm.reload")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                return true;
            }

            plugin.reloadConfig();

            FileConfiguration reloadedConfig = plugin.getConfig();
            playerDataConfig.reload();
            plugin.mainRepairManager.setConfig(reloadedConfig);
            ManagersListener.reloadConfig(reloadedConfig);

            sender.sendMessage(ChatColor.GREEN + "Config reloaded!");

            return true;
        }

        sender.sendMessage(ChatColor.BLUE + "Running BetterThanMending " + ChatColor.GOLD + pluginVersion + ChatColor.BLUE + " with config version " + ChatColor.DARK_GREEN + configVersion + ChatColor.BLUE);

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Arrays.asList("toggle", "version");
    }
}
