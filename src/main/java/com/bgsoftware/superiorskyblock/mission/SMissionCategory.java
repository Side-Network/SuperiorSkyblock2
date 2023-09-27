package com.bgsoftware.superiorskyblock.mission;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.missions.MissionCategory;
import com.bgsoftware.superiorskyblock.api.service.bossbar.BossBar;
import com.bgsoftware.superiorskyblock.api.service.bossbar.BossBarsService;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.messages.Message;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SMissionCategory implements MissionCategory {

    private final String name;
    private final int slot;
    private final List<Mission<?>> missions;

    public SMissionCategory(String name, int slot, List<Mission<?>> missions) {
        this.name = name;
        this.slot = slot;
        this.missions = missions;
        missions.forEach(mission -> mission.setMissionCategory(this));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSlot() {
        return this.slot;
    }

    @Override
    public List<Mission<?>> getMissions() {
        return Collections.unmodifiableList(this.missions);
    }

    @Override
    public void sendBossBar(SuperiorPlayer superiorPlayer, Mission<?> mission, String action, int progress, int total, double totalProgress) {
        String displayName;
        Optional<MissionData> data = SuperiorSkyblockPlugin.getPlugin().getMissions().getMissionData(mission);
        if (data.isPresent())
            displayName = data.get().getNotCompleted().getItemMeta().getDisplayName();
        else
            displayName = "?";

        double fTotal = BigDecimal.valueOf(totalProgress * 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
        String message = Message.MISSION_BOSS_BAR.getMessage(superiorPlayer.getUserLocale(), displayName, action, Math.min(progress, total), total, fTotal);
        SuperiorSkyblockPlugin.getPlugin().getServices().getService(BossBarsService.class).createStaticBossBar(superiorPlayer.asPlayer(), message, BossBar.Color.YELLOW, Math.min(1.0, (double) progress / total),100);
    }
}
