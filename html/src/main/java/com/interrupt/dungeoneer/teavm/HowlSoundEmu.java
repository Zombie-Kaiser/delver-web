package com.interrupt.dungeoneer.teavm;

import com.github.xpenatan.gdx.backends.teavm.webaudio.howler.Howl;
import com.github.xpenatan.gdx.backends.teavm.webaudio.howler.HowlSound;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

/**
 * HowlSound.dispose() nulls its internal Howl. Game code keeps PlayingSound
 * references across Audio.disposeAudio() calls (ambient tile sounds are not
 * cleared there), so stop()/setVolume() can hit a disposed sound every tick,
 * throwing a raw JS TypeError that aborts Game.tick() mid-way and freezes the
 * HUD/backpack UI. Null-guards every method; semantics otherwise identical.
 */
@Emulate(value = HowlSound.class, updateCode = true)
public class HowlSoundEmu {

	private Howl howl;

	@Emulate
	public long play() {
		if (howl == null) return 0;
		return howl.play();
	}

	@Emulate
	public long play(float volume) {
		if (howl == null) return 0;
		int id = howl.play();
		howl.setVolume(volume, id);
		return id;
	}

	@Emulate
	public long play(float volume, float pitch, float pan) {
		if (howl == null) return 0;
		int id = howl.play();
		howl.setVolume(volume, id);
		howl.setRate(pitch, id);
		howl.setStereo(pan, id);
		return id;
	}

	@Emulate
	public long loop() {
		if (howl == null) return 0;
		int id = howl.play();
		howl.setLoop(true, id);
		return id;
	}

	@Emulate
	public long loop(float volume) {
		if (howl == null) return 0;
		int id = howl.play();
		howl.setLoop(true, id);
		howl.setVolume(volume, id);
		return id;
	}

	@Emulate
	public long loop(float volume, float pitch, float pan) {
		if (howl == null) return 0;
		int id = howl.play();
		howl.setLoop(true, id);
		howl.setVolume(volume, id);
		howl.setStereo(volume, id);
		return id;
	}

	@Emulate
	public void stop() {
		if (howl == null) return;
		howl.stop();
	}

	@Emulate
	public void stop(long soundId) {
		if (howl == null) return;
		howl.stop((int) soundId);
	}

	@Emulate
	public void pause() {
		if (howl == null) return;
		howl.pause();
	}

	@Emulate
	public void pause(long soundId) {
		if (howl == null) return;
		howl.pause((int) soundId);
	}

	@Emulate
	public void resume() {
		if (howl == null) return;
		howl.play();
	}

	@Emulate
	public void resume(long soundId) {
		if (howl == null) return;
		howl.play((int) soundId);
	}

	@Emulate
	public void setLooping(long soundId, boolean looping) {
		if (howl == null) return;
		howl.setLoop(looping, (int) soundId);
	}

	@Emulate
	public void setPitch(long soundId, float pitch) {
		if (howl == null) return;
		howl.setRate(pitch, (int) soundId);
	}

	@Emulate
	public void setVolume(long soundId, float volume) {
		if (howl == null) return;
		howl.setVolume(volume, (int) soundId);
	}

	@Emulate
	public void setPan(long soundId, float pan, float volume) {
		if (howl == null) return;
		howl.setStereo(pan, (int) soundId);
		howl.setVolume(volume, (int) soundId);
	}

	@Emulate
	public void dispose() {
		if (howl == null) return;
		howl.stop();
		howl = null;
	}
}
