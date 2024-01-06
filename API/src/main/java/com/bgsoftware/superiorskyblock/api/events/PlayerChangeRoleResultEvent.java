package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * PlayerChangeRoleEvent is called when a player has its role changed.
 */
public class PlayerChangeRoleResultEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final SuperiorPlayer superiorPlayer;
    private final PlayerRole newRole;
    private final SuperiorPlayer initiator;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player.
     * @param newRole        The new role for the player.
     * @param initiator      Who changed the role
     */
    public PlayerChangeRoleResultEvent(SuperiorPlayer superiorPlayer, PlayerRole newRole, SuperiorPlayer initiator) {
        super(!Bukkit.isPrimaryThread());
        this.superiorPlayer = superiorPlayer;
        this.newRole = newRole;
        this.initiator = initiator;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the player who created the island.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new role of the player.
     */
    public PlayerRole getNewRole() {
        return newRole;
    }

    /**
     * Get who changed the role
     */
    public SuperiorPlayer getInitiator() {
        return initiator;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
