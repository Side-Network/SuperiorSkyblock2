package com.bgsoftware.superiorskyblock.module.upgrades.type;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpgradeTypeCitadel implements IUpgradeType {

    private static final List<ISuperiorCommand> commands = new ArrayList<>();

    private final SuperiorSkyblockPlugin plugin;

    public UpgradeTypeCitadel(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<Listener> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public List<ISuperiorCommand> getCommands() {
        return commands;
    }
}
