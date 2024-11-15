package com.github.devsnaith.unwholesome.io;

import java.io.File;
import java.util.HashMap;

import com.github.devsnaith.unwholesome.core.QConsole;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.SampleManager;
import net.beadsproject.beads.ugens.SamplePlayer;

public class QAudioManger {
	private AudioContext ac;
	private HashMap<String, SamplePlayer> audioList = new HashMap<>();

	public void addAudio(String AudioName, String AudioPath) {

		if (ac == null) {
			ac = AudioContext.getDefaultContext();
			new Thread(()->ac.start()).start();
		}

		try {
			String audioFile = String.format("%s/%s", new File("").getAbsolutePath(), AudioPath);
			SamplePlayer player = new SamplePlayer(SampleManager.sample(audioFile));
			player.setKillOnEnd(false);
			player.pause(true);
			audioList.put(AudioName, player);
			ac.out.addInput(player);
			QConsole.print(QConsole.Status.INFO,
					String.format("Audio in [%s] '%s' has been loaded", new Object[] { AudioName, AudioPath }));
		} catch (NullPointerException ex) {
			QConsole.print(QConsole.Status.ERROR, "Couldn't find audio at '" + AudioPath + "'");
		}
	}

	private SamplePlayer getClip(String AudioName) {
		SamplePlayer audio = this.audioList.get(AudioName);
		if (audio != null && audio.getSample() != null) {
			audio.pause(false);
			return audio;
		}
		QConsole.print(QConsole.Status.ERROR, "Can not found audio with name '" + AudioName + "'");
		return null;
	}

	public synchronized void playAudio(String AudioName) {
		SamplePlayer audio = getClip(AudioName);
		if(audio == null)
			return;
		audio.pause(false);
		audio.start(0);
	}

	public synchronized void playAudioWhenReady(String AudioName) {
		SamplePlayer audio = getClip(AudioName);
		if(audio == null)
			return;
		if (audio.isPaused() || audio.getPosition() > audio.getSample().getLength()) {
			audio.start(0);
		}
	}

	public synchronized void stopAudio(String AudioName) {
		SamplePlayer audio = getClip(AudioName);
		if(audio == null)
			return;
		audio.pause(true);
		audio.setToEnd();
	}
}