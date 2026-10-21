package com.interrupt.dungeoneer.serializers;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.interrupt.dungeoneer.serializers.hacks.BoundingBoxOld;

public class GdxBoundingBoxOldSerializer extends Serializer<BoundingBox> {
	private final CompatibleFieldSerializer<BoundingBoxOld> delegate;

	public GdxBoundingBoxOldSerializer(Kryo kryo) {
		delegate = new CompatibleFieldSerializer<BoundingBoxOld>(kryo, BoundingBoxOld.class);
	}

	@Override
	public void write(Kryo kryo, Output output, BoundingBox object) {
		BoundingBoxOld old = new BoundingBoxOld();
		if (object != null) {
			if (object.min != null && old.min != null) old.min.set(object.min);
			if (object.max != null && old.max != null) old.max.set(object.max);
		}
		delegate.write(kryo, output, old);
	}

	@Override
	public BoundingBox read(Kryo kryo, Input input, Class type) {
		BoundingBoxOld old = delegate.read(kryo, input, BoundingBoxOld.class);
		BoundingBox box = new BoundingBox();
		if (old != null && old.min != null && old.max != null) {
			box.set(old.min, old.max);
		}
		return box;
	}
}
