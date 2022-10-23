package com.bgsoftware.superiorskyblock.api.island;

public interface IslandStrike {

    /**
     * Get the island of this strike.
     */
    Island getIsland();

    /**
     * Get the reason of the strike.
     */
    String getReason();

    /**
     * Get the time of the strike.
     */
    long getGivenAt();

    /**
     * Get who gave the strike.
     */
    String getGivenBy();
}
