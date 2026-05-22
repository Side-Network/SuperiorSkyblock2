package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * IslandChangeAltLimitEvent is called when the alt-limit of the island is changed.
 */
public class IslandChangeAltLimitEvent extends IslandEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;

    private int altLimit;
    private boolean cancelled = false;

    public IslandChangeAltLimitEvent(@Nullable SuperiorPlayer superiorPlayer, Island island, int altLimit) {
        super(island);
        this.superiorPlayer = superiorPlayer;
        this.altLimit = altLimit;
    }

    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    public int getAltLimit() {
        return altLimit;
    }

    public void setAltLimit(int altLimit) {
        Preconditions.checkArgument(altLimit >= -1, "Cannot set the alt limit to a limit lower than -1.");
        this.altLimit = altLimit;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
