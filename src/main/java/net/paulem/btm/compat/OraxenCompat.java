package net.paulem.btm.compat;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;
import net.paulem.btm.BetterMending;

public class OraxenCompat extends OraxenDefaultCompat {
    @Override
    public boolean isBlacklisted(ItemStack stack) {
        String id = OraxenItems.getIdByItem(stack);

        return super.isBlacklisted(stack) || BetterMending.configBlacklist.getBlacklistedItems().contains(id)
                || BetterMending.configBlacklist.getBlacklistedItems().contains(OraxenItems.getItemById(id).getItemName());
    }
}
