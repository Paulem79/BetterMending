package ovh.paulem.btm.compat;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;
import ovh.paulem.btm.BetterMending;

public class OraxenCompat extends OraxenDefaultCompat {
    @Override
    public boolean isBlacklisted(ItemStack stack) {
        String id = OraxenItems.getIdByItem(stack);
        return BetterMending.getConfigBlacklist().getBlacklistedItems().contains(id)
                || BetterMending.getConfigBlacklist().getBlacklistedItems().contains(OraxenItems.getItemById(id).getItemName());
    }
}
