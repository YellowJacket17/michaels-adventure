package render;

import org.lwjgl.BufferUtils;
import utility.UtilityTool;
import utility.exceptions.AssetLoadException;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * This class defines a texture to be bound to a drawn object.
 */
public class Texture {

    // FIELDS
    /**
     * Texture file path.
     */
    private final String filePath;

    /**
     * Texture ID.
     */
    private int textureId;

    /**
     * Native texture width.
     */
    private int nativeWidth;

    /**
     * Native texture height.
     */
    private int nativeHeight;


    // CONSTRUCTORS
    /**
     * Constructs a Texture instance.
     * The texture at the provided file path is loaded and uploaded to the GPU upon construction.
     * Note that as many textures as desired can be uploaded to the GPU as long as memory permits.
     * This should not be confused with the number of slots available for binding on the GPU for texture sampling.
     *
     * @param filePath file path of texture from resources directory
     */
    public Texture(String filePath) {
        this.filePath = filePath;
        load();
    }


    /**
     * Constructs a Texture instance.
     * An empty texture is prepared and allocated on the GPU upon construction.
     *
     * @param width texture width
     * @param height texture height
     */
    public Texture(int width, int height) {

        this.filePath = "auto-generated-texture";
        allocate(width, height);
    }


    // METHODS
    /**
     * Binds this texture to be used when drawing.
     * When binding, a shader is told where to find a texture that's been uploaded to the GPU via its texture ID.
     * This texture is bound to a slot on the GPU.
     * Inside a shader, the texture can then be retrieved from that slot and sampled to draw.
     */
    public void bind() {

        glBindTexture(GL_TEXTURE_2D, textureId);
    }


    /**
     * Unbinds this texture when finished being used.
     */
    public void unbind() {

        glBindTexture(GL_TEXTURE_2D, 0);
    }


    /**
     * Loads this texture from file and uploads it to the GPU.
     *
     * @throws RuntimeException
     */
    private void load() {

        // Generate texture on GPU.
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Parameter: repeat image in both directions.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Parameter: pixelate when stretching.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // Parameter: pixelate when shrinking.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Load image.
        IntBuffer bufferWidth = BufferUtils.createIntBuffer(1);
        IntBuffer bufferHeight = BufferUtils.createIntBuffer(1);
        IntBuffer bufferChannels = BufferUtils.createIntBuffer(1);                                                      // rgb or rgba.
        ByteBuffer image = UtilityTool.ioResourceToByteBuffer(filePath, 4096);
        ByteBuffer pixels = stbi_load_from_memory(image, bufferWidth, bufferHeight, bufferChannels, 0);
        if (pixels != null) {
            if (bufferChannels.get(0) == 3) {                                                                           // rbg image.
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, bufferWidth.get(0), bufferHeight.get(0),                         // Upload image to GPU.
                        0, GL_RGB, GL_UNSIGNED_BYTE, pixels);
            } else if (bufferChannels.get(0) == 4) {                                                                    // rgba image.
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bufferWidth.get(0), bufferHeight.get(0),                        // Upload image to GPU.
                        0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
            } else {
                throw new AssetLoadException("Unexpected number of channels (" + bufferChannels.get(0)
                        + ") in image for texture loaded from " + filePath);
            }
            nativeWidth = bufferWidth.get(0);
            nativeHeight = bufferHeight.get(0);
        } else {
            throw new AssetLoadException("Failed to load texture from " + filePath);
        }

        // Free memory.
        stbi_image_free(pixels);
    }


    /**
     * Allocates an empty constructor on the GPU.
     *
     * @param width texture width
     * @param height texture height
     */
    private void allocate(int width, int height) {

        // Generate texture on GPU.
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Allocate space for empty image.
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        nativeWidth = width;
        nativeHeight = height;
    }


    // GETTERS
    public String getFilePath() {
        return filePath;
    }

    public int getTextureId() {
        return textureId;
    }

    public int getNativeWidth() {
        return nativeWidth;
    }

    public int getNativeHeight() {
        return nativeHeight;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Texture)) {
            return false;
        }
        Texture oTexture = (Texture)o;
        return (oTexture.getNativeWidth() == this.nativeWidth)
                && (oTexture.getNativeHeight() == this.nativeHeight)
                && (oTexture.getTextureId() == this.textureId)
                && (oTexture.getFilePath().equals(this.filePath));
    }
}
