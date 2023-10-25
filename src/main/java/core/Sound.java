package core;

import utility.UtilityTool;
import utility.exceptions.AudioStageException;

import javax.sound.sampled.*;
import java.net.URL;

/**
 * This class manages sound (music and sound effects).
 */
public class Sound {

    /*
     * All methods withing the Sound class have a protected access modifier.
     * This is because all classes across this application should only control sound via the appropriate methods in the
     * GamePanel class.
     */

    // FIELDS
    /**
     * Clip for playing music tracks; only one music track can be played at a time.
     */
    private Clip musicClip;

    /**
     * Channels for playing sound effects; size of the array sets the maximum amount that can be played simultaneously.
     */
    private EffectChannel[] effectChannels = new EffectChannel[10];

    /**
     * Array to stores music track file paths.
     */
    private URL[] musicURL = new URL[30];

    /**
     * Array to store sound effect file paths.
     */
    private URL[] effectURL = new URL[30];

    /**
     * Variable to stores the index from `musicURL` of the music track that's currently staged to play.
     * A value of -1 means no music track is staged to play.
     */
    private int stagedMusic = -1;

    /**
     * Variable to store a staged music track's index from the `musicURL` array to be forcefully staged upon calling the
     * `setMusicTrackFile()` function in place of its passed argument.
     * A value of -1 means that no music track is staged to overwrite.
     */
    private int stagedMusicOverwrite = -1;

    /**
     * Boolean indicating whether a music track is currently in the process of fading out or not.
     */
    private boolean musicFading = false;

    /**
     * Boolean indicating whether a music track is currently playing or not.
     */
    private boolean musicPlaying = false;

    /**
     * Boolean indicating whether a music track was successfully staged to play (i.e., loaded) or not.
     */
    private boolean stagingMusicFailed = false;

    /**
     * Boolean indicating whether an additional thread is currently being run for a sound process or not.
     */
    private boolean additionalThreadRunning = false;


    // CONSTRUCTOR
    /**
     * Constructs a Sound instance.
     */
    protected Sound() {

        // Music tracks.
        String musicFilePath = "/sound/tracks/";
        musicURL[0] = getClass().getResource(musicFilePath + "testTrack3.wav");
        musicURL[1] = getClass().getResource(musicFilePath + "testTrack2.wav");
        musicURL[2] = getClass().getResource(musicFilePath + "testTrack4.wav");

        // Sound effects.
        String effectFilePath = "/sound/effects/";
        effectURL[0] = getClass().getResource(effectFilePath + "testEffect1.wav");

        // Initialize sound effect channels.
        for (int i = 0; i < effectChannels.length; i++) {
            effectChannels[i] = new EffectChannel();
        }
    }


    // METHODS
    /**
     * Stages a music track audio file to play.
     *
     * @param index index in the `musicURL` that the target sound occupies
     * @throws AudioStageException if the music track fails to be staged
     */
    protected void setMusicTrackFile(int index) {

        try {

            if (stagedMusicOverwrite != -1) {
                if (stagedMusicOverwrite != index) {
                    index = stagedMusicOverwrite;
                }
                stagedMusicOverwrite = -1;
            }

            if (stagingMusicFailed) {
                stagingMusicFailed = false;
            }

            if ((musicClip != null) && (musicClip.isOpen())) {
                musicClip.close();
            }

            try (AudioInputStream ais = AudioSystem.getAudioInputStream(musicURL[index])) {
                musicClip = AudioSystem.getClip();
                musicClip.open(ais);
                stagedMusic = index;
            }

        } catch (Exception e) {

            stagingMusicFailed = true;
            throw new AudioStageException("Could not stage music track file at index " + index + " to play");
        }
    }


    /**
     * Stages a sound effect audio file to play.
     *
     * @param index index in the `effectURL` array that the target sound occupies
     * @return sound effect channel that the sound was assigned to
     * @throws AudioStageException if the sound effect fails to be staged
     */
    protected int setSoundEffectFile(int index) {

        int availableEffectChannel = -1;

        try {

            for (int i = 0; i < effectChannels.length; i++) {
                if (effectChannels[i].isAvailable()) {
                    availableEffectChannel = i;
                    break;
                }
            }

            if (availableEffectChannel != -1) {

                effectChannels[availableEffectChannel].openChannel(effectURL[index]);
            } else {

                UtilityTool.logWarning("Attempted to open a sound effect channel, but none are available.");
            }

        } catch (Exception e) {

            throw new AudioStageException("Could not stage sound effect file at index " + index + " to play");
        }
        return availableEffectChannel;
    }


