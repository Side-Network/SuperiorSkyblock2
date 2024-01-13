package com.bgsoftware.superiorskyblock.module.upgrades.type;

import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.island.SIsland;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class UpgradeTypeCitadel implements IUpgradeType {

    private static final List<ISuperiorCommand> commands = new ArrayList<>();

    @Override
    public Listener getListener() {
        return null;
    }

    @Override
    public List<ISuperiorCommand> getCommands() {
        return commands;
    }

    public void upgradeLevel(SIsland island, int newLevel) {

    }
}
