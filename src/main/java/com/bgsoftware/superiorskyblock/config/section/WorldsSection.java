package com.bgsoftware.superiorskyblock.config.section;

import com.bgsoftware.superiorskyblock.api.config.SettingsManager;
import com.bgsoftware.superiorskyblock.api.enums.Environment;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockOffset;
import com.bgsoftware.superiorskyblock.config.SettingsContainerHolder;
import org.bukkit.World;
import com.bgsoftware.superiorskyblock.config.SettingsContainer;

public class WorldsSection extends SettingsContainerHolder implements SettingsManager.Worlds {
    private final Normal normal = new NormalSection();
    private final Nether nether = new NetherSection();
    private final End end = new EndSection();
    private final Citadel citadel = new CitadelSection();

    @Override
    public Environment getDefaultWorld() {
        return getContainer().defaultWorldEnvironment;
    }

    @Override
    public String getWorldName() {
        return getContainer().islandWorldName;
    }

    @Override
    public String getDefaultWorldName() {
        return getContainer().defaultWorldName;
    }

    @Override
    public Normal getNormal() {
        return this.normal;
    }

    @Override
    public Nether getNether() {
        return this.nether;
    }

    @Override
    public End getEnd() {
        return this.end;
    }

    @Override
    public Citadel getCitadel() {
        return this.citadel;
    }

    @Override
    public String getDifficulty() {
        return getContainer().worldsDifficulty;
    }

    private class NormalSection implements Normal {

        @Override
        public boolean isEnabled() {
            return getContainer().normalWorldEnabled;
        }

        @Override
        public boolean isUnlocked() {
            return getContainer().normalWorldUnlocked;
        }

        @Override
        public boolean isSchematicOffset() {
            return getContainer().normalSchematicOffset;
        }

        @Override
        public String getBiome() {
            return getContainer().normalBiome;
        }
    }

    private class NetherSection implements Nether {

        @Override
        public boolean isEnabled() {
            return getContainer().netherWorldEnabled;
        }

        @Override
        public boolean isUnlocked() {
            return getContainer().netherWorldUnlocked;
        }

        @Override
        public String getName() {
            return getContainer().netherWorldName;
        }

        @Override
        public boolean isSchematicOffset() {
            return getContainer().netherSchematicOffset;
        }

        @Override
        public String getBiome() {
            return getContainer().netherBiome;
        }
    }

    private class EndSection implements End {

        @Override
        public boolean isEnabled() {
            return getContainer().endWorldEnabled;
        }

        @Override
        public boolean isUnlocked() {
            return getContainer().endWorldUnlocked;
        }

        @Override
        public String getName() {
            return getContainer().endWorldName;
        }

        @Override
        public boolean isSchematicOffset() {
            return getContainer().endSchematicOffset;
        }

        @Override
        public String getBiome() {
            return getContainer().endBiome;
        }

        @Override
        public boolean isDragonFight() {
            return getContainer().endDragonFightEnabled;
        }

        @Override
        public BlockOffset getPortalOffset() {
            return getContainer().endDragonFightPortalOffset;
        }
    }

    private class CitadelSection implements Citadel {

        @Override
        public boolean isEnabled() {
            return container.citadelWorldEnabled;
        }

        @Override
        public boolean isUnlocked() {
            return container.citadelWorldUnlocked;
        }

        @Override
        public String getName() {
            return container.citadelWorldName;
        }

        @Override
        public String getBiome() {
            return container.citadelBiome;
        }

        @Override
        public int getPortalAreaMinX() {
            return container.citadelPortalMinXOffset;
        }

        @Override
        public int getPortalAreaMinY() {
            return container.citadelPortalMinYOffset;
        }

        @Override
        public int getPortalAreaMinZ() {
            return container.citadelPortalMinZOffset;
        }

        @Override
        public int getPortalAreaMaxX() {
            return container.citadelPortalMaxXOffset;
        }

        @Override
        public int getPortalAreaMaxY() {
            return container.citadelPortalMaxYOffset;
        }

        @Override
        public int getPortalAreaMaxZ() {
            return container.citadelPortalMaxZOffset;
        }
    }

}
