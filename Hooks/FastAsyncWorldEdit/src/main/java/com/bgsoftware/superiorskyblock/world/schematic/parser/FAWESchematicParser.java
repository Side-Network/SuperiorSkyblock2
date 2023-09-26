package com.bgsoftware.superiorskyblock.world.schematic.parser;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.schematic.parser.SchematicParseException;
import com.bgsoftware.superiorskyblock.api.schematic.parser.SchematicParser;
import com.bgsoftware.superiorskyblock.world.schematic.impl.WorldEditSchematic;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class FAWESchematicParser implements SchematicParser {

    @Override
    public Schematic parseSchematic(DataInputStream inputStream, String schematicName) throws SchematicParseException {
        File file = new File(SuperiorSkyblockPlugin.getPlugin().getDataFolder().getPath() + "/schematics/" + schematicName + ".schem");
        if (!file.exists())
            throw new SchematicParseException("Schematic doesn't exist");

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        Clipboard clipboard;

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SchematicParseException("Couldn't load schematic");
        }

        //noinspection deprecation
        return new WorldEditSchematic(schematicName, clipboard);
    }

}
