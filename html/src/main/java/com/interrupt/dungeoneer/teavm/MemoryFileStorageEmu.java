package com.interrupt.dungeoneer.teavm;

import com.github.xpenatan.gdx.backends.teavm.filesystem.MemoryFileStorage;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

/**
 * Game code builds asset paths like "audio/" + "/ui/ui_x.mp3" = "audio//ui/ui_x.mp3".
 * On desktop the OS collapses duplicate separators; TeaVM's MemoryFileStorage keys
 * its file map verbatim, so lookups miss. Merges a fixed fixPath that collapses
 * duplicate slashes into the backend's MemoryFileStorage at build time.
 */
@Emulate(value = MemoryFileStorage.class, updateCode = true)
public class MemoryFileStorageEmu {

	@Emulate
	protected String fixPath(String path) {
		path = path.trim();
		if (path.startsWith("./")) {
			path = path.replace("./", "");
		}
		if (path.startsWith(".")) {
			path = path.replaceFirst(".", "");
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		while (path.contains("//")) {
			path = path.replace("//", "/");
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		return path;
	}
}
