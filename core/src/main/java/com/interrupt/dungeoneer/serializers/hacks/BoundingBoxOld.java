package com.interrupt.dungeoneer.serializers.hacks;

import com.badlogic.gdx.math.Vector3;

// The BoundingBox class changed, this is a hack for being able to serialize / deserialize it.
// Nothing really cares about this anyway since it is computed at runtime.
public class BoundingBoxOld {
    public final Vector3 crn[] = new Vector3[8];
    public final Vector3 min = new Vector3();
    public final Vector3 max = new Vector3();
    public final Vector3 cnt = new Vector3();
    public final Vector3 dim = new Vector3();
    public boolean crn_dirty = true;
}