package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

/**
 * IslandChangePlayerPrivilegeEvent is called when a privilege is changed for a player on an island.
 */
public class IslandChangePlayerPrivilegeResultEvent extends IslandEvent {

    private final SuperiorPlayer superiorPlayer;
    private final SuperiorPlayer privilegedPlayer;
    private final IslandPrivilege islandPrivilege;
    private final boolean privilegeEnabled;

    /**
     * The constructor of the event.
     *
     * @param island           The island that the privilege was changed in.
     * @param superiorPlayer   The player that changed the privilege to the other player.
     * @param privilegedPlayer The player that the privilege was changed for.
     * @param islandPrivilege  The privilege that got changed
     * @param privilegeEnabled Whether the privilege was enabled or disabled for the player.
     */
    public IslandChangePlayerPrivilegeResultEvent(Island island, SuperiorPlayer superiorPlayer, SuperiorPlayer privilegedPlayer, IslandPrivilege islandPrivilege, boolean privilegeEnabled) {
        super(island);
        this.superiorPlayer = superiorPlayer;
        this.privilegedPlayer = privilegedPlayer;
        this.islandPrivilege = islandPrivilege;
        this.privilegeEnabled = privilegeEnabled;
    }

    /**
     * Get the player that changed the privilege to the other player.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the player that the privilege was changed for.
     */
    public SuperiorPlayer getPrivilegedPlayer() {
        return privilegedPlayer;
    }

    /**
     * Get the privilege that got changed
     */
    public IslandPrivilege getIslandPrivilege() {
        return islandPrivilege;
    }

    /**
     * Check whether the privilege was enabled to {@link #getPrivilegedPlayer()}
     */
    public boolean isPrivilegeEnabled() {
        return privilegeEnabled;
    }

}
