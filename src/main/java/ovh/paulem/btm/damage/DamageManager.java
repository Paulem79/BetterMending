package ovh.paulem.btm.damage;

import com.google.common.primitives.Ints;
import org.bukkit.inventory.ItemStack;

public interface DamageManager {
    boolean hasDamage(ItemStack item);

    int getDamage(ItemStack item);

    void setDamage(ItemStack item, int damage);

    /**
     * May return true if isn't damaged
     */
    boolean isDamageable(ItemStack item);

    static int getDamageCalculation(int itemDamages, int expValue, double ratio) {
        return DamageManager.getDamageCalculation(itemDamages, expValue, 1, ratio);
    }

    static int getDamageCalculation(int itemDamages, int expValue, int xpDivisor, double ratio) {
        return itemDamages - Ints.constrainToRange((int) ((double) expValue / xpDivisor * ratio), 0, itemDamages);
    }
}
