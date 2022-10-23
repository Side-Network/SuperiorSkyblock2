package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandStrike;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminIslandCommand;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.island.IslandUtils;
import com.bgsoftware.superiorskyblock.player.PlayerLocales;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.*;

public class CmdAdminStrikes implements IAdminIslandCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("strikes");
    }

    @Override
    public String getPermission() {
        return "superior.admin.strikes";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin strikes <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ISLAND_NAME.getMessage(locale) + "> give/info/take <...>";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return "Modify strikes for an island";
    }

    @Override
    public int getMinArgs() {
        return 4;
    }

    @Override
    public int getMaxArgs() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public boolean supportMultipleIslands() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, SuperiorPlayer targetPlayer, Island island, String[] args) {
        Locale locale = PlayerLocales.getLocale(sender);
        String islandName = island.getName().isBlank() ? (targetPlayer != null ? targetPlayer.getName() : island.getName()) : island.getName();

        if (args.length == 4) {
            if (args[3].equalsIgnoreCase("give")) {
                sender.sendMessage("You need to provide a reason!");
                return;
            }

            if (args[3].equalsIgnoreCase("take")) {
                sender.sendMessage("You need to provide strike id!");
                return;
            }

            if (args[3].equalsIgnoreCase("info")) {
                Message.STRIKE_LIST.send(sender, islandName, island.getStrikes().size());

                int i = 1;
                for (IslandStrike strike : island.getStrikes()) {
                    String at = Formatters.DATE_FORMATTER.format(new Date(strike.getGivenAt() * 1000));
                    Message.STRIKE_LIST_FORMAT.send(sender, String.valueOf(i), strike.getReason(), strike.getGivenBy(), at);
                    i++;
                }
                return;
            }

            Message.COMMAND_USAGE.send(sender, locale, plugin.getCommands().getLabel() + " " + getUsage(locale));
            return;
        }

        if (args[3].equalsIgnoreCase("give")) {
            StringBuilder reason = new StringBuilder();
            for (int i = 4; i < args.length; i++) {
                reason.append(args[i]);
                if (i + 1 < args.length)
                    reason.append(" ");
            }

            island.addStrike(reason.toString(), sender.getName());

            Message.STRIKE_GIVEN.send(sender, islandName, reason);
            Message.STRIKE_GIVEN_INFO.broadcast(locale, reason);
            return;
        }

        if (args[3].equalsIgnoreCase("take")) {
            int id;
            try {
                id = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                Message.COMMAND_USAGE.send(sender, locale, plugin.getCommands().getLabel() + " " + getUsage(locale));
                return;
            }

            island.removeStrike(id);

            Message.STRIKE_TAKEN.send(sender, String.valueOf(id), islandName);
            return;
        }

        Message.COMMAND_USAGE.send(sender, locale, plugin.getCommands().getLabel() + " " + getUsage(locale));
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Island island, String[] args) {
        if (args.length != 4)
            return Collections.emptyList();

        String lowerArgument = args[3].toLowerCase(Locale.ENGLISH);
        List<String> completions = new ArrayList<>();
        completions.add("give");
        completions.add("info");
        completions.add("take");

        return StringUtil.copyPartialMatches(lowerArgument, completions, new ArrayList<>());
    }
}