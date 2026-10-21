package com.interrupt.dungeoneer.teavm;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.interrupt.managers.EntityManager;
import com.interrupt.managers.StringManager;
import com.interrupt.utils.JsonUtil;

import java.io.File;
import java.util.HashMap;

/** Dev-only: reproduces the entities.dat Json parse on a JVM to get a full stack trace. */
public class Repro {
    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "assets/data/entities.dat";
        GdxNativesLoader.load();
        StringManager.localizedStrings = new HashMap<>();
        FileHandle f = new FileHandle(new File(path));
        try {
            EntityManager em = JsonUtil.fromJson(EntityManager.class, f);
            System.out.println("REPRO-OK categories=" + (em == null || em.entities == null ? "?" : em.entities.size()));
        }
        catch (Throwable t) {
            System.out.println("REPRO-FAIL");
            t.printStackTrace(System.out);
        }
        System.exit(0);
    }
}
