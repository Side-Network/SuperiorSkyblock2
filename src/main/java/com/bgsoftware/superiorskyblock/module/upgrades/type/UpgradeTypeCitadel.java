package com.bgsoftware.superiorskyblock.module.upgrades.type;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.Environment;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.island.SIsland;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class UpgradeTypeCitadel implements IUpgradeType {

    private static final List<ISuperiorCommand> commands = new ArrayList<>();

    private final SuperiorSkyblockPlugin plugin;

    public UpgradeTypeCitadel(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Listener getListener() {
        return null;
    }

    @Override
    public List<ISuperiorCommand> getCommands() {
        return commands;
    }

    public void upgradeLevel(SIsland island, int newLevel) {
        Schematic upgrade = plugin.getSchematics().getSchematic("citadel_level_" + newLevel);
        if (upgrade != null)
            upgrade.pasteSchematic(island, island.getCenter(Environment.CITADEL), () -> {}, Throwable::printStackTrace);
    }
}
