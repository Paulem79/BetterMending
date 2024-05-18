package io.github.paulem.btm.managers;

import com.github.Anon8281.universalScheduler.UniversalRunnable;
import io.github.paulem.btm.BTM;
import io.github.paulem.btm.experience.ExperienceSystem;
import io.github.paulem.btm.interfaces.DamageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RepairManager {
    private final FileConfiguration config;

    private final DamageManager damageManager;
    private final ParticleManager particleManager;

    public RepairManager(BTM plugin, DamageManager damageManager){
        this.config = plugin.getConfig();

        this.damageManager = damageManager;
        this.particleManager = new ParticleManager(plugin);
    }

    public void initAutoRepair(){
        long delay = config.getLong("delay", 40L);

        BTM.getScheduler().runTaskTimer(new UniversalRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(!player.hasPermission("btm.use")) continue;

                    List<ItemStack> damageables = Arrays.stream(player.getInventory().getContents())
                            .filter(i -> i != null &&
                                    i.getItemMeta() != null &&
                                    i.getType() != Material.AIR &&
                                    i.containsEnchantment(Enchantment.MENDING) &&
                                    damageManager.isDamageable(i) &&
                                    damageManager.hasDamage(i)).collect(Collectors.toList());
                    if(!damageables.isEmpty()) {
                        if (config.getBoolean("repairFullInventory", true)) {
                            for (ItemStack item : damageables) {
                                if (item != null)
                                    repairItem(player, item, false, false);
                            }
                        } else {
                            ItemStack item = damageables.get(ThreadLocalRandom.current().nextInt(damageables.size()));
                            if (item != null)
                                repairItem(player, item, false, false);
                        }
                    }
                }
            }
        }, delay, delay);
    }

    public void repairItem(Player player, ItemStack item, boolean playSound, boolean playParticle){

        double ratio = item.getEnchantmentLevel(Enchantment.MENDING) * config.getDouble("ratio", 2.0);
        int playerXP = ExperienceSystem.getPlayerXP(player);

        int itemDamages = damageManager.getDamage(item);

        int expValue = config.getInt("expValue", 20);

        if (playerXP >= 30 && itemDamages >= expValue * ratio) {
            damageManager.setDamage(item, itemDamages - (int) (expValue * ratio));
            ExperienceSystem.changePlayerExp(player, -expValue);
        } else if (playerXP >= expValue/10) {
            damageManager.setDamage(item, itemDamages - (int) (expValue/10 * ratio));
            ExperienceSystem.changePlayerExp(player, -expValue/10);
        } else return;

        // Should play sound?
        if(playSound)
            player.playSound(player.getLocation(),
                    Sound.BLOCK_ANVIL_PLACE,
                    (float) config.getDouble("soundVolume", 1),
                    (float) config.getDouble("soundPitch", 1));

        // Should play particle?
        if(playParticle) {
            particleManager.summonCircle(player, config.getInt("range", 3));
        }
    }

    public boolean canRepairItem(Player player, ItemStack item){
        double ratio = item.getEnchantmentLevel(Enchantment.MENDING) * config.getDouble("ratio", 2.0);
        int playerXP = ExperienceSystem.getPlayerXP(player);

        int itemDamages = damageManager.getDamage(item);

        int expValue = config.getInt("expValue", 20);

        return (playerXP >= 30 && itemDamages >= expValue * ratio) || (playerXP >= expValue / 10);
    }
}
