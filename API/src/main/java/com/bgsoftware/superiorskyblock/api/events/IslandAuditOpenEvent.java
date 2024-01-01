package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * PreIslandCreateEvent is called when a player is trying to open the audit menu.
 */
public class IslandAuditOpenEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final SuperiorPlayer superiorPlayer;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player who created the island.
     */
    public IslandAuditOpenEvent(SuperiorPlayer superiorPlayer) {
        this.superiorPlayer = superiorPlayer;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
