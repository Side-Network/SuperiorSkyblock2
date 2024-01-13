package com.bgsoftware.superiorskyblock.api.enums;

import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public enum Environment {
    NORMAL(0),
    NETHER(-1),
    THE_END(1);

    private final int id;
    private static final Map<Integer, Environment> lookup = new HashMap();

    static {
        Environment[] var0;
        int var1 = (var0 = values()).length;

        for(int var2 = 0; var2 < var1; ++var2) {
            Environment env = var0[var2];
            lookup.put(env.getId(), env);
        }

    }

    Environment(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public Environment getEnvironment(int id) {
        return lookup.get(id);
    }

    public static Environment of(World.Environment worldEnvironment) {
        return lookup.values().stream().filter(env -> env.getId() == worldEnvironment.getId()).findFirst().orElse(null);
    }
}
