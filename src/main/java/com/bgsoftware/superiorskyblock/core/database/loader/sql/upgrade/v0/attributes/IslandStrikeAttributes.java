package com.bgsoftware.superiorskyblock.core.database.loader.sql.upgrade.v0.attributes;

public class IslandStrikeAttributes extends AttributesRegistry<IslandStrikeAttributes.Field> {

    public IslandStrikeAttributes() {
        super(Field.class);
    }

    @Override
    public IslandStrikeAttributes setValue(Field field, Object value) {
        return (IslandStrikeAttributes) super.setValue(field, value);
    }

    public enum Field {

        REASON,
        GIVEN_BY,
        GIVEN_AT

    }

}