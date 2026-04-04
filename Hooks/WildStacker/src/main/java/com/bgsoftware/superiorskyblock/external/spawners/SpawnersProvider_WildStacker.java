package com.bgsoftware.superiorskyblock.external.spawners;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.hooks.SpawnersSnapshotProvider;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.api.service.region.InteractionResult;
import com.bgsoftware.superiorskyblock.api.service.region.RegionManagerService;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.key.Keys;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.external.WildStackerSnapshotsContainer;
import com.bgsoftware.superiorskyblock.module.upgrades.listeners.WildStackerListener;
import com.bgsoftware.superiorskyblock.service.region.ProtectionHelper;
import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.events.SpawnerPlaceEvent;
import com.bgsoftware.wildstacker.api.events.SpawnerPlaceInventoryEvent;
import com.bgsoftware.wildstacker.api.events.SpawnerStackEvent;
import com.bgsoftware.wildstacker.api.events.SpawnerUnstackEvent;
import com.bgsoftware.wildstacker.api.objects.StackedSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class SpawnersProvider_WildStacker implements SpawnersProviderItemMetaSpawnerType, SpawnersSnapshotProvider {

    private static StackerListener stackerListener = null;
    private static WildStackerListener wildStackerListener = null;

    private final SuperiorSkyblockPlugin plugin;
    private final LazyReference<RegionManagerService> protectionManager = new LazyReference<RegionManagerService>() {
        @Override
        protected RegionManagerService create() {
            return plugin.getServices().getService(RegionManagerService.class);
        }
    };

    public SpawnersProvider_WildStacker(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
        // Unregister old listeners if they exist (for reload support)
        if (stackerListener != null) {
            org.bukkit.event.HandlerList.unregisterAll(stackerListener);
        }
        if (wildStackerListener != null) {
            org.bukkit.event.HandlerList.unregisterAll(wildStackerListener);
        }
        // Register new listeners
        stackerListener = new StackerListener();
        wildStackerListener = new WildStackerListener();
        Bukkit.getPluginManager().registerEvents(stackerListener, plugin);
        Bukkit.getPluginManager().registerEvents(wildStackerListener, plugin);
        Log.info("Using WildStacker as a spawners provider.");
    }

    @Override
    public Pair<Integer, String> getSpawner(Location location) {
        Map.Entry<Integer, EntityType> entry;
        try (ChunkPosition chunkPosition = ChunkPosition.of(location)) {
            entry = WildStackerSnapshotsContainer.accessStackedSnapshot(chunkPosition,
                    stackedSnapshot -> stackedSnapshot.getStackedSpawner(location));
        }

        if (entry == null) {
            StackedSpawner stackedSpawner = WildStackerAPI.getWildStacker().getSystemManager().getStackedSpawner(location);
            if (stackedSpawner == null) {
                return new Pair<>(1, null);
            } else {
                return new Pair<>(stackedSpawner.getStackAmount(), stackedSpawner.getSpawnedType().name());
            }
        }

        return new Pair<>(entry.getKey(), entry.getValue() + "");
    }

    @Override
    public void takeSnapshot(Chunk chunk) {
        WildStackerSnapshotsContainer.takeSnapshot(chunk);
    }

    @Override
    public void releaseSnapshot(World world, int chunkX, int chunkZ) {
        try (ChunkPosition chunkPosition = ChunkPosition.of(world, chunkX, chunkZ)) {
            WildStackerSnapshotsContainer.releaseSnapshot(chunkPosition);
        }
    }

    @SuppressWarnings("unused")
    private class StackerListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onSpawnerPlace(SpawnerPlaceEvent e) {
            Island island = plugin.getGrid().getIslandAt(e.getSpawner().getLocation());

            if (island == null)
                return;

            Key blockKey = Keys.ofSpawner(e.getSpawner().getSpawnedType());
            int stackAmount = e.getSpawner().getStackAmount();

            if (island.hasReachedBlockLimit(blockKey, stackAmount)) {
                e.setCancelled(true);
                Message.REACHED_BLOCK_LIMIT.send(e.getPlayer(), Formatters.CAPITALIZED_FORMATTER.format(blockKey.toString()));
            } else if (stackAmount > 1) {
                // Vanilla listener counts 1, so adjust to count the full stack amount
                island.handleBlockBreak(blockKey, 1);
                island.handleBlockPlace(blockKey, stackAmount);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onSpawnerStackCheckLimit(SpawnerStackEvent e) {
            Island island = plugin.getGrid().getIslandAt(e.getSpawner().getLocation());

            if (island == null)
                return;

            Key blockKey = Keys.ofSpawner(e.getSpawner().getSpawnedType());
            int targetAmount = e.getTarget().getStackAmount();

            // Check if adding the target spawner would exceed the limit
            if (island.hasReachedBlockLimit(blockKey, targetAmount)) {
                e.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onSpawnerStack(SpawnerStackEvent e) {
            Island island = plugin.getGrid().getIslandAt(e.getSpawner().getLocation());

            if (island == null)
                return;

            Key blockKey = Keys.ofSpawner(e.getSpawner().getSpawnedType());

            int sourceAmount = e.getSpawner().getStackAmount();
            int targetAmount = e.getTarget().getStackAmount();
            int currentCount = island.getBlockCountAsBigInteger(blockKey).intValue();
            int expectedCount = sourceAmount + targetAmount;

            if (currentCount < expectedCount) {
                // New spawner wasn't counted yet, add the difference
                island.handleBlockPlace(blockKey, expectedCount - currentCount);
            } else if (currentCount > expectedCount) {
                // Multiple stacks exist, WildStacker intercepted the new spawner
                island.handleBlockPlace(blockKey, targetAmount);
            }
            // else: currentCount == expectedCount, both already counted (vanilla counted first)
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onSpawnerUnstack(SpawnerUnstackEvent e) {
            Island island = plugin.getGrid().getIslandAt(e.getSpawner().getLocation());
            if (island != null)
                island.handleBlockBreak(Keys.ofSpawner(e.getSpawner().getSpawnedType()), e.getAmount());
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onSpawnerPlaceInventoryCheckLimit(SpawnerPlaceInventoryEvent e) {
            Island island = plugin.getGrid().getIslandAt(e.getSpawner().getLocation());

            if (island == null)
                return;

            Key blockKey = Keys.ofSpawner(e.getSpawner().getSpawnedType());
            int increaseAmount = e.getIncreaseAmount();

            if (island.hasReachedBlockLimit(blockKey, increaseAmount)) {
                e.setCancelled(true);
                Message.REACHED_BLOCK_LIMIT.send(e.getPlayer(), Formatters.CAPITALIZED_FORMATTER.format(blockKey.toString()));
            }
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onSpawnerPlaceInventoryMonitor(SpawnerPlaceInventoryEvent e) {
            Island island = plugin.getGrid().getIslandAt(e.getSpawner().getLocation());

            if (island == null)
                return;

            Key blockKey = Keys.ofSpawner(e.getSpawner().getSpawnedType());
            int increaseAmount = e.getIncreaseAmount();

            island.handleBlockPlace(blockKey, increaseAmount);
        }

        /* Protection Listener */
        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        public void onSpawnerPlaceInventoryNormal(SpawnerPlaceInventoryEvent e) {
            Island island = plugin.getGrid().getIslandAt(e.getSpawner().getLocation());

            if (island == null)
                return;

            SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());

            InteractionResult interactionResult = protectionManager.get().handleBlockPlace(superiorPlayer, e.getSpawner().getLocation().getBlock());
            if (ProtectionHelper.shouldPreventInteraction(interactionResult, superiorPlayer, true))
                e.setCancelled(true);
        }

    }

}
