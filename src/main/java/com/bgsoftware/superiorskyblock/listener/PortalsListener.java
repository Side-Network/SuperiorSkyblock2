package com.bgsoftware.superiorskyblock.listener;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.Environment;
import com.bgsoftware.superiorskyblock.api.events.IslandChangeLevelBonusEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandChangeWorthBonusEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandEnterEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.player.PlayerStatus;
import com.bgsoftware.superiorskyblock.api.service.portals.EntityPortalResult;
import com.bgsoftware.superiorskyblock.api.service.portals.PortalsManagerService;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import com.bgsoftware.superiorskyblock.core.Materials;
import com.bgsoftware.superiorskyblock.core.ServerVersion;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.player.SuperiorNPCPlayer;
import com.bgsoftware.superiorskyblock.world.EntityTeleports;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PortalsListener implements Listener {

    private final LazyReference<PortalsManagerService> portalsManager = new LazyReference<PortalsManagerService>() {
        @Override
        protected PortalsManagerService create() {
            return plugin.getServices().getService(PortalsManagerService.class);
        }
    };

    private final SuperiorSkyblockPlugin plugin;

    public PortalsListener(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent e) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());

        if (superiorPlayer instanceof SuperiorNPCPlayer)
            return;

        PortalType portalType = (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) ? PortalType.NETHER : PortalType.ENDER;
        if (portalType == PortalType.ENDER && e.getFrom().getWorld().getEnvironment() == World.Environment.NORMAL)
            portalType = PortalType.CUSTOM;

        EntityPortalResult portalResult = this.portalsManager.get().handlePlayerPortal(superiorPlayer, e.getFrom(),
                portalType, e.getTo(), true);

        switch (portalResult) {
            case DESTINATION_WORLD_DISABLED:
            case PORTAL_NOT_IN_ISLAND:
                return;
            case PLAYER_IMMUNED_TO_PORTAL:
            case SCHEMATIC_GENERATING_COOLDOWN:
            case DESTINATION_NOT_ISLAND_WORLD:
            case PORTAL_EVENT_CANCELLED:
            case INVALID_SCHEMATIC:
            case WORLD_NOT_UNLOCKED:
            case DESTINATION_ISLAND_NOT_PERMITTED:
            case SUCCEED:
                e.setCancelled(true);
                return;
            default:
                throw new IllegalStateException("No handling for result: " + portalResult);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPortalEnter(EntityPortalEnterEvent e) {
        Island island = plugin.getGrid().getIslandAt(e.getEntity().getLocation());

        if (island == null)
            return;

        World world = e.getLocation().getWorld();

        // Simulate end portal
        if (Environment.of(world.getEnvironment()) == Environment.THE_END && plugin.getGrid().isIslandsWorld(world)) {
            if (island.wasSchematicGenerated(Environment.NORMAL)) {
                /* We teleport the player to his island instead of cancelling the event.
                Therefore, we must prevent the player from acting like he entered another island or left his island.*/

                SuperiorPlayer teleportedPlayer = e.getEntity() instanceof Player ?
                        plugin.getPlayers().getSuperiorPlayer((Player) e.getEntity()) : null;

                if (teleportedPlayer != null)
                    teleportedPlayer.setPlayerStatus(PlayerStatus.LEAVING_ISLAND);
                else
                    return;

                BukkitExecutor.sync(() -> {
                    EntityTeleports.teleportUntilSuccess(e.getEntity(), island.getIslandHome(Environment.NORMAL), 5, () -> {
                        if (teleportedPlayer.getPlayerStatus() == PlayerStatus.LEAVING_ISLAND)
                            teleportedPlayer.setPlayerStatus(PlayerStatus.NONE);
                    });
                }, 5L);
            }
        }

        if (ServerVersion.isLessThan(ServerVersion.v1_16))
            return;

        boolean isPlayer = e.getEntity() instanceof Player;
        if (!isPlayer)
            return;

        Material originalMaterial = e.getLocation().getBlock().getType();
        PlayerTeleportEvent.TeleportCause teleportCause = originalMaterial == Materials.NETHER_PORTAL.toBukkitType() ?
                PlayerTeleportEvent.TeleportCause.NETHER_PORTAL : PlayerTeleportEvent.TeleportCause.END_PORTAL;

        PortalType portalType = originalMaterial == Materials.NETHER_PORTAL.toBukkitType() ? PortalType.NETHER : PortalType.ENDER;
        if (portalType == PortalType.ENDER && Environment.of(world.getEnvironment()) == Environment.NORMAL)
            portalType = PortalType.CUSTOM;

        if (teleportCause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL ? Bukkit.getAllowNether() : Bukkit.getAllowEnd())
            return;

        if (teleportCause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            int ticksDelay = ((Player) e.getEntity()).getGameMode() == GameMode.CREATIVE ? 1 : 80;
            int portalTicks = plugin.getNMSEntities().getPortalTicks(e.getEntity());
            if (portalTicks != ticksDelay)
                return;
        }

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getEntity());
        this.portalsManager.get().handlePlayerPortalFromIsland(superiorPlayer, island, e.getLocation(), portalType, true);
    }

}
