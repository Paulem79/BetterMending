package ovh.paulem.btm.managers;

import com.github.Anon8281.universalScheduler.UniversalRunnable;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import ovh.paulem.btm.BetterMending;
import ovh.paulem.btm.versions.damage.DamageHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ovh.paulem.btm.utils.ExperienceUtils;
import ovh.paulem.btm.utils.MathUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RepairManager {
    private FileConfiguration config;
    private final TaskScheduler scheduler;

    private final DamageHandler damageHandler;
    private final ParticleManager particleManager;

    public RepairManager(BetterMending plugin, FileConfiguration config, DamageHandler damageHandler){
        this.config = config;
        this.scheduler = UniversalScheduler.getScheduler(plugin);

        this.damageHandler = damageHandler;
        this.particleManager = new ParticleManager(plugin, config);
    }

    public void initAutoRepair(){
        long delay = config.getLong("delay", 40L);

        scheduler.runTaskTimer(new UniversalRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(!player.hasPermission("btm.use")) continue;

                    List<ItemStack> damageables = Arrays.stream(player.getInventory().getContents())
                            .filter(i -> i != null &&
                                    i.getItemMeta() != null &&
                                    i.getType() != Material.AIR &&
                                    i.containsEnchantment(Enchantment.MENDING) &&
                                    damageHandler.isDamageable(i) &&
                                    damageHandler.hasDamage(i)
                            ).collect(Collectors.toList());

                    if(!damageables.isEmpty()) {
                        if (config.getBoolean("repairFullInventory", true)) {
                            for (ItemStack item : damageables) {
                                if (item != null) {
                                    repairItem(player, item, false, false, true);
                                }
                            }

                        } else {
                            ItemStack item = damageables.get(ThreadLocalRandom.current().nextInt(damageables.size()));

                            if (item != null) {
                                repairItem(player, item, false, false, true);
                            }
                        }
                    }
                }
            }
        }, delay, delay);
    }

    public void repairItem(Player player, ItemStack item, boolean playSound, boolean playParticle, boolean isAutoRepair){
        int playerXP = ExperienceUtils.getPlayerXP(player);

        int itemDamages = damageHandler.getDamage(item);

        String expValueConfig = config.getString("expValue", "20");
        int expValue = (int) MathUtils.evaluate(expValueConfig.replace("%exp%", "x"), player);

        double ratio = item.getEnchantmentLevel(Enchantment.MENDING) * config.getDouble("ratio", 2.0);

        int autoRepairExpValue = config.getInt("auto-repair-config.expConsumed", 20);

        if (playerXP >= 30 && itemDamages >= expValue * ratio) {
            damageHandler.setDamage(item, DamageHandler.getDamageCalculation(itemDamages, expValue, ratio));
            if(isAutoRepair) ExperienceUtils.changePlayerExp(player, -autoRepairExpValue);
            else ExperienceUtils.changePlayerExp(player, -expValue);
        } else if (playerXP >= expValue/10) {
            damageHandler.setDamage(item, DamageHandler.getDamageCalculation(itemDamages, expValue, 10, ratio));
            if(isAutoRepair) ExperienceUtils.changePlayerExp(player, -autoRepairExpValue/10);
            else ExperienceUtils.changePlayerExp(player, -expValue/10);
        } else return;

        // Should play sound?
        if(playSound) {
            player.playSound(player.getLocation(),
                    Sound.BLOCK_ANVIL_PLACE,
                    (float) config.getDouble("soundVolume", 1),
                    (float) config.getDouble("soundPitch", 1));
        }

        // Should play particle?
        if(playParticle) {
            particleManager.summonCircle(player, config.getInt("range", 3));
        }
    }

    public boolean canRepairItem(Player player, ItemStack item){
        double ratio = item.getEnchantmentLevel(Enchantment.MENDING) * config.getDouble("ratio", 2.0);
        int playerXP = ExperienceUtils.getPlayerXP(player);

        int itemDamages = damageHandler.getDamage(item);

        String expValueConfig = config.getString("expValue", "20");
        int expValue = (int) MathUtils.evaluate(expValueConfig.replace("%exp%", "x"), player);

        return (playerXP >= 30 && itemDamages >= expValue * ratio) || (playerXP >= expValue / 10);
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;
    }
}
