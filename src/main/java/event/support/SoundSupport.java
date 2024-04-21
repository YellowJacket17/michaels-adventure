package event.support;

import core.GamePanel;
import asset.Sound;
import asset.AssetPool;
import utility.UtilityTool;

import java.util.concurrent.CompletableFuture;

/**
 * This class contains methods to facilitate sound playback.
 * The public methods in this class serve as primary endpoints to use when programming in-game events.
 * Note that "track" refers to background music that plays during gameplay, while "effect" refers to quick soundbites
 * that plays during an event/action.
 */
public class SoundSupport {

    // FIELD
    private final GamePanel gp;

    /**
     * Boolean indicating whether any tracks are currently fading out.
     */
    private boolean trackFadingOut = false;

    /**
     * File path of staged track to play after a fade out of a previous track is complete.
     */
    private String stagedTrackFilePath = NO_TRACK;

    /**
     * File path of track that is currently playing.
     */
    private String playingTrackFilePath = NO_TRACK;

    /**
     * Argument to be passed when no track will play.
     */
    public static final String NO_TRACK = "600a7daa-1abc-4708-86c2-f742a93f23b8";

    /**
     * Argument to be passed when no track swap will occur.
     */
    public static final String RETAIN_TRACK = "8daa0d6a-8d80-4337-b3fa-6eb7f081acdd";


    // CONSTRUCTOR
    /**
     * Constructs a SoundSupport instance.
     *
     * @param gp GamePanel instance
     */
    public SoundSupport(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Plays a new effect.
     *
     * @param newEffectFilePath file path of new effect to play from root directory.
     */
    public void playEffect(String newEffectFilePath) {

        AssetPool.getSound(newEffectFilePath).play();
    }


    /**
     * Plays a new track.
     * If the playing track is already fading out when this method is called, then the new track will not play until the
     * fade out is complete.
     * If there is already a playing track that is not fading out, then the playing track will be instantly stopped
     * before the new track is played.
     *
     * @param newTrackFilePath file path of new track to play from root directory
     */
    public void playTrack(String newTrackFilePath) {

        if (!trackFadingOut) {

            stopTrack(false);
            AssetPool.getSound(newTrackFilePath).play();
            playingTrackFilePath = newTrackFilePath;
        } else {

            stagedTrackFilePath = newTrackFilePath;
        }
    }


    /**
     * Stops the playing track.
     * The playing track can either be stopped instantly or faded out.
     * If the playing track is already fading out when this method is called, then nothing will happen.
     *
     * @param fadeOut whether the playing track will fade out (true) or not (false)
     */
    public void stopTrack(boolean fadeOut) {

        if ((!playingTrackFilePath.equals(NO_TRACK)) && (AssetPool.getSound(playingTrackFilePath).isPlaying())) {

            swapTrack(NO_TRACK, fadeOut);
        }
    }


    /**
     * Swaps the playing track out for a new one.
     * The playing track can either be swapped out instantly or faded out.
     * If the playing track is already fading out when this method is called, then the new track will not play until the
     * fade out is complete, regardless of the value of the `fadeOut` parameter.
     *
     * @param newTrackFilePath file path of new track to play from root directory (SoundSupport.NO_TRACK to swap to no
     *                         track playing)
     * @param fadeOut whether the playing track will fade out (true) or not (false)
     */
    public void swapTrack(String newTrackFilePath, boolean fadeOut) {

        stagedTrackFilePath = newTrackFilePath;

        if ((!playingTrackFilePath.equals(NO_TRACK)) && (AssetPool.getSound(playingTrackFilePath).isPlaying())) {

            if (!trackFadingOut) {

                if (fadeOut) {

                    trackFadingOut = true;                                                                              // Starting a fade process.

                    CompletableFuture.runAsync(() -> {                                                                  // Track is faded out asynchronously so that process does not halt entire program.

                        Sound oldSound = AssetPool.getSound(playingTrackFilePath);

                        while (oldSound.getGain() > 0.001f) {                                                           // Fade until the old track is too quiet to hear.

                            AssetPool.getSound(playingTrackFilePath).adjustGain(-0.0007f);                              // This argument, combined with the argument in the succeeding line, controls fade out intensity.
                            UtilityTool.wait(1);
                        }
                        oldSound.stop();

                        if (!playingTrackFilePath.equals(NO_TRACK)) {

                            if (!stagedTrackFilePath.equals(NO_TRACK)) {

                                AssetPool.getSound(stagedTrackFilePath).play();                                         // Play the new track.
                            }
                            playingTrackFilePath = stagedTrackFilePath;
                        }
                        trackFadingOut = false;                                                                         // Fade process of old track is complete.
                    });
                } else {

                    AssetPool.getSound(playingTrackFilePath).stop();

                    if (!playingTrackFilePath.equals(NO_TRACK)) {

                        if (!stagedTrackFilePath.equals(NO_TRACK)) {

                            AssetPool.getSound(stagedTrackFilePath).play();
                        }
                        playingTrackFilePath = stagedTrackFilePath;
                    }
                }
            }
        } else {

            if (!stagedTrackFilePath.equals(NO_TRACK)) {

                AssetPool.getSound(stagedTrackFilePath).play();
            }
            playingTrackFilePath = stagedTrackFilePath;
        }
    }


    // GETTER
    public String getPlayingTrackFilePath() {
        return playingTrackFilePath;
    }
}
