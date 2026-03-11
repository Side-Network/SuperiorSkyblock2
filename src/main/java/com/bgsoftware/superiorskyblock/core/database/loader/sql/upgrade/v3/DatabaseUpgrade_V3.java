package com.bgsoftware.superiorskyblock.core.database.loader.sql.upgrade.v3;

import com.bgsoftware.superiorskyblock.core.database.sql.DBSession;

public class DatabaseUpgrade_V3 implements Runnable {

    public static final DatabaseUpgrade_V3 INSTANCE = new DatabaseUpgrade_V3();

    private DatabaseUpgrade_V3() {

    }

    @Override
    public void run() {
        DBSession.addColumn("islands_settings", "peak_member_count", "INTEGER DEFAULT 1");
    }

}
