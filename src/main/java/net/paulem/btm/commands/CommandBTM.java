package net.paulem.btm.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.paulem.btm.BetterMending;
import net.paulem.btm.utils.PluginUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.paulem.btm.versioned.playerconfig.PlayerConfigHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
            BetterMending.languageManager.sendMessage(sender, "version",
                    Placeholder.parsed("plugin", pluginVersion), Placeholder.parsed("config", String.valueOf(BetterMending.instance.getConfigVersion()))
            );

            return true;
        } else if((args.length == 0 || args[0].equalsIgnoreCase("toggle")) && sender instanceof Player player) {
            if(!sender.hasPermission("btm.commands.btm")) {
                BetterMending.languageManager.sendMessage(sender, "nopermission");
                return true;
            }

            boolean enabled = playerDataConfig.setPlayer(player, !playerDataConfig.getPlayerOrCreate(player, true));

            if(enabled) {
                BetterMending.languageManager.sendMessage(player, "toggle.enabled");
            } else {
                BetterMending.languageManager.sendMessage(player, "toggle.disabled");
            }

            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("btm.commands.btm.admin")) {
                BetterMending.languageManager.sendMessage(sender, "nopermission");
                return true;
            }

            PluginUtils.reloadConfig();

            BetterMending.languageManager.sendMessage(sender, "reloaded");

            return true;
        } else if(args.length == 2 && args[0].startsWith("lang") && sender instanceof Player player) {
            if(!sender.hasPermission("btm.commands.btm.language")) {
                BetterMending.languageManager.sendMessage(sender, "nopermission");
                return true;
            }

            String selectedLanguage = args[1];

            if(selectedLanguage.equalsIgnoreCase("reset")) {
                BetterMending.languageManager.removePlayerLanguage(player);
                BetterMending.languageManager.sendMessage(player, "language.set");
                return true;
            }

            // Parse language
            Locale locale = BetterMending.languageManager.parseLocale(selectedLanguage);

            // If locale doesn't exist, set to default
            if(!BetterMending.languageManager.getLocales().contains(locale)) {
                BetterMending.languageManager.removePlayerLanguage(player);
                BetterMending.languageManager.sendMessage(player, "language.set");
                return true;
            }

            BetterMending.languageManager.setPlayerLanguage(player, locale);
            BetterMending.languageManager.sendMessage(player, "language.set");
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0 || args.length == 1) {
            return Arrays.asList("toggle", "reload", "version", "language");
        }

        if(args.length == 2 && args[0].startsWith("lang")) {
            List<String> languageOptions = BetterMending.languageManager.getLocales()
                    .stream()
                    .map(Locale::toString)
                    .collect(Collectors.toCollection(ArrayList::new));
            languageOptions.add("reset");

            return languageOptions;
        }

        return null;
    }
}
