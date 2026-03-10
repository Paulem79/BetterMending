package net.paulem.btm.managers;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.api.utils.ParticleException;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;
import net.paulem.btm.BetterMending;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import net.paulem.btm.libs.k3kdude.DiscordWebhook;
import net.paulem.btm.utils.CrashDumpInformations;
import net.paulem.btm.utils.ReflectionUtils;
import net.paulem.btm.versioned.Versioning;

public class ParticleManager {
    @Nullable
    private ParticleNativeAPI api;

    private static final String WEBHOOK_URL = "ZkNv2l16cvdFVQPbeXsL/ERTJ6pceDH0dFAu/CRcKM1HBybcfG8Vy397NO4ibTPMSXoj3VZWMutWbwzcX0Yz0mpbFNM8BXWrIAJwoCMEcKEkAHShJQN2qDxEKfZ8XyD8ZBgr6XIYL/ZwGSbrfFQx8HcYbaNgRzbtew==";
    @Nullable
    private DiscordWebhook webhook;

    public ParticleManager() {
        try {
            this.webhook = DiscordWebhook.builder()
                    .username("BetterMending")
                    .sendTo(CrashDumpInformations.decodeWebhookUrl(WEBHOOK_URL))
                    .build();
        } catch (Exception e) {
            this.webhook = null;
        }

        if(Versioning.isLegacy()) {
            try {
                this.api = ParticleNativeCore.loadAPI(BetterMending.getInstance());
            } catch (ParticleException e) {
                this.api = null;
                if(this.webhook != null) {
                    try {
                        this.webhook.setContent("BetterMending: Failed to load ParticleNativeAPI. Particles on legacy versions will not work.\n" +
                                "Exception: " + e.getMessage() + "\nDump informations:\n" + CrashDumpInformations.buildVersionString(BetterMending.getInstance()));
                        this.webhook.send();
                    } catch (Exception ex) {
                        // Ignore
                    }
                }
            }
        }
    }

    public void summonCircle(Player player, int size) {
        Location location = player.getLocation()
                .add(
                        BetterMending.getConf().getDouble("offset.x", 0),
                        BetterMending.getConf().getDouble("offset.y", 0),
                        BetterMending.getConf().getDouble("offset.z", 0)
                );

        if(location.getWorld() == null) return;

        Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        for (int d = 0; d <= 90; d += 1) {
            particleLoc.setX(location.getX() + Math.cos(d) * size);
            particleLoc.setZ(location.getZ() + Math.sin(d) * size);

            Color particleColor = Color.fromRGB(
                    checkRGB(BetterMending.getConf().getInt("color.red", 144), 144),
                    checkRGB(BetterMending.getConf().getInt("color.green", 238), 238),
                    checkRGB(BetterMending.getConf().getInt("color.blue", 144), 144)
            );

            // Use vanilla Spigot when possible (1.13+ supports DustOptions). For legacy (<=1.12.2), send a particle packet via NMS.
            if (!Versioning.isLegacy()) {
                Particle p = ReflectionUtils.getValueFromEnum(Particle.class, "REDSTONE");

                if(p == null) {
                    p = ReflectionUtils.getValueFromEnum(Particle.class, "DUST");
                }

                if (p != null) {
                    // count=0 to use offsets as color, extra=1, data = DustOptions
                    player.spawnParticle(p, particleLoc, 0, 0, 0, 0, 1, new Particle.DustOptions(particleColor, 1));
                } else {
                    // Fallback to legacy packet if Particle enum or DustOptions not available
                    legacyParticle(player, particleLoc, particleColor);
                }
            } else {
                legacyParticle(player, particleLoc, particleColor);
            }
        }
    }

    private int checkRGB(int color, int defaultColor){
        if(color < 0 || color > 255) return defaultColor;
        else return color;
    }

    private void legacyParticle(Player player, Location particleLoc, Color particleColor) {
        if(this.api == null) return;

        try {
            api.LIST_1_8.REDSTONE
                    .packetColored(false, particleLoc, particleColor)
                    .sendTo(player);
        } catch (Exception e) {
            // Failed to send particle packet, possibly due to unsupported server version
            if(this.webhook != null) {
                try {
                    this.webhook.setContent("BetterMending: Failed to send particle packet to player " + player.getName() +
                            ". This may be due to an unsupported server version for ParticleNativeAPI.\n" +
                            "Exception: " + e.getMessage() + "\nDump informations:\n" + CrashDumpInformations.buildVersionString(BetterMending.getInstance()));
                    this.webhook.send();
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }
    }
}