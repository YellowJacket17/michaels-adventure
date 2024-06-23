package asset;

import asset.enumeration.SoundType;
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
    private static final HashMap<String, Spritesheet> SPRITESHEETS = new HashMap<>();

    /**
     * Map to store all sounds loaded into the game.
     */
    private static final HashMap<String, Sound> SOUNDS = new HashMap<>();

    /**
     * Map to store all illustrations loaded into the game.
     */
    private static final HashMap<String, Illustration> ILLUSTRATIONS = new HashMap<>();


    // METHODS
    /**
     * Returns a shader loaded into memory.
     * If the specified shader is not yet loaded, it will first be loaded from file and then returned.
     *
     * @param resourceFilePath file path of shader from resources directory
     * @return shader
     */
    public static Shader getShader(String resourceFilePath) {

        if (SHADERS.containsKey(resourceFilePath)) {

            return SHADERS.get(resourceFilePath);
        } else {

            Shader shader = new Shader(resourceFilePath);
            SHADERS.put(resourceFilePath, shader);
            return shader;
        }
    }


    /**
     * Returns a texture loaded into memory.
     * If the specified texture is not yet loaded, it will first be loaded from file and then returned.
     *
     * @param resourceFilePath file path of texture from resources directory
     * @return texture
     */
    public static Texture getTexture(String resourceFilePath) {

        if (TEXTURES.containsKey(resourceFilePath)) {

            return TEXTURES.get(resourceFilePath);
        } else {

            Texture texture = new Texture(resourceFilePath);
            TEXTURES.put(resourceFilePath, texture);
            return texture;
        }
    }


    /**
     * Loads a spritesheet into memory from file.
     * If the specified spritesheet is already loaded, then nothing will happen.
     *
     * @param resourceName name/title of spritesheet
     * @param spritesheet spritesheet to add
     * @throws AssetLoadException if the specified spritesheet is already loaded
     */
    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {

        boolean duplicate = false;

        for (Spritesheet loadedResource : SPRITESHEETS.values()) {

            if (loadedResource.equals(spritesheet)) {

                duplicate = true;
                break;
            }
        }

        if (!duplicate) {

            SPRITESHEETS.put(resourceName, spritesheet);
        } else {

            throw new AssetLoadException("Attempted to load a duplicate spritesheet into memory");
        }
    }


    /**
     * Returns a spritesheet loaded into memory.
     *
     * @param resourceName name/title of spritesheet
     * @return spritesheet loaded spritesheet
     * @throws AssetLoadException if the specified spritesheet is not yet loaded
     */
    public static Spritesheet getSpritesheet(String resourceName) {

        try {

            return SPRITESHEETS.get(resourceName);

        } catch (IndexOutOfBoundsException e) {

            throw new AssetLoadException("Attempted to access an unloaded spritesheet named '" + resourceName + "'");
        }
    }


    /**
     * Purges all loaded spritesheets along with their underlying textures, which are freed from memory.
     * If said textures are loaded in the TEXTURES (hash)map, they will also be removed from there.
     * Note that if said textures were associated with any other assets (illustrations, spritesheets, etc.) or otherwise
     * referenced, said assets/references will no longer be valid.
     * This invalidation includes any derived sprites.
     * Also note that this method does not destruct any object instances, which must be managed by JVM garbage
     * collection.
     * If no spritesheets are currently loaded, nothing will happen.
     */
    public static void purgeSpritesheets() {

        for (Spritesheet spritesheet : SPRITESHEETS.values()) {

            spritesheet.getTexture().delete();
            int textureId = spritesheet.getTexture().getTextureId();
            ArrayList<String> keysToPurge = new ArrayList<>();

            for (String resourceFilePath : TEXTURES.keySet()) {

                if (TEXTURES.get(resourceFilePath).getTextureId() == textureId) {

                    keysToPurge.add(resourceFilePath);
                }
            }

            for (String resourceFilePath : keysToPurge) {

                TEXTURES.remove(resourceFilePath);
            }
        }
        SPRITESHEETS.clear();
    }


    /**
     * Loads a sound of type track into memory from file.
     * If the specified sound is already loaded, then nothing will happen.
     *
     * @param resourceName name/title of sound
     * @param introFilePath file path of sound introduction from root directory
     * @param loopFilePath file path of sound loop from root directory
     * @throws AssetLoadException if the specified sound is already loaded
     */
    public static void addSound(String resourceName, String introFilePath, String loopFilePath) {

        boolean duplicate = false;

        for (Sound loadedResource : SOUNDS.values()) {

            if (loadedResource.getIntroFilePath().equals(introFilePath)
                    && loadedResource.getLoopFilePath().equals(loopFilePath)
                    && loadedResource.getType() == SoundType.TRACK) {

                duplicate = true;
                break;
            }
        }

        if (!duplicate) {

            Sound sound = new Sound(introFilePath, loopFilePath);
            SOUNDS.put(resourceName, sound);
        } else {

            throw new AssetLoadException("Attempted to load a duplicate sound into memory");
        }
    }


    /**
     * Loads a sound of type effect into memory from file.
     * If the specified sound is already loaded, then nothing will occur.
     *
     * @param resourceName name/title of sound
     * @param resourceFilePath file path of sound from root directory
     * @throws AssetLoadException if the specified sound is already loaded
     */
    public static void addSound(String resourceName, String resourceFilePath) {

        boolean duplicate = false;

        for (Sound loadedResource : SOUNDS.values()) {

            if (loadedResource.getIntroFilePath().equals(resourceFilePath)
                    && loadedResource.getType() == SoundType.EFFECT) {

                duplicate = true;
                break;
            }
        }

        if (!duplicate) {

            Sound sound = new Sound(resourceFilePath);
            SOUNDS.put(resourceName, sound);
        } else {

            throw new AssetLoadException("Attempted to load a duplicate sound into memory");
        }
    }


    /**
     * Returns a sound loaded into memory.
     *
     * @param resourceName name/title of sound
     * @throws AssetLoadException if the specified sound is not yet loaded
     */
    public static Sound getSound(String resourceName) {

        if (SOUNDS.containsKey(resourceName)) {

            return SOUNDS.get(resourceName);
        } else {

            throw new AssetLoadException("Attempted to access an unloaded sound named '" + resourceName + "'");
        }
    }


    /**
     * Purges all loaded sounds, which are freed from memory.
     * Both sounds of type track and effect are purged.
     * Note that this method does not destruct any object instances, which must be managed by JVM garbage collection.
     * If no sounds are currently loaded, nothing will happen.
     */
    public static void purgeSounds() {

        for (Sound sound : SOUNDS.values()) {

            sound.delete();
        }
        SOUNDS.clear();
    }


    /**
     * Loads an illustration into memory from file.
     * If the specified illustration is already loaded, then nothing will happen.
     *
     * @param resourceName name/title of illustration
     * @param illustration illustration to add
     * @throws AssetLoadException if the specified illustration is already loaded
     */
    public static void addIllustration(String resourceName, Illustration illustration) {

        boolean duplicate = false;

        for (Illustration loadedResource : ILLUSTRATIONS.values()) {

            if (loadedResource.equals(illustration)) {

                duplicate = true;
                break;
            }
        }

        if (!duplicate) {

            ILLUSTRATIONS.put(resourceName, illustration);
        } else {

            throw new AssetLoadException("Attempted to load a duplicate illustration into memory");
        }
    }


    /**
     * Returns an illustration loaded into memory.
     *
     * @param resourceName name/title of illustration
     * @return illustration loaded illustration
     * @throws AssetLoadException if the specified illustration is not yet loaded
     */
    public static Illustration getIllustration(String resourceName) {

        try {

            return ILLUSTRATIONS.get(resourceName);

        } catch (IndexOutOfBoundsException e) {

            throw new AssetLoadException("Attempted to access an unloaded illustration named '" + resourceName + "'");
        }
    }


    /**
     * Purges all loaded illustrations along with their underlying textures, which are freed from memory.
     * If said textures are loaded in the TEXTURES (hash)map, they will also be removed from there.
     * Note that if said textures were associated with any other assets (illustrations, spritesheets, etc.) or otherwise
     * referenced, said assets/references will no longer be valid.
     * Also note that this method does not destruct any object instances, which must be managed by JVM garbage
     * collection.
     * If no illustrations are currently loaded, nothing will happen.
     */
    public static void purgeIllustrations() {

        for (Illustration illustration : ILLUSTRATIONS.values()) {

            illustration.getSprite().getTexture().delete();
            int textureId = illustration.getSprite().getTexture().getTextureId();
            ArrayList<String> keysToPurge = new ArrayList<>();

            for (String resourceFilePath : TEXTURES.keySet()) {

                if (TEXTURES.get(resourceFilePath).getTextureId() == textureId) {

                    keysToPurge.add(resourceFilePath);
                }
            }

            for (String resourceFilePath : keysToPurge) {

                TEXTURES.remove(resourceFilePath);
            }
        }
        ILLUSTRATIONS.clear();
    }
}
