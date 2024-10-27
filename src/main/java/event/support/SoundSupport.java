package event.support;

import asset.Sound;
import asset.enumeration.SoundType;
import core.GamePanel;
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

    // FIELDS
    private final GamePanel gp;

    /**
     * Boolean indicating whether any tracks are currently fading out.
     */
    private boolean trackFadingOut = false;

    /**
     * Name/title of staged track to play after a fade out of a previous track is complete.
     */
    private String stagedTrackName = Sound.NO_TRACK;

    /**
     * Name/title of track that is currently playing.
     */
    private String playingTrackName = Sound.NO_TRACK;


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
     * Updates the state of the playing track by one frame, if applicable.
     */
    public void update(double dt) {

        // Update playing track.
        if (!playingTrackName.equals(Sound.NO_TRACK)) {

            AssetPool.getSound(playingTrackName).update();
        }
    }


    /**
     * Plays a new effect.
     * If the effect passed as argument is already playing, it will be stopped and restarted from the beginning.
     *
     * @param resourceName name/title of loaded effect
     */
    public void playEffect(String resourceName) {

        Sound sound = AssetPool.getSound(resourceName);

        if (sound.getType() == SoundType.EFFECT) {

            if (sound.isPlaying()) {

                sound.stop();
            }
            sound.play();
        } else {

            throw new IllegalArgumentException("Attempted to play sound named '" + resourceName + "' as an effect");
        }
    }


    /**
     * Plays a new track.
     * If the playing track is already fading out when this method is called, then the new track will not play until the
     * fade out is complete.
     * If there is already a playing track that is not fading out, then the playing track will be instantly stopped
     * before the new track is played.
     * If the track passed as argument is already playing, it will be stopped and restarted from the beginning.
     *
     * @param resourceName name/title of loaded track
     */
    public void playTrack(String resourceName) {

        Sound sound = AssetPool.getSound(resourceName);

        if (sound.getType() == SoundType.TRACK) {

            if (!trackFadingOut) {

                stopTrack(false);
                AssetPool.getSound(resourceName).play();
                playingTrackName = resourceName;
            } else {

                stagedTrackName = resourceName;
            }
        } else {

            throw new IllegalArgumentException("Attempted to play sound named '" + resourceName + "' as a track");
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

        if ((!playingTrackName.equals(Sound.NO_TRACK)) && (AssetPool.getSound(playingTrackName).isPlaying())) {

            swapTrack(Sound.NO_TRACK, fadeOut);
        }
    }


    /**
     * Swaps the playing track out for a new one.
     * The playing track can either be swapped out instantly or faded out.
     * If the playing track is already fading out when this method is called, then the new track will not play until the
     * fade out is complete, regardless of the value of the `fadeOut` parameter.
     *
     * @param resourceName name/title of new track to play from root directory (Sound.NO_TRACK to swap to no track
     *                         playing)
     * @param fadeOut whether the playing track will fade out (true) or not (false)
     */
    public void swapTrack(String resourceName, boolean fadeOut) {

        stagedTrackName = resourceName;

        if ((!playingTrackName.equals(stagedTrackName))
                && (!playingTrackName.equals(Sound.NO_TRACK))
                && (AssetPool.getSound(playingTrackName).isPlaying())) {

            if (!trackFadingOut) {

                if (fadeOut) {

                    trackFadingOut = true;                                                                              // Starting a fade process.

                    CompletableFuture.runAsync(() -> {                                                                  // Track is faded out asynchronously so that process does not halt entire program.

                        Sound oldSound = AssetPool.getSound(playingTrackName);

                        while (oldSound.getGain() > 0.001f) {                                                           // Fade until the old track is too quiet to hear.

                            AssetPool.getSound(playingTrackName).adjustGain(-0.0007f);                                  // This argument, combined with the argument in the succeeding line, controls fade out intensity.
                            UtilityTool.wait(1);
                        }
                        oldSound.stop();

                        if (!playingTrackName.equals(Sound.NO_TRACK)) {

                            if (!stagedTrackName.equals(Sound.NO_TRACK)) {

                                AssetPool.getSound(stagedTrackName).play();                                             // Play the new track.
                            }
                            playingTrackName = stagedTrackName;
                        }
                        trackFadingOut = false;                                                                         // Fade process of old track is complete.
                    });
                } else {

                    AssetPool.getSound(playingTrackName).stop();

                    if (!playingTrackName.equals(Sound.NO_TRACK)) {

                        if (!stagedTrackName.equals(Sound.NO_TRACK)) {

                            AssetPool.getSound(stagedTrackName).play();
                        }
                        playingTrackName = stagedTrackName;
                    }
                }
            }
        } else {

            if (!stagedTrackName.equals(Sound.NO_TRACK)) {

                AssetPool.getSound(stagedTrackName).play();
            }

            if (!playingTrackName.equals(stagedTrackName)) {

                playingTrackName = stagedTrackName;
            }
        }
    }


    // GETTER
    public String getPlayingTrackName() {
        return playingTrackName;
    }
}
