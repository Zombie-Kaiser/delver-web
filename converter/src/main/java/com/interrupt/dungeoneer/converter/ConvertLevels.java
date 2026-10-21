package com.interrupt.dungeoneer.converter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.game.LocalizedString;
import com.interrupt.managers.StringManager;
import com.interrupt.utils.JsonUtil;
import com.interrupt.dungeoneer.serializers.KryoSerializer;

/**
 * Converts the shipped kryo ".bin" level files into the TeaVM friendly
 * gzip+JSON format understood by the rewritten core KryoSerializer.
 * Files are overwritten in place; re-runs skip already converted files.
 */
public class ConvertLevels {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.app = new HeadlessApp();

		boolean trace = args.length > 1 && "--trace".equals(args[1]);
		if(trace) com.esotericsoftware.minlog.Log.set(com.esotericsoftware.minlog.Log.LEVEL_TRACE);

		Path root = Paths.get(args.length > 0 ? args[0] : "assets");
		if(!Files.isDirectory(root)) {
			System.err.println("assets dir not found: " + root.toAbsolutePath());
			System.exit(1);
			return;
		}

		// entity constructors localize strings, so preload the string table
		try {
			FileHandle stringsFile = new FileHandle(root.resolve("data/strings.dat").toString());
			if(stringsFile.exists()) {
				HashMap<String, LocalizedString> strings = JsonUtil.fromJson(HashMap.class, stringsFile);
				if(strings != null) StringManager.localizedStrings = strings;
			}
		}
		catch(Exception ex) {
			System.out.println("Could not preload strings: " + ex);
		}

		List<Path> bins = new ArrayList<Path>();
		try (Stream<Path> walk = Files.walk(root)) {
			walk.filter(p -> p.getFileName().toString().endsWith(".bin")).forEach(bins::add);
		}
		bins.sort(Comparator.comparing(Path::toString));
		System.out.println("Found " + bins.size() + " .bin files");

		int converted = 0;
		int skipped = 0;
		int failed = 0;

		for(Path file : bins) {
			byte[] raw = Files.readAllBytes(file);
			if(isGzip(raw)) {
				skipped++;
				continue;
			}
			try {
				Level level = readKryo(raw);
				if(level == null) throw new RuntimeException("kryo v2/v1 read failed");

				byte[] json = KryoSerializer.toBytes(level);
				if(json == null) throw new RuntimeException("json serialization failed");

				Level reloaded = KryoSerializer.loadLevel(json);
				validate(level, reloaded);

				Files.write(file, json);
				converted++;
				System.out.println("OK   [" + lastVersion + "] " + file + "  " + stats(reloaded));
			}
			catch(Exception ex) {
				failed++;
				System.out.println("FAIL " + file + " : " + ex);
			}
		}

		System.out.println("converted=" + converted + " skipped=" + skipped + " failed=" + failed);
		if(failed > 0) System.exit(2);
	}

	private static String lastVersion = "?";

	private static Level readKryo(byte[] raw) {
		lastVersion = "v2";
		try {
			return com.interrupt.dungeoneer.serializers.v2.LevelSerializer.loadLevel(raw);
		}
		catch(Exception v2ex) {
			lastVersion = "v1";
			try {
				return com.interrupt.dungeoneer.serializers.v1.LevelSerializer.loadLevel(raw);
			}
			catch(Exception v1ex) {
				System.out.println("     v2 error: " + trim(v2ex.toString()));
				System.out.println("     v1 error: " + trim(v1ex.toString()));
				return null;
			}
		}
	}

	private static void validate(Level original, Level reloaded) {
		if(reloaded.width != original.width || reloaded.height != original.height)
			throw new RuntimeException("size mismatch: " + reloaded.width + "x" + reloaded.height + " vs " + original.width + "x" + original.height);
		if(reloaded.tiles == null || reloaded.tiles.length != original.tiles.length)
			throw new RuntimeException("tile count mismatch: " + (reloaded.tiles == null ? -1 : reloaded.tiles.length) + " vs " + original.tiles.length);
		if(countTiles(original.tiles) != countTiles(reloaded.tiles))
			throw new RuntimeException("non-null tile mismatch");
		if(size(reloaded.entities) != size(original.entities))
			throw new RuntimeException("entities mismatch: " + size(reloaded.entities) + " vs " + size(original.entities));
		if(size(reloaded.non_collidable_entities) != size(original.non_collidable_entities))
			throw new RuntimeException("non_collidable_entities mismatch");
		if(size(reloaded.static_entities) != size(original.static_entities))
			throw new RuntimeException("static_entities mismatch");
	}

	private static int countTiles(com.interrupt.dungeoneer.tiles.Tile[] tiles) {
		int n = 0;
		for(com.interrupt.dungeoneer.tiles.Tile t : tiles)
			if(t != null) n++;
		return n;
	}

	private static int size(com.badlogic.gdx.utils.Array<?> array) {
		return array == null ? -1 : array.size;
	}

	private static String stats(Level l) {
		return l.width + "x" + l.height + " tiles=" + countTiles(l.tiles) + " ent=" + size(l.entities) + "/" + size(l.non_collidable_entities) + "/" + size(l.static_entities);
	}

	private static boolean isGzip(byte[] raw) {
		return raw.length > 2 && raw[0] == 0x1f && raw[1] == (byte)0x8b;
	}

	private static String trim(String s) {
		return s.length() > 160 ? s.substring(0, 160) : s;
	}
}
