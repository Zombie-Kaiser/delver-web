package com.interrupt.dungeoneer.serializers.v2;

import java.io.File;

import com.badlogic.gdx.files.FileHandle;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.game.OverworldLevel;
import com.interrupt.dungeoneer.serializers.KryoSerializer;

/**
 * TeaVM compatible replacement. Delegates to the JSON based KryoSerializer
 * while keeping the original API used by the overworld generator.
 */
public class ThreadSafeLevelSerializer {

    public Level loadLevel(FileHandle file) {
        return KryoSerializer.loadLevelNoPostLoad(file);
    }

    public Level loadLevel(File file) {
        try {
            return KryoSerializer.loadLevelNoPostLoad(new FileHandle(file));
        }
        catch(Exception ex) {
            return null;
        }
    }

    public Level loadLevel(byte[] bytes) {
        return KryoSerializer.loadLevel(bytes);
    }

    public OverworldLevel loadOverworldLevel(FileHandle file) {
        return KryoSerializer.loadOverworldLevel(file);
    }

    public void saveLevel(FileHandle file, Level level) {
        KryoSerializer.saveLevel(file, level);
    }

    public void saveLevel(File file, Level level) {
        KryoSerializer.saveLevel(new FileHandle(file), level);
    }

    public Object copyObject(Object object) {
        return KryoSerializer.copyObject(object);
    }

    public byte[] toBytes(Object object) {
        return KryoSerializer.toBytes(object);
    }
}
