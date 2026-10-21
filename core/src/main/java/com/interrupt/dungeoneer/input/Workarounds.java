package com.interrupt.dungeoneer.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Workarounds {
    public static boolean ignoreBrokenGdxControllersOniOS() {
        if (Gdx.app.getType() == Application.ApplicationType.WebGL) return false;
        return Gdx.app.getType() != Application.ApplicationType.iOS;
    }
}
