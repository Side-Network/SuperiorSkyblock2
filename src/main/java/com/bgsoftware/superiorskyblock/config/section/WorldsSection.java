package com.bgsoftware.superiorskyblock.config.section;

import com.bgsoftware.superiorskyblock.api.config.SettingsManager;
import com.bgsoftware.superiorskyblock.api.enums.Environment;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockOffset;
import com.bgsoftware.superiorskyblock.config.SettingsContainer;

public class WorldsSection implements SettingsManager.Worlds {

    private final SettingsContainer container;
    private final Normal normal = new NormalSection();
    private final Nether nether = new NetherSection();
    private final End end = new EndSection();
    private final Citadel citadel = new CitadelSection();

    public WorldsSection(SettingsContainer container) {
        this.container = container;
    }

    @Override
    public Environment getDefaultWorld() {
        return this.container.defaultWorldEnvironment;
    }

    @Override
    public String getWorldName() {
        return this.container.islandWorldName;
    }

    @Override
    public String getDefaultWorldName() {
        return this.container.defaultWorldName;
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
        return this.container.worldsDifficulty;
    }

    private class NormalSection implements Normal {

        @Override
        public boolean isEnabled() {
            return container.normalWorldEnabled;
        }

        @Override
        public boolean isUnlocked() {
            return container.normalWorldUnlocked;
        }

        @Override
        public boolean isSchematicOffset() {
            return container.normalSchematicOffset;
        }

        @Override
        public String getBiome() {
            return container.normalBiome;
        }
    }

    private class NetherSection implements Nether {

        @Override
        public boolean isEnabled() {
            return container.netherWorldEnabled;
        }

        @Override
        public boolean isUnlocked() {
            return container.netherWorldUnlocked;
        }

        @Override
        public String getName() {
            return container.netherWorldName;
        }

        @Override
        public boolean isSchematicOffset() {
            return container.netherSchematicOffset;
        }

        @Override
        public String getBiome() {
            return container.netherBiome;
        }
    }

    private class EndSection implements End {

        @Override
        public boolean isEnabled() {
            return container.endWorldEnabled;
        }

        @Override
        public boolean isUnlocked() {
            return container.endWorldUnlocked;
        }

        @Override
        public String getName() {
            return container.endWorldName;
        }

        @Override
        public boolean isSchematicOffset() {
            return container.endSchematicOffset;
        }

        @Override
        public String getBiome() {
            return container.endBiome;
        }

        @Override
        public boolean isDragonFight() {
            return container.endDragonFightEnabled;
        }

        @Override
        public BlockOffset getPortalOffset() {
            return container.endDragonFightPortalOffset;
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
