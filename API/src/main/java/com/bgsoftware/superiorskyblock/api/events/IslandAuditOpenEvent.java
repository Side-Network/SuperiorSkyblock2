package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * IslandAuditOpenEvent is called when a player is trying to open the audit menu.
 */
public class IslandAuditOpenEvent extends IslandEvent {

    private static final HandlerList handlers = new HandlerList();

    private final SuperiorPlayer superiorPlayer;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player who opened the audit.
     */
    public IslandAuditOpenEvent(SuperiorPlayer superiorPlayer, Island island) {
        super(island);
        this.superiorPlayer = superiorPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the player who opened the audit.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
