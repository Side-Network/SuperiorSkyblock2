package com.bgsoftware.superiorskyblock.api.missions;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import java.util.List;

public interface MissionCategory {

    /**
     * Get the name of the category.
     */
    String getName();

    /**
     * Get the slot of the category in the missions menu.
     */
    int getSlot();

    /**
     * Get all the missions in the category.
     */
    List<Mission<?>> getMissions();

    /**
     * Send a mission boss bar to a player.
     */
    void sendBossBar(SuperiorPlayer superiorPlayer, Mission<?> mission, String action, int progress, int total, double totalProgress);
}