    /**
     * Plays the currently staged music track.
     */
    protected void playMusicTrack() {

        try {

            FloatControl gainControlMusic = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControlMusic.setValue(0);
            musicClip.start();
            musicPlaying = true;

        } catch (NullPointerException e) {

            UtilityTool.logWarning("Attempted to play music track, but there is currently no file staged to play.");
        }
    }


    /**
     * Plays a sound effect in an opened sound effect channel.
     *
     * @param effectChannel opened sound effect channel to play sound effect from
     */
    protected void playSoundEffect(int effectChannel) {

        if ((effectChannel >= 0) && (effectChannel < effectChannels.length)) {

            try {

                effectChannels[effectChannel].playChannel();

            } catch (NullPointerException e) {

                UtilityTool.logWarning("Attempted to play sound effect, but there is currently no file staged to play.");
            }
        } else {

            UtilityTool.logWarning("Attempted to access a sound effect channel that does not exist.");
        }
    }


    /**
     * Loops the currently staged music track indefinitely.
     */
    protected void loopMusicTrack() {

        try {

            musicClip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (NullPointerException e) {

            UtilityTool.logWarning("Attempted to loop music track, but there is no file staged to loop.");
        }
    }


    /**
     * Stops the currently staged music track from playing.
     *
     * @param fade determines whether the sound instantly stops (false) or fades out (true)
     */
    protected void stopMusicTrack(boolean fade) {

        try {

            if (fade) {

                musicFading = true;                                                                                     // Fading process has begun.
                FloatControl gainControlMusic = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);

                for (int i = 0; i < 3000; i ++) {  // 3000

                    gainControlMusic.setValue(i * -0.02f);  // -0.02f
                    UtilityTool.wait(1);                                                                                // Allows for a gradual audio fade.
                }
                musicClip.stop();
                musicFading = false;                                                                                    // Fading process is complete.
            } else {

                musicClip.stop();
            }
            musicPlaying = false;

        } catch (NullPointerException e) {

            UtilityTool.logWarning("Attempted to stop music track, but there is no file staged to stop.");
        }
    }


    /**
     * Stops all the currently staged sound effects from playing.
     */
    protected void stopSoundEffects() {

        for (EffectChannel effectChannel : effectChannels) {

            if (!effectChannel.isAvailable()) {

                effectChannel.closeChannel();
            }
        }
    }


    // SUPPORT CLASS
    /**
     * This small class defines sound effect channels, used for controlling the playback of sound effects.
     */
    protected class EffectChannel {

        private boolean available = true;
        private Clip effectClip;

        protected void openChannel(URL url) {
            if (available) {
                try (AudioInputStream ais = AudioSystem.getAudioInputStream(url)) {
                    effectClip = AudioSystem.getClip();
                    effectClip.open(ais);
                } catch (Exception e) {
                    UtilityTool.logError("Failed to open sound effect channel.");
                }
                available = false;
            }
        }

        protected void playChannel() {
            if (!available) {
                FloatControl gainControlEffect = (FloatControl) effectClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControlEffect.setValue(0);
                effectClip.start();
                effectClip.addLineListener(lineEvent -> {
                    if (lineEvent.getType() == LineEvent.Type.STOP)
                        closeChannel();
                });
            }
        }

        protected void closeChannel() {
            if (!available) {
                effectClip.close();
                available = true;
            }
        }

        protected boolean isAvailable() {
            return available;
        }
    }


    // GETTERS
    protected boolean isMusicClipNull() {
        return musicClip == null;
    }

    protected int getStagedMusic() {
        return stagedMusic;
    }

    protected int getStagedMusicOverwrite() {
        return stagedMusicOverwrite;
    }

    protected boolean isMusicFading() {
        return musicFading;
    }

    protected boolean isMusicPlaying() {
        return musicPlaying;
    }

    protected boolean isStagingMusicFailed() {
        return stagingMusicFailed;
    }

    protected boolean isAdditionalThreadRunning() {
        return additionalThreadRunning;
    }


    // SETTERS
    protected void setStagedMusicOverwrite(int stagedMusicOverwrite) {
        if (stagedMusicOverwrite >= 0) {
            this.stagedMusicOverwrite = stagedMusicOverwrite;
        }
    }

    protected void setStagingMusicFailed(boolean stagingMusicFailed) {
        this.stagingMusicFailed = stagingMusicFailed;
    }

    protected void setAdditionalThreadRunning(boolean additionalThreadRunning) {
        this.additionalThreadRunning = additionalThreadRunning;
    }
}

