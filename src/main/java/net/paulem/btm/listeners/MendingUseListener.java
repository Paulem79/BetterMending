package net.paulem.btm.listeners;

import org.bukkit.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import net.paulem.btm.BetterMending;
import net.paulem.btm.listeners.extendables.ManagersListener;
import net.paulem.btm.versioned.sounds.SoundsHandler;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MendingUseListener extends ManagersListener {
    private static final Sound ENDERMAN_TELEPORT_SOUND = SoundsHandler.getSoundHandler().getEndermanTeleportSound();

    private final Map<UUID, Integer> cooldownUses = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        // If the player doesn't have perm btm.use, is in creative or spectator mode, or is blacklisted, then do not repair
        if(!player.hasPermission("btm.use") ||
                Arrays.asList(GameMode.SPECTATOR, GameMode.CREATIVE).contains(player.getGameMode()) ||
                BetterMending.configBlacklist.isBlacklisted(player)) return;

        if(!BetterMending.playerConfig.getPlayerOrCreate(player, true)) return;

        ItemStack item = player.getInventory().getItemInMainHand();

        if(item.getType() == Material.AIR || BetterMending.configBlacklist.isBlacklisted(item)) return;

        if(!BetterMending.damageHandler.isDamageable(item)) return;

        // Continue if item has Mending, the player is sneaking, and he's right-clicking in air
        if(!player.isSneaking() ||
                !item.containsEnchantment(Enchantment.MENDING) ||
                e.getAction() != Action.RIGHT_CLICK_AIR) return;

        // If it doesn't have any damage, return
        if(!BetterMending.damageHandler.hasDamage(item)) return;

        UUID playerId = player.getUniqueId();

        // I like spaghetti. (I'll improve this)
        if(cooldownManager.getDefaultCooldown() == 0) {

            useRepair(player, item);
        } else if(cooldownManager.hasCooldown(playerId)) {

            alertCooldown(playerId, player);
        } else if(cooldownUses.containsKey(playerId)) {

            isInMap(playerId, player, item);
        } else {

            // Put the player in map
            cooldownUses.put(playerId, 1);

            // Make repair
            useRepair(player, item);
        }

        e.setCancelled(true);
    }

    public void isInMap(UUID playerId, Player player, ItemStack item){
        int maxUses = BetterMending.instance.getConfig().getInt("cooldown.uses", 3);

        // If the player used too much times the ability
        if(cooldownUses.get(playerId) > maxUses){
            // Remove him from the uses
            cooldownUses.remove(playerId);

            // Set the cooldown before next use
            cooldownManager.setCooldown(playerId, Duration.ofSeconds(cooldownManager.getDefaultCooldown()));

            // Alert
            alertCooldown(playerId, player);
        } else {
            // If the player didn't use it too much, increase the number of uses
            cooldownUses.put(playerId, cooldownUses.get(playerId)+1);

            // Make the repair
            useRepair(player, item);
        }
    }

    public void alertCooldown(UUID playerId, Player player){
        Duration timeLeft = cooldownManager.getRemainingCooldown(playerId);
        if (!(timeLeft.isZero() || timeLeft.isNegative())) {
            if(BetterMending.instance.getConfig().getBoolean("cooldown.message", true)) {
                String text = BetterMending.instance.getConfig().getString(
                                "cooldown.text",
                                ChatColor.DARK_RED + "Please wait " + timeLeft.getSeconds() + " seconds before using this ability!"

                        ).replace("&", "§")
                        .replace("$s", ""+timeLeft.getSeconds());

                player.sendMessage(text);
            }
            if(BetterMending.instance.getConfig().getBoolean("cooldown.sound", true) && ENDERMAN_TELEPORT_SOUND != null)
                player.playSound(
                        player.getLocation(),
                        ENDERMAN_TELEPORT_SOUND,
                        1, 1);
        }
    }

    public void useRepair(Player player, ItemStack item){
        BetterMending.repairManager.repairItem(player, item, BetterMending.instance.getConfig().getBoolean("playSound", true), BetterMending.instance.getConfig().getBoolean("playEffect", true), false);
    }
}
