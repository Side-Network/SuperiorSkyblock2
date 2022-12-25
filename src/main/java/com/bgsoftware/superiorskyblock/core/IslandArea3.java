package com.bgsoftware.superiorskyblock.core;

public class IslandArea3 extends IslandArea {

    private final int minY;
    private final int maxY;

    public IslandArea3(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        super(minX, minZ, maxX, maxZ);
        this.minY = minY;
        this.maxY = maxY;
    }

    public boolean intercepts(int x, int y, int z) {
        return x >= this.minX && x <= this.maxX &&
                y >= this.minY && y <= this.maxY &&
                z >= this.minZ && z <= this.maxZ;
    }
}
