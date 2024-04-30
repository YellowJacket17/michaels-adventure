package asset;

import utility.exceptions.AssetLoadException;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Objects;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

/**
 * This class defines a sound to be played (music and sound effects).
 */
public class Sound {

    // BASIC FIELDS
    /**
     * Sound introduction file path.
     */
    private String introFilePath = NO_TRACK;

    /**
     * Sound loop file path.
     */
    private String loopFilePath = NO_TRACK;

    /**
     * Source ID.
     */
    private int sourceId;

    /**
     * Introduction buffer ID.
     */
    private int introBufferId;

    /**
     * Loop buffer ID.
     */
    private int loopBufferId;

    /**
     * Type of sound.
     */
    private final SoundType type;

    /**
     * Boolean indicating whether this sound is playing (true) or not (false).
     */
    private boolean playing = false;

    /**
     * Default gain value.
     */
    private static final float DEFAULT_GAIN = 0.3f;

    /**
     * Argument to be passed when no track will play.
     */
    public static final String NO_TRACK = "600a7daa-1abc-4708-86c2-f742a93f23b8";

    /**
     * Argument to be passed when no track swap will occur.
     */
    public static final String RETAIN_TRACK = "8daa0d6a-8d80-4337-b3fa-6eb7f081acdd";


    // CONSTRUCTORS
    /**
     * Constructs a Sound instance of type track.
     * Sounds should be in OGG file format.
     *
     * @param introFilePath file path of sound introduction from root directory
     * @param loopFilePath file path of sound loop from root directory
     */
    public Sound(String introFilePath, String loopFilePath) {
        this.introFilePath = introFilePath;
        this.loopFilePath = loopFilePath;
        this.type = SoundType.TRACK;
        load();
    }


    /**
     * Constructs a Sound instance of type effect.
     * Sounds should be in OGG file format.
     *
     * @param filePath file path of sound from root directory
     */
    public Sound(String filePath) {
        this.introFilePath = filePath;
        this.type = SoundType.EFFECT;
        load();
    }


    // METHODS
    /**
     * Updates the state of this sound by one frame, if applicable.
     */
    public void update() {

        if ((type == SoundType.TRACK)                                                                                   // If sound is of type track.
                && (alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING)                                              // If sound is playing.
                && (alGetSourcei(sourceId, AL_BUFFERS_PROCESSED) == 1)                                                  // If sound has one processed/completed buffer (i.e., introduction is finished).
                && (alGetSourcei(sourceId, AL_LOOPING) == 0)) {                                                         // If sound is not yet set to loop (i.e., introduction just finished).

            alSourceUnqueueBuffers(sourceId, new int[]{introBufferId});                                                 // Remove introduction buffer from queue (sound is now in main loop).
            alSourcei(sourceId, AL_LOOPING, 1);                                                                         // Sound is now in main loop, so actually set it to loop.
        }
    }


    /**
     * Plays this sound.
     * If this sound is already playing, nothing will happen.
     * If this sound is stopped, it will be restarted from the beginning.
     */
    public void play() {

        if (alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_STOPPED) {

            playing = false;                                                                                            // Sound is not playing, so ensure it is set as such.
            alSourcei(sourceId, AL_BUFFER, AL_NONE);                                                                    // Completely clear buffer queue.
            alSourcei(sourceId, AL_LOOPING, 0);                                                                         // Reset to not loop.
            alSourcei(sourceId, AL_POSITION, 0);                                                                        // Reset 3D position.
            alSourcef(sourceId, AL_GAIN, DEFAULT_GAIN);                                                                 // Reset to default gain.
        }

        if (!playing) {

            if (!introFilePath.equals(NO_TRACK)) {

                alSourceQueueBuffers(sourceId, introBufferId);                                                          // Add introduction buffer to queue.
            }

            if (!loopFilePath.equals(NO_TRACK)) {

                alSourceQueueBuffers(sourceId, loopBufferId);                                                           // Add loop buffer to queue.
            }
            alSourcePlay(sourceId);
            playing = true;
        }
    }


