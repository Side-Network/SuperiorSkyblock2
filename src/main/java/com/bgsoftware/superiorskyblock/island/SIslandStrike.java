package com.bgsoftware.superiorskyblock.island;

import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandStrike;

public class SIslandStrike implements IslandStrike {

    private final Island island;
    private final String reason;
    private final long givenAt;
    private final String givenBy;

    public SIslandStrike(Island island, String reason, long givenAt, String givenBy) {
        this.island = island;
        this.reason = reason;
        this.givenAt = givenAt;
        this.givenBy = givenBy;
    }

    @Override
    public Island getIsland() {
        return island;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public long getGivenAt() {
        return givenAt;
    }

    @Override
    public String getGivenBy() {
        return givenBy;
    }
}
