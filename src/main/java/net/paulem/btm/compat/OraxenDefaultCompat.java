package net.paulem.btm.compat;

import net.paulem.btm.BetterMending;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class OraxenDefaultCompat {
    public boolean isBlacklisted(ItemStack stack) {
        boolean blacklistedItem = BetterMending.configBlacklist.getBlacklistedItems().contains(stack.getType().name());
        if(blacklistedItem) return true;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;

        List<String> lores = meta.getLore();
        if(lores == null) return false;

        return lores
                .stream()
                .anyMatch(lore -> BetterMending.configBlacklist.getBlacklistedLores().contains(lore.toLowerCase()));
    }
}
