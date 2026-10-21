package com.interrupt.dungeoneer.converter;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;

/** Minimal headless Gdx.app replacement so engine logging works in the converter JVM. */
public class HeadlessApp implements Application {

	@Override
	public ApplicationListener getApplicationListener() {
		return null;
	}

	@Override
	public Graphics getGraphics() {
		return null;
	}

	@Override
	public Audio getAudio() {
		return null;
	}

	@Override
	public Input getInput() {
		return null;
	}

	@Override
	public Files getFiles() {
		return null;
	}

	@Override
	public Net getNet() {
		return null;
	}

	@Override
	public void log(String tag, String message) {
		System.out.println("[" + tag + "] " + message);
	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		System.out.println("[" + tag + "] " + message);
		exception.printStackTrace();
	}

	@Override
	public void error(String tag, String message) {
		System.out.println("[ERROR] " + tag + ": " + message);
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		System.out.println("[ERROR] " + tag + ": " + message);
		exception.printStackTrace();
	}

	@Override
	public void debug(String tag, String message) {
		System.out.println("[DEBUG] " + tag + ": " + message);
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		System.out.println("[DEBUG] " + tag + ": " + message);
		exception.printStackTrace();
	}

	@Override
	public void setLogLevel(int logLevel) {
	}

	@Override
	public int getLogLevel() {
		return LOG_INFO;
	}

	@Override
	public void setApplicationLogger(ApplicationLogger logger) {
	}

	@Override
	public ApplicationLogger getApplicationLogger() {
		return null;
	}

	@Override
	public Application.ApplicationType getType() {
		return Application.ApplicationType.Desktop;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public long getJavaHeap() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	@Override
	public long getNativeHeap() {
		return getJavaHeap();
	}

	@Override
	public Preferences getPreferences(String name) {
		return null;
	}

	@Override
	public Clipboard getClipboard() {
		return null;
	}

	@Override
	public void postRunnable(Runnable runnable) {
		runnable.run();
	}

	@Override
	public void exit() {
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
	}
}
