package com.interrupt.dungeoneer.serializers;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.esotericsoftware.kryo.util.MapReferenceResolver;
import com.interrupt.dungeoneer.serializers.hacks.BoundingBoxOld;

/**
 * Old gdx BoundingBox chunks are read through BoundingBoxOld intermediates
 * which kryo registers as shared references before their fields are read.
 * Convert them lazily when a back reference is resolved, so referenced
 * objects are real gdx BoundingBox instances with full field data.
 */
public class ConvertingReferenceResolver extends MapReferenceResolver {
	@Override
	public Object getReadObject(Class type, int id) {
		Object object = super.getReadObject(type, id);
		if(object instanceof BoundingBoxOld) {
			BoundingBoxOld old = (BoundingBoxOld)object;
			BoundingBox box = new BoundingBox();
			if(old.min != null && old.max != null) box.set(old.min, old.max);
			setReadObject(id, box);
			return box;
		}
		return object;
	}
}
