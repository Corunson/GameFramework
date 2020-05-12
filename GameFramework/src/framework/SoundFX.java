/**
 * 
 */
package framework;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author Krish Pillai
 *
 */
public enum SoundFX implements LineListener {

	//WIN("audio/win.wav"), LOSE("audio/lose.wav"), 
	// STARTUP("audio/start.wav"), 
	
	HIT("audio/hit.wav"),
	BOUNCE("audio/bounce.wav"),
	UKULELE("audio/ukulele.wav"),
	YAY("audio/yay.wav"),
	MEDIUM("audio/medium.wav"),
	HELL("audio/hell.wav"),
	INVINCIBLE("audio/invincible.wav"),
	SHOOT("audio/laser.wav");
	//BOUNCE("audio/bounce.wav");

	// Nested class for specifying volume
	public static enum Volume {
		MUTE, LOW, MEDIUM, HIGH
	}
	
	private boolean debug = false;

	public static Volume volume = Volume.LOW;

	// Each sound effect has its own clip, loaded with its own sound file.
	private Clip clip;

	// Constructor to construct each element of the enum with its own sound
	// file.
	SoundFX(String soundFileName) {
		try {
			// Use URL (instead of File) to read from disk and JAR.
			URL url = this.getClass().getClassLoader().getResource(soundFileName);
			// Set up an audio input stream piped from the sound file.
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			AudioFormat format = audioInputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);

			// Get a clip resource.
			if (!AudioSystem.isLineSupported(info))
				new LineUnavailableException("Line is not unspported!");

			clip = (Clip) AudioSystem.getLine(info);
			clip.addLineListener(this);

			// Open audio clip and load samples from the audio input stream.
			clip.open(audioInputStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	// Play or Re-play the sound effect from the beginning, by rewinding.
	public void play() {
		
		Runnable playNote = new Runnable() {

			@Override
			public void run() {
				if (clip.isRunning())
					clip.stop();
				clip.setFramePosition(0); // rewind to the beginning
				clip.start(); // Start playing
			}
		};

		Thread playThread = new Thread(playNote);
		playThread.start();

	}

	// Optional static method to pre-load all the sound files.
	public static void init() {
		values(); // calls the constructor for all the elements
	}

	// Called by the media player
	@Override
	public void update(LineEvent event) {

		LineEvent.Type type = event.getType();
		if (type == LineEvent.Type.START) {
			if (debug)
				System.out.println(this + ": Started...");
		}
		if (type == LineEvent.Type.STOP) {
			if (debug)
				System.out.println(this + ": Stopped!");
		}
	}
	
	public void stop() {
		if (clip.isRunning())
			clip.stop();
	}
	
	public static void waitForPlayback(){

	}

}
