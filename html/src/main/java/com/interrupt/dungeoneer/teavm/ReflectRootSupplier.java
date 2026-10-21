package com.interrupt.dungeoneer.teavm;

import org.teavm.classlib.ReflectionContext;
import org.teavm.classlib.ReflectionSupplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Roots game classes so that Class.forName can resolve classes that are only
 * referenced from data files (entities, items, triggers instantiated by name
 * from .dat content). TeaReflectionSupplier alone only marks classes that are
 * already reachable from code, so data-only classes get removed by TeaVM DCE.
 * This supplier links every game class unconditionally through
 * getClassesFoundByName, matching desktop/Android builds where the whole
 * classpath is present at runtime.
 */
public class ReflectRootSupplier implements ReflectionSupplier {

	public static final List<String> ROOTED_CLASSES = new ArrayList<String>();

	@Override
	public Collection<String> getClassesFoundByName(ReflectionContext context) {
		return ROOTED_CLASSES;
	}
}
