package com.bgsoftware.superiorskyblock.island.builder;

public class StrikeRecord {

    public final String reason;
    public final long givenAt;
    public final String givenBy;

    public StrikeRecord(String reason, long givenAt, String givenBy) {
        this.reason = reason;
        this.givenAt = givenAt;
        this.givenBy = givenBy;
    }
}
