package com.interrupt.dungeoneer.serializers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Random;

/**
 * Reflection based deep object cloner, replacing the kryo based
 * KryoSerializer.copyObject used for spawning entities and spells.
 */
public class ObjectCloner {

	public static Object copy(Object object) throws Exception {
		return copy(object, new IdentityHashMap<Object, Object>());
	}

	private static Object copy(Object obj, IdentityHashMap<Object, Object> clones) throws Exception {
		if(obj == null) return null;

		Class<?> type = obj.getClass();

		if(isImmutable(type)) return obj;

		Object clone = clones.get(obj);
		if(clone != null) return clone;

		if(type.isArray()) {
			int length = Array.getLength(obj);
			Class<?> component = type.getComponentType();
			clone = Array.newInstance(component, length);
			clones.put(obj, clone);
			if(component.isPrimitive()) {
				System.arraycopy(obj, 0, clone, 0, length);
			}
			else {
				for(int i = 0; i < length; i++) {
					Array.set(clone, i, copy(Array.get(obj, i), clones));
				}
			}
			return clone;
		}

		if(obj instanceof com.badlogic.gdx.utils.Array) {
			com.badlogic.gdx.utils.Array<?> source = (com.badlogic.gdx.utils.Array<?>)obj;
			com.badlogic.gdx.utils.Array<Object> target = new com.badlogic.gdx.utils.Array<Object>(source.size);
			clones.put(obj, target);
			for(int i = 0; i < source.size; i++) {
				target.add(copy(source.get(i), clones));
			}
			return target;
		}

		if(obj instanceof ArrayList) {
			ArrayList<?> source = (ArrayList<?>)obj;
			ArrayList<Object> target = new ArrayList<Object>(source.size());
			clones.put(obj, target);
			for(int i = 0; i < source.size(); i++) {
				target.add(copy(source.get(i), clones));
			}
			return target;
		}

		if(obj instanceof HashMap) {
			HashMap<?, ?> source = (HashMap<?, ?>)obj;
			HashMap<Object, Object> target = new HashMap<Object, Object>();
			clones.put(obj, target);
			for(java.util.Map.Entry<?, ?> entry : source.entrySet()) {
				target.put(copy(entry.getKey(), clones), copy(entry.getValue(), clones));
			}
			return target;
		}

		if(obj instanceof HashSet) {
			HashSet<?> source = (HashSet<?>)obj;
			HashSet<Object> target = new HashSet<Object>();
			clones.put(obj, target);
			for(Object item : source) {
				target.add(copy(item, clones));
			}
			return target;
		}

		if(obj instanceof Random) {
			return new Random();
		}

		clone = type.getDeclaredConstructor().newInstance();
		clones.put(obj, clone);

		Class<?> current = type;
		while(current != null && current != Object.class) {
			for(Field field : current.getDeclaredFields()) {
				int modifiers = field.getModifiers();
				if(Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) continue;
				field.setAccessible(true);
				Object value = field.get(obj);
				if(value == null) {
					continue;
				}
				if(field.getType().isPrimitive()) {
					field.set(clone, value);
				}
				else {
					try {
						field.set(clone, copy(value, clones));
					}
					catch(IllegalAccessException ex) {
						// final fields that cannot be reassigned stay shared
					}
				}
			}
			current = current.getSuperclass();
		}

		return clone;
	}

	private static boolean isImmutable(Class<?> type) {
		if(type == String.class || type == Integer.class || type == Long.class || type == Float.class
				|| type == Double.class || type == Boolean.class || type == Byte.class || type == Short.class
				|| type == Character.class || type == Class.class) {
			return true;
		}
		return type.isEnum();
	}
}
