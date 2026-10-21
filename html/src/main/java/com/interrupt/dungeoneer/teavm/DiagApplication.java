package com.interrupt.dungeoneer.teavm;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.interrupt.managers.EntityManager;
import com.interrupt.managers.StringManager;
import com.interrupt.utils.JsonUtil;

import java.util.HashMap;

/**
 * Temporary diagnostic listener. Runs inside the TeaVM browser environment
 * after asset preload and isolates which Json construct fails under TeaVM.
 * Swap TeaVMBuilder.mainApplicationClass back to GameApplication when done.
 */
public class DiagApplication implements ApplicationListener {

	public static class Bean {
		public String name;
	}

	public static class HashMapBean {
		public HashMap<String, String> map = new HashMap<String, String>();
	}

	public static class OrderedMapBean {
		public OrderedMap<String, String> map = new OrderedMap<String, String>();
	}

	@Override
	public void create() {
		diag("START");

		try {
			Bean b = JsonUtil.fromJson(Bean.class, "{\"name\":\"x\"}");
			diag("A plain-bean: " + (b != null && "x".equals(b.name) ? "OK" : "FAIL"));
		} catch (Throwable t) { safe("A", t); }

		try {
			HashMapBean b = JsonUtil.fromJson(HashMapBean.class, "{\"map\":{\"a\":\"b\"}}");
			diag("B hashmap-field: " + (b != null && "b".equals(b.map.get("a")) ? "OK" : "FAIL"));
		} catch (Throwable t) { safe("B", t); }

		try {
			OrderedMapBean b = JsonUtil.fromJson(OrderedMapBean.class, "{\"map\":{\"a\":\"b\"}}");
			diag("C orderedmap-field: " + (b != null && "b".equals(b.map.get("a")) ? "OK" : "FAIL"));
		} catch (Throwable t) { safe("C", t); }

		try {
			Object h = HashMap.class.newInstance();
			if (h instanceof HashMap) {
				((HashMap<Object, Object>) h).put("k", "v");
				diag("D hashmap-newInstance: OK get=" + ((HashMap<Object, Object>) h).get("k"));
			}
			else {
				diag("D hashmap-newInstance: NOT-INSTANCE null=" + (h == null));
			}
		} catch (Throwable t) { safe("D", t); }

		try {
			Object h = ClassReflection.newInstance(HashMap.class);
			if (h instanceof HashMap) {
				((HashMap<Object, Object>) h).put("k", "v");
				diag("D2 gdx-newInstance: OK get=" + ((HashMap<Object, Object>) h).get("k"));
			}
			else {
				diag("D2 gdx-newInstance: NOT-INSTANCE null=" + (h == null));
			}
		} catch (Throwable t) { safe("D2", t); }

		String json = null;
		try {
			json = Gdx.files.internal("data/entities.dat").readString("UTF-8");
			diag("E entities.dat read: " + (json == null ? "null" : json.length() + " chars"));
		} catch (Throwable t) { safe("E-read", t); }
		if (json == null || json.length() == 0) {
			try {
				json = Gdx.files.internal("./data/entities.dat").readString("UTF-8");
				diag("E entities.dat read(./): " + (json == null ? "null" : json.length() + " chars"));
			} catch (Throwable t) { safe("E-read2", t); }
		}

		if (json != null && json.length() > 0) {
			try {
				StringManager.init();
				EntityManager em = JsonUtil.fromJson(EntityManager.class, json);
				diag("E entities.dat parse: " + (em != null ? "OK" : "NULL"));
			} catch (Throwable t) { safe("E", t); }

			try {
				com.badlogic.gdx.utils.JsonValue root = new com.badlogic.gdx.utils.JsonReader().parse(json);
				com.badlogic.gdx.utils.JsonValue entities = root.get("entities");
				int checked = 0;
				int failed = 0;
				if (entities != null) {
					for (com.badlogic.gdx.utils.JsonValue category : entities) {
						for (com.badlogic.gdx.utils.JsonValue entityDef : category) {
							com.badlogic.gdx.utils.JsonValue cls = entityDef.get("class");
							if (cls != null && cls.isString()) {
								String cn = cls.asString();
								checked++;
								try {
									ClassReflection.forName(cn);
								} catch (Throwable t) {
									failed++;
									diag("F class-fail: " + cn);
								}
							}
						}
					}
				}
				diag("F checked=" + checked + " failed=" + failed);
			} catch (Throwable t) { safe("F", t); }
		}

		diag("DONE");
	}

	private static void diag(String msg) {
		Gdx.app.log("DIAG", msg);
	}

	private static void safe(String tag, Throwable t) {
		try {
			diag(tag + " EX: " + t.getClass().getName());
		} catch (Throwable ignored) { }
		try {
			Object s = t.toString();
			if (s instanceof String) diag(tag + " TOSTRING: " + ((String) s).substring(0, Math.min(500, ((String) s).length())));
		} catch (Throwable ignored) { }
		try {
			Object m = t.getMessage();
			if (m instanceof String) {
				String s = (String) m;
				diag(tag + " MSG: " + s.substring(0, Math.min(500, s.length())));
			}
		} catch (Throwable ignored) { }
		Throwable cur = t;
		for (int i = 0; i < 6 && cur != null; i++) {
			Object c;
			try { c = cur.getCause(); } catch (Throwable x) { break; }
			if (!(c instanceof Throwable)) break;
			cur = (Throwable) c;
			try { diag(tag + " CAUSE" + i + ": " + cur.getClass().getName()); } catch (Throwable x) { break; }
			try {
				Object m = cur.getMessage();
				if (m instanceof String) {
					String s = (String) m;
					diag(tag + " CAUSE" + i + " MSG: " + s.substring(0, Math.min(500, s.length())));
				}
			} catch (Throwable x) { }
		}
	}

	@Override
	public void resize(int width, int height) { }

	@Override
	public void render() { }

	@Override
	public void pause() { }

	@Override
	public void resume() { }

	@Override
	public void dispose() { }
}
