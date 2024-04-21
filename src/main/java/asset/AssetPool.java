package asset;

import utility.exceptions.AssetLoadException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class stores and manages resources loaded into memory.
 * The purpose of this is to prevent the same resource from being unnecessarily loaded multiple times.
 */
public class AssetPool {

    // FIELDS
    /**
     * Map to store all shaders loaded into the game.
     */
    private static final HashMap<String, Shader> SHADERS = new HashMap<>();

    /**
     * Map to store all textures loaded into the game.
     */
    private static final HashMap<String, Texture> TEXTURES = new HashMap<>();

    /**
     * Map to store all spritesheets loaded into the game.
     */
    private static final ArrayList<Spritesheet> SPRITESHEETS = new ArrayList<>();

    /**
     * Map to store all sounds loaded into the game.
     */
    private static final HashMap<String, Sound> SOUNDS = new HashMap<>();


    // METHODS
    /**
     * Returns a shader loaded into memory.
     * If the specified shader is not yet loaded, it will first be loaded from file and then returned.
     *
     * @param resourceName file path of shader from resources directory
     * @return shader
     */
    public static Shader getShader(String resourceName) {

        if (SHADERS.containsKey(resourceName)) {

            return SHADERS.get(resourceName);
        } else {

            Shader shader = new Shader(resourceName);
            SHADERS.put(resourceName, shader);
            return shader;
        }
    }


    /**
     * Returns a texture loaded into memory.
     * If the specified texture is not yet loaded, it will first be loaded from file and then returned.
     *
     * @param resourceName file path of texture from resources directory
     * @return texture
     */
    public static Texture getTexture(String resourceName) {

        if (TEXTURES.containsKey(resourceName)) {

            return TEXTURES.get(resourceName);
        } else {

            Texture texture = new Texture(resourceName);
            TEXTURES.put(resourceName, texture);
            return texture;
        }
    }


    /**
     * Loads a spritesheet into memory from file.
     * If the specified spritesheet is already loaded, then nothing will occur.
     *
     * @param spritesheet spritesheet to add
     */
    public static void addSpritesheet(Spritesheet spritesheet) {

        boolean repeat = false;

        for (Spritesheet loaded : SPRITESHEETS) {

            if (loaded.equals(spritesheet)) {

                repeat = true;
                break;
            }
        }

        if (!repeat) {

            SPRITESHEETS.add(spritesheet);
        }
    }


    /**
     * Returns a spritesheet loaded into memory.
     *
     * @param spritesheet index of spritesheet to retrieve (0, 1, 2, etc.); note that spritesheets are indexed in the
     *                    order in which they are loaded into memory
     * @return spritesheet
     * @throws AssetLoadException if the specified spritesheet is not yet loaded
     */
    public static Spritesheet getSpritesheet(int spritesheet) {

        try {

            return SPRITESHEETS.get(spritesheet);

        } catch (IndexOutOfBoundsException e) {

            throw new AssetLoadException("Attempted to access an unloaded spritesheet");
        }
    }


    /**
     * Loads a sound into memory from file.
     * If the specified sound is already loaded, then nothing will occur.
     *
     * @param resourceName file path of sound from root directory
     * @param loops whether the sound will loop (true) or not (false) upon play
     */
    public static void addSound(String resourceName, boolean loops) {

        boolean repeat = false;

        for (String loaded : SOUNDS.keySet()) {

            if (loaded.equals(resourceName)) {

                repeat = true;
                break;
            }
        }

        if (!repeat) {

            Sound sound = new Sound(resourceName, loops);
            SOUNDS.put(resourceName, sound);
        }
    }


    /**
     * Returns a sound loaded into memory.
     *
     * @param resourceName file path of sound from root directory
     * @throws AssetLoadException if the specified sound is not yet loaded
     */
    public static Sound getSound(String resourceName) {

        if (SOUNDS.containsKey(resourceName)) {

            return SOUNDS.get(resourceName);
        } else {

            throw new AssetLoadException("Attempted to access an unloaded sound");
        }
    }
}
