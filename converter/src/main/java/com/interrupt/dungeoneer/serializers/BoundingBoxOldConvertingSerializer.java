package com.interrupt.dungeoneer.serializers;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.interrupt.dungeoneer.serializers.hacks.BoundingBoxOld;

/**
 * Some level files were written by the android port with
 * com.interrupt.dungeoneer.serializers.hacks.BoundingBoxOld as the class of
 * stored field values. Those values are read with the old six field layout
 * and converted to real gdx BoundingBox instances.
 */
public class BoundingBoxOldConvertingSerializer extends Serializer<Object> {
	private final CompatibleFieldSerializer<BoundingBoxOld> delegate;

	public BoundingBoxOldConvertingSerializer(Kryo kryo) {
		delegate = new CompatibleFieldSerializer<BoundingBoxOld>(kryo, BoundingBoxOld.class);
	}

	@Override
	public void write(Kryo kryo, Output output, Object object) {
		delegate.write(kryo, output, (BoundingBoxOld)object);
	}

	@Override
	public Object read(Kryo kryo, Input input, Class type) {
		BoundingBoxOld old = delegate.read(kryo, input, BoundingBoxOld.class);
		BoundingBox box = new BoundingBox();
		if (old != null && old.min != null && old.max != null) {
			box.set(old.min, old.max);
		}
		return box;
	}
}
