package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.IslandJoinEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.events.plugin.PluginEventsFactory;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.island.IslandUtils;
import com.bgsoftware.superiorskyblock.island.privilege.IslandPrivileges;
import com.bgsoftware.superiorskyblock.island.role.SPlayerRole;
import com.bgsoftware.superiorskyblock.player.PlayerLocales;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CmdAlt implements ISuperiorCommand {

    private static final List<String> SUB_COMMANDS = Arrays.asList("invite", "accept", "kick", "list");

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("alt");
    }

    @Override
    public String getPermission() {
        return "superior.island.alt";
    }

    @Override
    public String getUsage(Locale locale) {
        return "alt <invite/accept/kick/list>";
    }

    @Override
    public String getDescription(Locale locale) {
        return Message.COMMAND_DESCRIPTION_ALT.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 2;
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
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);
        String subCommand = args[1].toLowerCase(Locale.ENGLISH);

        switch (subCommand) {
            case "invite":
                handleInvite(plugin, superiorPlayer, args);
                break;
            case "accept":
                handleAccept(plugin, superiorPlayer, args);
                break;
            case "kick":
                handleKick(plugin, superiorPlayer, args);
                break;
            case "list":
                handleList(plugin, superiorPlayer);
                break;
            default:
                Message.COMMAND_USAGE.send(sender, PlayerLocales.getLocale(sender), plugin.getCommands().getLabel() + " " + getUsage(PlayerLocales.getLocale(sender)));
        }
    }

    private void handleInvite(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, String[] args) {
        if (args.length < 3) {
            Message.COMMAND_USAGE.send(superiorPlayer, "alt invite <" + Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(superiorPlayer.getUserLocale()) + ">");
            return;
        }

        Island island = superiorPlayer.getIsland();
        if (island == null)
            return;

        if (!superiorPlayer.hasPermission(IslandPrivileges.INVITE_ALT)) {
            Message.NO_ALT_INVITE_PERMISSION.send(superiorPlayer, island.getRequiredPlayerRole(IslandPrivileges.INVITE_ALT));
            return;
        }

        SuperiorPlayer targetPlayer = CommandArguments.getPlayer(plugin, superiorPlayer, args[2]);
        if (targetPlayer == null)
            return;

        if (island.isMember(targetPlayer)) {
            Message.ALREADY_IN_ISLAND_OTHER.send(superiorPlayer);
            return;
        }

        if (island.isBanned(targetPlayer)) {
            Message.INVITE_BANNED_PLAYER.send(superiorPlayer);
            return;
        }

        if (SPlayerRole.altRole() == null) {
            Message.CUSTOM.send(superiorPlayer, "&c&lError | &7Island alts are disabled.", true);
            return;
        }

        if (island.isAltInvited(targetPlayer)) {
            island.revokeAltInvite(targetPlayer);
            IslandUtils.sendMessage(island, Message.ALT_REVOKE_INVITE_ANNOUNCEMENT, Collections.emptyList(),
                    superiorPlayer.getName(), targetPlayer.getName());
            Message.ALT_GOT_REVOKED.send(targetPlayer, superiorPlayer.getName());
            return;
        }

        int altLimit = island.getAltLimit();
        if (altLimit >= 0 && island.getIslandAltCount() >= altLimit) {
            Message.ALT_LIMIT_EXCEED.send(superiorPlayer);
            return;
        }

        if (!PluginEventsFactory.callIslandInviteEvent(island, superiorPlayer, targetPlayer))
            return;

        island.inviteAlt(targetPlayer);
        IslandUtils.sendMessage(island, Message.ALT_INVITE_ANNOUNCEMENT, Collections.emptyList(),
                superiorPlayer.getName(), targetPlayer.getName());
        Message.ALT_GOT_INVITE.send(targetPlayer, superiorPlayer.getName());
    }

    private void handleAccept(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, String[] args) {
        if (!senderHasAcceptPermission(superiorPlayer))
            return;

        Island island;
        if (args.length == 2) {
            List<Island> altInvites = superiorPlayer.getAltInvites();
            island = altInvites.isEmpty() ? null : altInvites.get(0);
        } else {
            SuperiorPlayer targetPlayer = plugin.getPlayers().getSuperiorPlayer(args[2]);
            island = targetPlayer == null ? plugin.getGrid().getIsland(args[2]) : targetPlayer.getIsland();
        }

        if (island == null || !island.isAltInvited(superiorPlayer)) {
            Message.ALT_NO_ISLAND_INVITE.send(superiorPlayer);
            return;
        }

        if (superiorPlayer.getIsland() != null) {
            Message.JOIN_WHILE_IN_ISLAND.send(superiorPlayer);
            return;
        }

        int altLimit = island.getAltLimit();
        if (altLimit >= 0 && island.getIslandAltCount() >= altLimit) {
            Message.ALT_JOIN_FULL_ISLAND.send(superiorPlayer);
            island.revokeAltInvite(superiorPlayer);
            return;
        }

        if (SPlayerRole.altRole() == null) {
            Message.CUSTOM.send(superiorPlayer, "&c&lError | &7Island alts are disabled.", true);
            return;
        }

        if (!PluginEventsFactory.callIslandJoinEvent(island, superiorPlayer, IslandJoinEvent.Cause.ALT_INVITE))
            return;

        IslandUtils.sendMessage(island, Message.ALT_JOIN_ANNOUNCEMENT, Collections.emptyList(), superiorPlayer.getName());
        island.addMember(superiorPlayer, SPlayerRole.altRole());

        if (args.length == 2) {
            if (island.getName().isEmpty())
                Message.ALT_JOINED_ISLAND.send(superiorPlayer, island.getOwner().getName());
            else
                Message.ALT_JOINED_ISLAND_NAME.send(superiorPlayer, island.getName());
        } else {
            Message.ALT_JOINED_ISLAND.send(superiorPlayer, island.getOwner().getName());
        }
    }

    private void handleKick(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, String[] args) {
        if (args.length < 3) {
            Message.COMMAND_USAGE.send(superiorPlayer, "alt kick <" + Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(superiorPlayer.getUserLocale()) + ">");
            return;
        }

        Island island = superiorPlayer.getIsland();
        if (island == null)
            return;

        if (!superiorPlayer.hasPermission(IslandPrivileges.KICK_MEMBER)) {
            Message.NO_KICK_PERMISSION.send(superiorPlayer, island.getRequiredPlayerRole(IslandPrivileges.KICK_MEMBER));
            return;
        }

        SuperiorPlayer targetPlayer = CommandArguments.getPlayer(plugin, superiorPlayer, args[2]);
        if (targetPlayer == null)
            return;

        if (!island.isMember(targetPlayer) || !targetPlayer.getPlayerRole().isAltRole()) {
            Message.PLAYER_NOT_ALT.send(superiorPlayer);
            return;
        }

        if (!IslandUtils.checkKickRestrictions(superiorPlayer, island, targetPlayer))
            return;

        IslandUtils.handleKickPlayer(superiorPlayer, island, targetPlayer);
    }

    private void handleList(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer) {
        Island island = superiorPlayer.getIsland();
        if (island == null)
            return;

        List<SuperiorPlayer> alts = island.getIslandAlts();
        int altLimit = island.getAltLimit();

        Message.ALT_LIST.send(superiorPlayer, String.valueOf(alts.size()), String.valueOf(altLimit));

        if (alts.isEmpty()) {
            Message.ALT_LIST_EMPTY.send(superiorPlayer);
            return;
        }

        for (SuperiorPlayer alt : alts)
            Message.ALT_LIST_FORMAT.send(superiorPlayer, alt.getName());
    }

    private boolean senderHasAcceptPermission(SuperiorPlayer superiorPlayer) {
        if (!superiorPlayer.asPlayer().hasPermission("superior.island.alt.accept")) {
            Message.NO_COMMAND_PERMISSION.send(superiorPlayer, "superior.island.alt.accept");
            return false;
        }

        return true;
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        if (args.length == 2) {
            String argument = args[1].toLowerCase(Locale.ENGLISH);
            return new com.bgsoftware.superiorskyblock.core.SequentialListBuilder<String>()
                    .filter(name -> name.contains(argument))
                    .build(SUB_COMMANDS);
        }

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);
        Island island = superiorPlayer.getIsland();

        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("invite"))
                return CommandTabCompletes.getOnlinePlayers(plugin, args[2], plugin.getSettings().isTabCompleteHideVanished(),
                        onlinePlayer -> island != null && !island.isMember(onlinePlayer));
            if (args[1].equalsIgnoreCase("kick") && island != null)
                return CommandTabCompletes.getIslandMembers(island, args[2],
                        islandMember -> islandMember.getPlayerRole().isAltRole());
            if (args[1].equalsIgnoreCase("accept"))
                return CommandTabCompletes.getOnlinePlayersAndIslands(plugin, args[2],
                        plugin.getSettings().isTabCompleteHideVanished(), (onlinePlayer, onlineIsland) ->
                                onlineIsland != null && onlineIsland.isAltInvited(superiorPlayer));
        }

        return Collections.emptyList();
    }

}