    /**
     * Stops this sound.
     * If this sound is already stopped, nothing will happen.
     */
    public void stop() {

        if (playing) {

            alSourceStop(sourceId);
            playing = false;
        }
    }


    /**
     * Frees this memory if this sound is finished being used.
     */
    public void delete() {

        alDeleteSources(sourceId);
        alSourcei(sourceId, AL_BUFFER, AL_NONE);                                                                        // Ensure all buffers are completely removed from queue.

        if (!introFilePath.equals(NO_TRACK)) {

            alDeleteBuffers(introBufferId);
        }
        if (!loopFilePath.equals(NO_TRACK)) {

            alDeleteBuffers(loopBufferId);
        }
    }


    /**
     * Adjusts the gain of this sound.
     *
     * @param difference amount to adjust gain by (positive or negative)
     */
    public void adjustGain(float difference) {

        alSourcef(sourceId, AL_GAIN, alGetSourcef(sourceId, AL_GAIN) + difference);
    }


    /**
     * Loads this sound from file.
     *
     * @throws AssetLoadException
     */
    private void load() {

        // Generate sound source.
        sourceId = alGenSources();

        // Load buffers.
        if (!introFilePath.equals(NO_TRACK)) {
            introBufferId = loadBuffer(introFilePath);
        }
        if (!loopFilePath.equals(NO_TRACK)) {
            loopBufferId = loadBuffer(loopFilePath);
        }

        // Set sound values; these can be changed at runtime.
        alSourcei(sourceId, AL_LOOPING, 0);                                                                             // Looping of sound (1 = true, 0 = false).
        alSourcei(sourceId, AL_POSITION, 0);                                                                            // 3D position of sound (not timestamp).
        alSourcef(sourceId, AL_GAIN, DEFAULT_GAIN);                                                                     // Volume of sound.
    }


    /**
     * Loads part of this sound from file and initializes the buffer linked to it.
     *
     * @param filePath file path of sound part from root directory
     * @return
     */
    private int loadBuffer(String filePath) {

        // Allocate space to store return information from stb.
        stackPush();                                                                                                    // Manages memory automatically; prevents memory leak of succeeding line when popped.
        IntBuffer channelsBuffer = stackMallocInt(1);                                                                   // Allocate memory size for one integer.
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        // Load sound.
        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filePath, channelsBuffer, sampleRateBuffer);
        if (rawAudioBuffer == null) {
            stackPop();                                                                                                 // Free memory.
            stackPop();                                                                                                 // ^^^
            throw new AssetLoadException("Failed to load sound from root '" + filePath + "'");
        }

        // Retrieve extra information stored in buffers by stb.
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();

        // Free memory.
        stackPop();
        stackPop();

        // Find correct OpenAL format (i.e., number of channels).
        int format = -1;
        if (channels == 1) {
            format = AL_FORMAT_MONO16;
        } else if (channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        // Create space to hold audio sample with OpenAL.
        int bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

        // Free stb raw audio buffer.
        free(rawAudioBuffer);

        // Return generated buffer ID.
        return bufferId;
    }


    // GETTERS
    public String getIntroFilePath() {
        return introFilePath;
    }

    public String getLoopFilePath() {
        return loopFilePath;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getIntroBufferId() {
        return introBufferId;
    }

    public int getLoopBufferId() {
        return loopBufferId;
    }

    public SoundType getType() {
        return type;
    }

    public boolean isPlaying() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            playing = false;
        }
        return playing;
    }

    public float getGain() {

        return alGetSourcef(sourceId, AL_GAIN);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Sound)) {
            return false;
        }
        Sound oSound = (Sound)o;
        return (oSound.getIntroFilePath().equals(this.introFilePath))
                && (oSound.getLoopFilePath().equals(this.loopFilePath))
                && (oSound.getSourceId() == this.sourceId)
                && (oSound.getIntroBufferId() == this.introBufferId)
                && (oSound.getLoopBufferId() == this.loopBufferId);
    }
}
