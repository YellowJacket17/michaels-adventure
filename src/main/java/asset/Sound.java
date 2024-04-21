package asset;

import utility.exceptions.AssetLoadException;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

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
     * Sound file path.
     */
    private String filePath;

    /**
     * Buffer ID.
     */
    private int bufferId;

    /**
     * Source ID.
     */
    private int sourceId;

    /**
     * Boolean indicating whether this sound is playing (true) or not (false).
     */
    private boolean playing = false;

    /**
     * Default gain value.
     */
    private static final float DEFAULT_GAIN = 0.3f;


    // CONSTRUCTOR
    /**
     * Constructs a Sound instance.
     * Sounds should be in OGG file format.
     *
     * @param filePath file path of sound from root directory
     * @param loops whether the sound will loop (true) or not (false) upon play
     */
    public Sound(String filePath, boolean loops) {
        this.filePath = filePath;
        load(loops);
    }


    // METHODS
    /**
     * Plays this sound.
     * If this sound is already playing, nothing will happen.
     * If this sound is stopped, it will be restarted from the beginning.
     */
    public void play() {

        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);

        if (state == AL_STOPPED) {

            playing = false;                                                                                            // Sound is not playing, so ensure it is set as such.
            alSourcei(sourceId, AL_POSITION, 0);                                                                        // Return to beginning of sound.
            alSourcef(sourceId, AL_GAIN, DEFAULT_GAIN);                                                                 // Reset to default gain.
        }

        if (!playing) {

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
        alDeleteBuffers(bufferId);
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
     * @param loops whether the sound will loop (true) or not (false) upon play
     * @throws AssetLoadException
     */
    private void load(boolean loops) {

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
        bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

        // Generate sound (i.e., source); these values can be changed at runtime.
        sourceId = alGenSources();
        alSourcei(sourceId, AL_BUFFER, bufferId);
        alSourcei(sourceId, AL_LOOPING, (loops ? 1 : 0));                                                               // Looping of sound.
        alSourcei(sourceId, AL_POSITION, 0);                                                                            // Position of sound (i.e., timestamp sound will begin playing at).
        alSourcef(sourceId, AL_GAIN, DEFAULT_GAIN);                                                                     // Volume of sound.

        // Free stb raw audio buffer.
        free(rawAudioBuffer);
    }


    // GETTERS
    public String getFilePath() {
        return filePath;
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
}
