package net.paulem.btm.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import net.paulem.btm.BetterMending;
import net.paulem.btm.listeners.extendables.ManagersListener;
import net.paulem.btm.versioned.playerconfig.PlayerConfigNewer;

public class ConfigMigrationListener extends ManagersListener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(BetterMending.playerConfig instanceof PlayerConfigNewer) {
            PlayerConfigNewer playerConfigNewer = (PlayerConfigNewer) BetterMending.playerConfig;

            playerConfigNewer.migratePlayer(e.getPlayer());
        }
    }
}
