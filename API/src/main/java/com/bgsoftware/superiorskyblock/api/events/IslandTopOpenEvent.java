package com.bgsoftware.superiorskyblock.api.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * IslandTopOpenEvent is called when the island top is requested.
 */
public class IslandTopOpenEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final CommandSender sender;
    private final int page;

    /**
     * The constructor of the event.
     */
    public IslandTopOpenEvent(CommandSender sender, int page) {
        this.sender = sender;
        this.page = page;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CommandSender getSender() {
        return sender;
    }

    public int getPage() {
        return page;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
