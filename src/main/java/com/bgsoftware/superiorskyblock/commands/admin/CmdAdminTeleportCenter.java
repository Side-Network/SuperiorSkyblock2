package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.Environment;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminIslandCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CmdAdminTeleportCenter implements IAdminIslandCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("center");
    }

    @Override
    public String getPermission() {
        return "superior.admin.teleport";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin center <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ISLAND_NAME.getMessage(locale) + "> [normal/nether/the_end/citadel]";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_TELEPORT.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public int getMaxArgs() {
        return 4;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public boolean supportMultipleIslands() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, SuperiorPlayer targetPlayer, Island island, String[] args) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);

        Environment environment;

        if (args.length != 4) {
            environment = plugin.getSettings().getWorlds().getDefaultWorld();
        } else {
            environment = CommandArguments.getEnvironment(sender, args[3]);
            if (environment == null)
                return;
        }

        if (plugin.getGrid().getIslandsWorld(island, environment) == null) {
            Message.WORLD_NOT_ENABLED.send(sender);
            return;
        }

        if (environment != plugin.getSettings().getWorlds().getDefaultWorld()) {
            if (!island.wasSchematicGenerated(environment)) {
                Message.WORLD_NOT_UNLOCKED.send(sender, Formatters.CAPITALIZED_FORMATTER.format(environment.name()));
                return;
            }
        }

        Location center = island.getCenter(environment);
        if (center != null)
            superiorPlayer.asPlayer().teleport(center);
        else
            superiorPlayer.asPlayer().sendMessage("Couldn't find center!");
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Island island, String[] args) {
        return args.length == 4 ? CommandTabCompletes.getEnvironments(args[3]) : Collections.emptyList();
    }
}
