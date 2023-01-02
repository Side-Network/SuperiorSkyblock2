package com.bgsoftware.superiorskyblock.world.schematic.impl;

import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.logging.Debug;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.world.schematic.BaseSchematic;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class WorldEditSchematic extends BaseSchematic implements Schematic {

    private final Clipboard schematic;

    public WorldEditSchematic(String name, Clipboard schematic) {
        super(name);
        this.schematic = schematic;
    }

    @Override
    public void pasteSchematic(Island island, Location location, Runnable callback) {
        pasteSchematic(island, location, callback, null);
    }

    @Override
    public void pasteSchematic(Island island, Location location, Runnable callback, Consumer<Throwable> onFailure) {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()))) {
            Log.debug(Debug.PASTE_SCHEMATIC, this.name, island.getOwner().getName(), location);

            BlockVector3 at = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

            Operation operation = new ClipboardHolder(schematic)
                    .createPaste(editSession)
                    .to(at)
                    .build();
            Operations.complete(operation);

            editSession.commit();
        } catch (Throwable ex) {
            if (onFailure != null)
                onFailure.accept(ex);
        }
    }

    @Override
    public Location adjustRotation(Location location) {
        return location;
    }

    @Override
    public List<ChunkPosition> getAffectedChunks() {
        return Collections.emptyList();
    }
}
