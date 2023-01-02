package com.bgsoftware.superiorskyblock.module.generators.listeners;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.Environment;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.core.Materials;
import com.bgsoftware.superiorskyblock.core.ServerVersion;
import com.bgsoftware.superiorskyblock.core.key.ConstantKeys;
import com.bgsoftware.superiorskyblock.module.generators.GeneratorsModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;

@SuppressWarnings("unused")
public class GeneratorsListener implements Listener {

    private static final BlockFace[] nearbyFaces = new BlockFace[]{
            BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.UP
    };
    private static final Material BLUE_ICE_MATERIAL = Materials.getMaterialSafe("BLUE_ICE");
    private static final Material SOUL_SOIL_MATERIAL = Materials.getMaterialSafe("SOUL_SOIL");
    private static final Material BASALT_MATERIAL = Materials.getMaterialSafe("BASALT");
    private static final Material LAVA_MATERIAL = Materials.getMaterialSafe("STATIONARY_LAVA", "LAVA");

    private final SuperiorSkyblockPlugin plugin;
    private final GeneratorsModule module;

    public GeneratorsListener(SuperiorSkyblockPlugin plugin, GeneratorsModule module) {
        this.plugin = plugin;
        this.module = module;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFormEvent(BlockFormEvent e) {
        if (!module.isEnabled())
            return;

        Location blockLocation = e.getBlock().getLocation();

        Island island = plugin.getGrid().getIslandAt(blockLocation);

        if (island == null)
            return;

        if (e.getBlock().getType() != LAVA_MATERIAL || e.getNewState().getType() != BASALT_MATERIAL)
            return;

        Environment worldEnvironment = module.isMatchGeneratorWorld() &&
                e.getNewState().getType() == BASALT_MATERIAL ? Environment.NETHER :
                Environment.of(blockLocation.getWorld().getEnvironment());

        Key generatedBlock = island.generateBlock(blockLocation, worldEnvironment, true, module.getBonusGenerators().contains(island));

        if (generatedBlock != null && !generatedBlock.equals(ConstantKeys.COBBLESTONE))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFromToEvent(BlockFromToEvent e) {
        if (!module.isEnabled())
            return;

        Block block = e.getToBlock();

        // Should fix solid blocks from generating custom blocks
        // https://github.com/BG-Software-LLC/SuperiorSkyblock2/issues/837
        if (block.getType().isSolid())
            return;

        Location blockLocation = block.getLocation();

        Island island = plugin.getGrid().getIslandAt(blockLocation);

        if (island == null)
            return;

        if (e.getBlock().getType() != LAVA_MATERIAL)
            return;

        GeneratorType generatorType = findGenerator(block);

        if (generatorType == GeneratorType.NONE)
            return;

        Environment worldEnvironment = module.isMatchGeneratorWorld() &&
                generatorType == GeneratorType.BASALT ? Environment.NETHER :
                Environment.of(blockLocation.getWorld().getEnvironment());

        Key generatedBlock = island.generateBlock(blockLocation, worldEnvironment, true, module.getBonusGenerators().contains(island));

        if (generatedBlock != null && !generatedBlock.equals(ConstantKeys.COBBLESTONE))
            e.setCancelled(true);
    }

    private GeneratorType findGenerator(Block block) {
        if (ServerVersion.isAtLeast(ServerVersion.v1_16) &&
                Environment.of(block.getWorld().getEnvironment()) == Environment.NETHER) {
            for (BlockFace blockFace : nearbyFaces) {
                if (block.getRelative(blockFace).getType() == BLUE_ICE_MATERIAL &&
                        block.getRelative(BlockFace.DOWN).getType() == SOUL_SOIL_MATERIAL)
                    return GeneratorType.BASALT;
            }
        } else {
            for (BlockFace blockFace : nearbyFaces) {
                if (plugin.getNMSWorld().isWaterLogged(block.getRelative(blockFace)))
                    return GeneratorType.NORMAL;
            }
        }

        return GeneratorType.NONE;
    }

    private enum GeneratorType {

        NORMAL,
        BASALT,
        NONE

    }


}
