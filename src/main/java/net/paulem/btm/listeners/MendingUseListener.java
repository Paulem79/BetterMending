package net.paulem.btm.listeners;

import net.paulem.btm.utils.PlayerUtils;
import net.paulem.btm.utils.PluginUtils;
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
import com.github.Anon8281.universalScheduler.UniversalRunnable;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import net.paulem.btm.BetterMending;
import net.paulem.btm.listeners.extendables.ManagersListener;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MendingUseListener extends ManagersListener {
    private final Map<UUID, Integer> cooldownUses = new HashMap<>();
    private final TaskScheduler scheduler;

    public MendingUseListener() {
        super();
        this.scheduler = UniversalScheduler.getScheduler(BetterMending.instance);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        // If the player can't use btm, is in creative or spectator mode, then do not repair
        if(!PlayerUtils.canUseBtm(player) ||
                (player.getGameMode() == GameMode.SPECTATOR ||
                player.getGameMode() == GameMode.CREATIVE)) return;

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
        if(getCooldownManager().getDefaultCooldown() == 0) {

            useRepair(player, item);
        } else if(getCooldownManager().hasCooldown(playerId)) {

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
            getCooldownManager().setCooldown(playerId, Duration.ofSeconds(getCooldownManager().getDefaultCooldown()));

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
        Duration timeLeft = getCooldownManager().getRemainingCooldown(playerId);
        if(timeLeft == null) return;

        if (!(timeLeft.isZero() || timeLeft.isNegative())) {
            if(BetterMending.instance.getConfig().getBoolean("cooldown.message", true)) {
                String text = PluginUtils.parseConfigText(
                                "cooldown.text",
                                ChatColor.DARK_RED + "Please wait " + timeLeft.getSeconds() + " seconds before using this ability!"

                        )
                        .replace("$s", String.valueOf(timeLeft.getSeconds()));

                player.sendMessage(text);
            }
            if(BetterMending.instance.getConfig().getBoolean("cooldown.sound", true))
                player.playSound(
                        player.getLocation(),
                        Sound.ENTITY_ENDERMAN_TELEPORT,
                        1, 1);
        }
    }

    public void useRepair(Player player, ItemStack item){
        boolean playSound = BetterMending.instance.getConfig().getBoolean("playSound", true);
        boolean playEffect = BetterMending.instance.getConfig().getBoolean("playEffect", true);

        // Delay by 1 tick so the repair happens after the event cancellation is fully processed.
        // Without this delay, setCancelled(true) causes the server to restore the inventory state
        // on the client, reverting the durability change.
        scheduler.runTaskLater(new UniversalRunnable() {
            @Override
            public void run() {
                ItemStack currentItem = player.getInventory().getItemInMainHand();
                if (currentItem.isSimilar(item)) {
                    BetterMending.repairManager.repairItem(player, currentItem, playSound, playEffect, false);
                }
            }
        }, 1L);
    }
}
