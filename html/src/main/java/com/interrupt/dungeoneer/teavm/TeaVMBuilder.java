package com.interrupt.dungeoneer.teavm;

import com.badlogic.gdx.Files.FileType;
import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier;
import org.teavm.tooling.TeaVMTool;

import java.io.File;

/**
 * Build main for the TeaVM module. Compiles core plus the teavm backend
 * into html/webapp/teavm/app.js and copies the game assets next to it.
 */
public class TeaVMBuilder {

	public static void main(String[] args) {
		TeaBuildConfiguration config = new TeaBuildConfiguration();

		config.webappPath = "html";
		config.mainApplicationClass = "com.interrupt.dungeoneer.GameApplication";
		config.htmlTitle = "Delver";
		config.htmlWidth = 960;
		config.htmlHeight = 640;

		config.assetsPath.add(AssetFileHandle.createCopyHandle("assets", FileType.Internal, ""));
		config.useDefaultHtmlIndex = false;

		TeaReflectionSupplier.addReflectionClass("com.interrupt");
		TeaReflectionSupplier.addReflectionClass("com.noise");
		TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.utils");
		TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.Camera");
		TeaReflectionSupplier.addReflectionClass("com.badlogic.gdx.graphics.PerspectiveCamera");

		collectRootedClasses();
		System.out.println("Rooted " + ReflectRootSupplier.ROOTED_CLASSES.size() + " game classes for reflection lookup");

		TeaVMTool tool = TeaBuilder.config(config);
		tool.setDebugInformationGenerated(true);
		tool.setSourceMapsFileGenerated(true);
		TeaBuilder.build(tool);

		patchAssetManifest();
	}

	/**
	 * The generated assets.txt does not list itself, so the runtime asset DB
	 * never contains assets.txt and Game.getPackagedFiles() fails with
	 * "File not found: assets.txt", breaking directory listings used during
	 * level generation. Prepend a self-referencing entry.
	 */
	private static void patchAssetManifest() {
		try {
			File manifest = new File("html/webapp/assets/assets.txt");
			if (!manifest.isFile()) {
				System.out.println("Manifest patch skipped: no assets.txt");
				return;
			}
			String content = new String(java.nio.file.Files.readAllBytes(manifest.toPath()), java.nio.charset.StandardCharsets.UTF_8);
			if (!content.contains("/assets.txt:")) {
				String selfEntry = "i:b:/assets.txt:0:1\n";
				java.nio.file.Files.write(manifest.toPath(), (selfEntry + content).getBytes(java.nio.charset.StandardCharsets.UTF_8));
				System.out.println("Manifest patched with assets.txt self-entry");
			}
		}
		catch (Exception ex) {
			System.out.println("Manifest patch failed: " + ex);
		}
	}

	/**
	 * Scans classpath directories for game classes and roots them so
	 * Class.forName resolves data-only referenced entities, items and triggers.
	 * Build-only classes (this package) are excluded.
	 */
	private static void collectRootedClasses() {
		String classpath = System.getProperty("java.class.path");
		for (String entry : classpath.split(File.pathSeparator)) {
			File file = new File(entry);
			if (file.isDirectory()) {
				collectDir(file, file);
			}
			else if (entry.endsWith(".jar")) {
				collectJar(file);
			}
		}
	}

	private static void addRooted(String name) {
		if (name.startsWith("com.interrupt.dungeoneer.teavm.")
				|| name.startsWith("com.interrupt.dungeoneer.scripting.")) {
			return;
		}
		if (name.startsWith("com.interrupt.") || name.startsWith("com.noise.")) {
			ReflectRootSupplier.ROOTED_CLASSES.add(name);
		}
	}

	private static void collectJar(File jar) {
		try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(jar)) {
			java.util.Enumeration<? extends java.util.zip.ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				String path = entries.nextElement().getName();
				if (!path.endsWith(".class")) continue;
				addRooted(path.substring(0, path.length() - 6).replace('/', '.'));
			}
		}
		catch (Exception ignored) {
		}
	}

	private static void collectDir(File root, File dir) {
		File[] files = dir.listFiles();
		if (files == null) return;
		for (File file : files) {
			if (file.isDirectory()) {
				collectDir(root, file);
			}
			else if (file.getName().endsWith(".class")) {
				String rel = root.toURI().relativize(file.toURI()).getPath();
				addRooted(rel.substring(0, rel.length() - 6).replace('/', '.'));
			}
		}
	}
}
