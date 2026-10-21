package com.interrupt.dungeoneer.teavm;

import com.badlogic.gdx.audio.Music;
import com.github.xpenatan.gdx.backends.teavm.webaudio.howler.Howl;
import com.github.xpenatan.gdx.backends.teavm.webaudio.howler.HowlMusic;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

/**
 * HowlMusic's Howl can be null when Howl.create() fails to decode the track;
 * every subsequent isPlaying()/stop()/setVolume() call then throws a raw JS
 * TypeError. Null-guards every method; semantics otherwise identical.
 */
@Emulate(value = HowlMusic.class, updateCode = true)
public class HowlMusicEmu {

	private Howl howl;

	@Emulate
	public void play() {
		if (isPlaying()) return;
		if (howl == null) return;
		howl.play();
	}

	@Emulate
	public void pause() {
		if (howl == null) return;
		howl.pause();
	}

	@Emulate
	public void stop() {
		if (howl == null) return;
		howl.stop();
	}

	@Emulate
	public boolean isPlaying() {
		if (howl == null) return false;
		return howl.isPlaying();
	}

	@Emulate
	public void setLooping(boolean looping) {
		if (howl == null) return;
		howl.setLoop(looping);
	}

	@Emulate
	public boolean isLooping() {
		if (howl == null) return false;
		return howl.getLoop();
	}

	@Emulate
	public void setVolume(float volume) {
		if (howl == null) return;
		howl.setVolume(volume);
	}

	@Emulate
	public float getVolume() {
		if (howl == null) return 1f;
		return howl.getVolume();
	}

	@Emulate
	public void setPan(float pan, float volume) {
		if (howl == null) return;
		howl.setStereo(pan);
		howl.setVolume(volume);
	}

	@Emulate
	public void setPosition(float position) {
		if (howl == null) return;
		howl.setSeek(position);
	}

	@Emulate
	public float getPosition() {
		if (howl == null) return 0f;
		return howl.getSeek();
	}

	@Emulate
	public void dispose() {
		if (howl == null) return;
		howl.stop();
		howl.unload();
		howl = null;
	}

	@Emulate
	public void setOnCompletionListener(Music.OnCompletionListener listener) {
	}
}
