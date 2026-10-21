package com.interrupt.dungeoneer.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.game.OverworldLevel;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.interrupt.dungeoneer.tiles.Tile;

/**
 * TeaVM compatible replacement for the Kryo based level serialization.
 * Levels are stored as gzip compressed JSON, keeping the same API and
 * the same ".bin" file names as before. Convert existing kryo ".bin"
 * files with the converter module before shipping them to the web build.
 */
public class KryoSerializer {

	private static Json json = createJson();

	private static Json createJson() {
		Json j = new Json() {
			@Override
			protected boolean ignoreUnknownField(Class type, String fieldName) {
				return true;
			}
		};
		j.setOutputType(OutputType.json);
		j.setUsePrototypes(false);
		j.setSerializer(Tile.class, new TileJsonSerializer());
		j.setSerializer(BoundingBox.class, new Json.Serializer<BoundingBox>() {
			@Override
			public void write(Json json, BoundingBox object, Class knownType) {
				json.writeObjectStart();
				json.writeObjectEnd();
			}

			@Override
			public BoundingBox read(Json json, JsonValue jsonData, Class type) {
				return new BoundingBox();
			}
		});
		return j;
	}

	public static synchronized Level loadLevel(FileHandle file) {
		return parseLevel(file.readBytes(), Level.class, true);
	}

	public static synchronized Level loadLevel(byte[] bytes) {
		return parseLevel(bytes, Level.class, true);
	}

	public static synchronized OverworldLevel loadOverworldLevel(FileHandle file) {
		try {
			return parseLevel(file.readBytes(), OverworldLevel.class, true);
		}
		catch(Exception ex) {
			safeLog("Delver", ex.toString());
			return null;
		}
	}

	public static synchronized void saveLevel(FileHandle file, Level level) {
		try {
			file.writeBytes(toJsonBytes(level), false);
		}
		catch(Exception ex) {
			safeLog("Delver", ex.toString());
		}
	}

	public static synchronized Object copyObject(Object object) {
		if(object == null) return null;
		try {
			return ObjectCloner.copy(object);
		}
		catch(Exception ex) {
			safeLog("Delver", ex.toString());
			return null;
		}
	}

	public static synchronized byte[] toBytes(Object object) {
		if(object == null) return null;
		try {
			return toJsonBytes(object);
		}
		catch(Exception ex) {
			safeLog("Delver", ex.toString(), ex);
			return null;
		}
	}

	/** Loads a level without running postLoad, used by the overworld generator. */
	public static synchronized Level loadLevelNoPostLoad(FileHandle file) {
		return parseLevel(file.readBytes(), Level.class, false);
	}

	private static <T extends Level> T parseLevel(byte[] bytes, Class<T> type, boolean postLoad) {
		String text = gunzip(bytes);
		T level = json.fromJson(type, text);
		if(postLoad) level.postLoad();
		return level;
	}

	private static byte[] toJsonBytes(Object object) throws IOException {
		String text = json.toJson(object);
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(bytesOut);
		try {
			gzip.write(text.getBytes(StandardCharsets.UTF_8));
		}
		finally {
			gzip.close();
		}
		return bytesOut.toByteArray();
	}

	private static String gunzip(byte[] bytes) {
		try {
			if(bytes != null && bytes.length > 2 && bytes[0] == 0x1f && bytes[1] == (byte)0x8b) {
				ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(64, bytes.length * 4));
				InputStream in = new GZIPInputStream(new ByteArrayInputStream(bytes));
				try {
					byte[] buffer = new byte[8192];
					int read = in.read(buffer);
					while(read != -1) {
						out.write(buffer, 0, read);
						read = in.read(buffer);
					}
				}
				finally {
					in.close();
				}
				return new String(out.toByteArray(), StandardCharsets.UTF_8);
			}
			return new String(bytes, StandardCharsets.UTF_8);
		}
		catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static String dbgCodeSource(Class c) {
		try {
			return c.getProtectionDomain().getCodeSource().getLocation().toString();
		}
		catch(Throwable t) {
			return "?";
		}
	}

	private static void safeLog(String tag, String message) {
		if(Gdx.app != null) Gdx.app.log(tag, message);
		else System.out.println("[" + tag + "] " + message);
	}

	private static void safeLog(String tag, String message, Throwable throwable) {
		if(Gdx.app != null) Gdx.app.error(tag, message, throwable);
		else {
			System.out.println("[" + tag + "] " + message);
			throwable.printStackTrace();
		}
	}
}
