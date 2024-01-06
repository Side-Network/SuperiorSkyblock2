package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

/**
 * IslandChangeRolePrivilegeEvent is called when a privilege is changed for a role on an island.
 */
public class IslandChangeRolePrivilegeResultEvent extends IslandEvent {

    private final SuperiorPlayer superiorPlayer;
    private final PlayerRole playerRole;
    private final IslandPrivilege islandPrivilege;

    /**
     * The constructor of the event.
     *
     * @param island         The island that the privilege was changed in.
     * @param superiorPlayer The player that changed the privilege to the other role.
     *                       If null, the privilege was changed by the console.
     * @param playerRole     The role that the privilege was changed for.
     * @param islandPrivilege Island privilege changed
     */
    public IslandChangeRolePrivilegeResultEvent(Island island, @Nullable SuperiorPlayer superiorPlayer, PlayerRole playerRole, IslandPrivilege islandPrivilege) {
        super(island);
        this.superiorPlayer = superiorPlayer;
        this.playerRole = playerRole;
        this.islandPrivilege = islandPrivilege;
    }

    /**
     * Get the player that changed the privilege to the other player.
     * If null, the privilege was changed by the console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the role that the privilege was changed for.
     */
    public PlayerRole getPlayerRole() {
        return playerRole;
    }

    /**
     * Get the privilege that was changed
     */
    public IslandPrivilege getIslandPrivilege() {
        return islandPrivilege;
    }

}
