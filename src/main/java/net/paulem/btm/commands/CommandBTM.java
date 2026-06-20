package net.paulem.btm.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.paulem.btm.BetterMending;
import net.paulem.btm.utils.PluginUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.paulem.btm.versioned.playerconfig.PlayerConfigHandler;

import java.util.Arrays;
import java.util.List;

public class CommandBTM implements TabExecutor {
    public final PlayerConfigHandler playerDataConfig;
    public final String pluginVersion;

    public CommandBTM(PlayerConfigHandler playerDataConfig) {
        this.playerDataConfig = playerDataConfig;
        this.pluginVersion = BetterMending.instance.getDescription().getVersion();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if ((args.length == 1 && args[0].equalsIgnoreCase("version")) || sender instanceof ConsoleCommandSender) {
            TextComponent versionComponent = Component.text("Running BetterThanMending")
                    .color(NamedTextColor.BLUE)
                    .append(Component.text(pluginVersion).color(NamedTextColor.GOLD))
                    .append(Component.text(" with config version ").color(NamedTextColor.BLUE))
                    .append(Component.text(BetterMending.instance.getConfigVersion()).color(NamedTextColor.DARK_GREEN));
            sender.sendMessage(MiniMessage.miniMessage().serialize(versionComponent));

        } else if((args.length == 0 || args[0].equalsIgnoreCase("toggle")) && sender instanceof Player player) {
            boolean enabled = playerDataConfig.setPlayer(player, !playerDataConfig.getPlayerOrCreate(player, true));

            if(enabled) {
                player.sendMessage(PluginUtils.parseConfigText("toggle.enabled", "Mending's ability has been &aenabled &r!"));
            } else {
                player.sendMessage(PluginUtils.parseConfigText("toggle.disabled", "Mending's ability has been &cdisabled &r!"));
            }

            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("btm.reload")) {
                TextComponent noPermissionComponent = Component.text("You don't have permission to do that!")
                        .color(NamedTextColor.RED);

                sender.sendMessage(MiniMessage.miniMessage().serialize(noPermissionComponent));
                return true;
            }

            PluginUtils.reloadConfig();

            sender.sendMessage(ChatColor.GREEN + "Config reloaded!");

            return true;
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0 || args.length == 1) {
            return Arrays.asList("toggle", "reload", "version");
        }

        return null;
    }
}
