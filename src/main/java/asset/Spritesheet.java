package asset;

import org.joml.Vector2f;
import utility.UtilityTool;

import java.util.ArrayList;

/**
 * This class represent a spritesheet that contains many sprites to be extracted.
 * A spritesheet is a parent entire texture, while a sprite is a section of a spritesheet (i.e., texture).
 */
public class Spritesheet {

    // FIELDS
    /**
     * Parent texture of this spritesheet.
     */
    private final Texture texture;

    /**
     * List to store all sprites derived from this spritesheet.
     */
    private final ArrayList<Sprite> sprites = new ArrayList<>();


    // CONSTRUCTORS
    /**
     * Constructs a Spritesheet instance.
     * Sprites are derived from this spritesheet upon construction.
     * This constructor is used for spritesheets containing sprites with uniform widths and heights.
     *
     * @param texture      parent texture of this spritesheet to derive sprites from
     * @param numSprites   number of sprites contained within this spritesheet
     * @param spriteWidth  native width of each sprite in this spritesheet
     * @param spriteHeight native height of each sprite in this spritesheet
     * @param spacing      native padding (pixels) surrounding each sprite in this spritesheet
     */
    public Spritesheet(Texture texture, int numSprites, int spriteWidth, int spriteHeight, int spacing) {

        this.texture = texture;
        loadUniform(numSprites, spriteWidth, spriteHeight, spacing);
    }


    /**
     * Constructs a Spritesheet instance.
     * Sprites are derived from this spritesheet upon construction.
     * This constructor is used for spritesheets containing sprites with non-uniform widths and heights.
     *
     * @param texture       parent texture of this spritesheet to derive sprites from
     * @param numSprites    number of sprites contained within this spritesheet
     * @param spriteWidths  native width of each sprite in this spritesheet, ordered left-to-right, bottom-to-top
     * @param spriteHeights native height of each sprite in this spritesheet, ordered left-to-right, bottom-to-top
     * @param spacing       native padding (pixels) surrounding each sprite in this spritesheet
     */
    public Spritesheet(Texture texture, int numSprites, int[] spriteWidths, int[] spriteHeights, int spacing) {

        this.texture = texture;
        loadNonUniform(numSprites, spriteWidths, spriteHeights, spacing);
    }


    // METHODS
    /**
     * Derives sprites from a texture containing sprites with uniform widths and heights.
     * Sprites are read left-to-right, bottom-to-top when deriving; in other words, the bottom-left sprite in the
     * spritesheet is read first and the top-right sprite is read last.
     *
     * @param numSprites   number of sprites contained within this spritesheet
     * @param spriteWidth  native width of each sprite in this spritesheet
     * @param spriteHeight native height of each sprite in this spritesheet
     * @param padding      native padding (pixels) surrounding each sprite
     */
    private void loadUniform(int numSprites, int spriteWidth, int spriteHeight, int padding) {

        if (UtilityTool.VERBOSE_LOGGING) {
            UtilityTool.logInfo("Loading uniform spritesheet from texture: '" + texture.getFilePath() + "'");
        }
        int currentX = padding;
        int currentY = texture.getNativeHeight() - spriteHeight - padding;

        for (int i = 0; i < numSprites; i++) {

            // Normalize sprite coordinates on parent texture;
            float topY = (currentY + spriteHeight) / (float)texture.getNativeHeight();
            float rightX = (currentX + spriteWidth) / (float)texture.getNativeWidth();
            float leftX = currentX / (float)texture.getNativeWidth();
            float bottomY = currentY / (float)texture.getNativeHeight();

            // Store coordinates of sprite on parent texture.
            Vector2f[] textureCoords = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY), // +0.0001f
                    new Vector2f(leftX, bottomY),  // +0.0001f
                    new Vector2f(leftX, topY)
            };

            // Create sprite using coordinates on parent texture.
            Sprite sprite = new Sprite(texture, textureCoords, spriteWidth, spriteHeight);
            sprites.add(sprite);

            // Iterate to next sprite in parent texture.
            currentX += spriteWidth + (2 * padding);
            if (currentX >= texture.getNativeWidth()) {
                currentX = padding;
                currentY -= spriteHeight + (2 * padding);
            }
        }
    }


    /**
     * Derives sprites from a texture containing sprites with non-uniform widths and heights.
     * Sprites are read left-to-right, bottom-to-top when deriving; in other words, the bottom-left sprite in the
     * texture is read first and the top-right sprite is read last.
     * Each sprite should be placed in the texture with its topmost edge at the highest point in its respective row.
     * Ensure that the first sprite in each row is the tallest in said row.
     * The first sprite in a row determines the maximum height of said row.
     *
     * @param numSprites    number of sprites contained within this spritesheet
     * @param spriteWidths  native width of each sprite in this spritesheet, ordered left-to-right, bottom-to-top
     * @param spriteHeights native height of each sprite in this spritesheet, ordered left-to-right, bottom-to-top
     * @param padding       native padding (pixels) surrounding each sprite in this spritesheet
     */
    private void loadNonUniform(int numSprites, int[] spriteWidths, int[] spriteHeights, int padding) {

        if (UtilityTool.VERBOSE_LOGGING) {
            UtilityTool.logInfo("Loading non-uniform spritesheet from texture: '" + texture.getFilePath() + "'");
        }
        int currentX = padding;
        int currentY = texture.getNativeHeight() - spriteHeights[0] - padding;
        int spriteHeightLeftmostInRow = spriteHeights[0];

        for (int i = 0; i < numSprites; i++) {

            // Normalize sprite coordinates on parent texture;
            float topY = (currentY + spriteHeights[i]) / (float) texture.getNativeHeight();
            float rightX = (currentX + spriteWidths[i]) / (float) texture.getNativeWidth();
            float leftX = currentX / (float) texture.getNativeWidth();
            float bottomY = currentY / (float) texture.getNativeHeight();

            // Store coordinates of sprite on parent texture.
            Vector2f[] textureCoords = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY)
            };

            // Create sprite using coordinates on parent texture.
            Sprite sprite = new Sprite(texture, textureCoords, spriteWidths[i], spriteHeights[i]);
            sprites.add(sprite);

            // Iterate to next sprite in parent texture if necessary.
            if (i < (numSprites - 1)) {
                currentX += spriteWidths[i] + (2 * padding);
                if (((currentX + spriteWidths[i + 1]) > texture.getNativeWidth())                                       // If width of next sprite cannot fit in current row, it must be in the next row up.
                        || (spriteHeights[i + 1] > spriteHeightLeftmostInRow)) {                                        // If height of next sprite is taller than the leftmost in the current spritesheet row, it must be in the next row up.
                    currentX = padding;
                    currentY -= spriteHeights[i + 1] + (2 * padding);
                    spriteHeightLeftmostInRow = spriteHeights[i + 1];
                }
            }
        }
    }


    // GETTERS
    public Texture getTexture() {
        return texture;
    }

    public Sprite getSprite(int index) {
        return sprites.get(index);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Spritesheet)) {
            return false;
        }
        Spritesheet oSpritesheet = (Spritesheet) o;
        return oSpritesheet.getTexture().equals(this.texture);
    }
}
