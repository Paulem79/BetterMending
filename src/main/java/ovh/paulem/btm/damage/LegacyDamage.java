package ovh.paulem.btm.damage;

import org.bukkit.inventory.ItemStack;

public class LegacyDamage implements DamageManager {
    @Override
    public boolean hasDamage(ItemStack item) {
        return (item.getType().getMaxDurability() - item.getDurability()) < item.getType().getMaxDurability();
    }

    @Override
    public int getDamage(ItemStack item) {
        return item.getDurability();
    }

    @Override
    public void setDamage(ItemStack item, int damage) {
        item.setDurability((short) damage);
    }

    @Override
    public boolean isDamageable(ItemStack item) {
        return item.getDurability() != 0;
    }
}
