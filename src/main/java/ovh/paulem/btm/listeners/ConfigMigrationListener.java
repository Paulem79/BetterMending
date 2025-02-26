package ovh.paulem.btm.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import ovh.paulem.btm.listeners.extendables.DataConfigManagersListener;
import ovh.paulem.btm.managers.RepairManager;
import ovh.paulem.btm.versions.damage.DamageHandler;
import ovh.paulem.btm.versions.playerconfig.PlayerConfigHandler;
import ovh.paulem.btm.versions.playerconfig.PlayerConfigNewer;

public class ConfigMigrationListener extends DataConfigManagersListener {
    public ConfigMigrationListener(@NotNull FileConfiguration config, DamageHandler damageHandler, RepairManager repairManager, PlayerConfigHandler playerConfigHandler) {
        super(config, damageHandler, repairManager, playerConfigHandler);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(playerConfigHandler instanceof PlayerConfigNewer) {
            PlayerConfigNewer playerConfigNewer = (PlayerConfigNewer) playerConfigHandler;
            playerConfigNewer.migratePlayer(e.getPlayer());
        }
    }
}
