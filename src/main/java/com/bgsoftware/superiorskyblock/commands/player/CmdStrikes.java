package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandStrike;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.IslandArgument;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CmdStrikes implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return List.of("strikes");
    }

    @Override
    public String getPermission() {
        return "superior.island.strikes";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "strikes";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_STRIKES.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        IslandArgument arguments = CommandArguments.getSenderIsland(plugin, sender);
        Island island = arguments.getIsland();
        if (island == null)
            return;

        String islandName = island.getName().isBlank() ? sender.getName() : island.getName();
        Message.STRIKE_LIST.send(sender, islandName, island.getStrikes().size());

        int i = 1;
        for (IslandStrike strike : island.getStrikes()) {
            String at = Formatters.DATE_FORMATTER.format(new Date(strike.getGivenAt() * 1000));
            Message.STRIKE_LIST_FORMAT.send(sender, String.valueOf(i), strike.getReason(), strike.getGivenBy(), at);
            i++;
        }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}