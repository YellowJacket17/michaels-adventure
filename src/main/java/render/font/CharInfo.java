package render.font;

import org.joml.Vector2f;

/**
 * This class represents data for a single font character (size, texture coordinates, etc.).
 */
public class CharInfo {

    // FIELDS
    /**
     * Raw character coordinate on original generated parent font image.
     * This is NOT a texture coordinate and is only used to calculate texture coordinates.
     * Note that the parent image form which these raw coordinates are taken from has its origin set in the top-left
     * corner; all raw coordinates are in respect to that.
     */
    private final int sourceX, sourceY;

    /**
     * Native character width.
     */
    private final int width;

    /**
     * Native character height.
     * Note that this will be the same for all characters within a given font.
     */
    private final int height;

    /**
     * Character descent (i.e., amount character extends below baseline).
     * Note that this will be the same for all characters within a given font.
     */
    private final int descent;

    /**
     * Coordinates of this character on the parent font texture.
     * Note that texture coordinates are normalized from zero to one, where (0, 0) is the bottom-left corner of the
     * texture and (1, 1) is the top-right corner.
     *
     */
    private final Vector2f[] textureCoords = new Vector2f[4];

    /**
     * Width adjustment for this character when generating texture coordinates.
     * This amount is added to the original width right before converting to texture coordinates.
     * Increased width helps prevent any of the character from getting clipped on the rightmost side.
     */
    private static final int WIDTH_ADJUSTMENT = 3;


    // CONSTRUCTOR
    /**
     * Constructs a CharInfo instance.
     *
     * @param sourceX raw character coordinate on original generated parent font image
     * @param sourceY raw character coordinate on original generated parent font image
     * @param width character width
     * @param height character height
     * @param descent character descent
     */
    public CharInfo(int sourceX, int sourceY, int width, int height, int descent) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.width = width;
        this.height = height;
        this.descent = descent;
    }


    // METHOD
    /**
     * Calculates the texture coordinate of this character on the parent font texture.
     *
     * @param fontWidth width of parent texture containing font
     * @param fontHeight height of parent texture containing font
     */
    public void calculateTextureCoordinates(int fontWidth, int fontHeight) {

        float x0 = (float)sourceX / (float)fontWidth;                                                                   // Convert `sourceX` to a 0-1 range.
        float x1 = (float)(sourceX + width + WIDTH_ADJUSTMENT) / (float)fontWidth;                                      // Convert `sourceX + width + widthAdjustment` to a 0-1 range.
        float y0 = (float)(sourceY - height) / (float)fontHeight;                                                       // Convert `sourceY - height` to a 0-1 range.
        float y1 = ((float)sourceY / (float)fontHeight) + ((float)descent / (float)fontHeight);                         // Convert `sourceY + descent` to a 0-1 range.

        textureCoords[0] = new Vector2f(x0, y1);
        textureCoords[1] = new Vector2f(x1, y0);
    }


    // GETTERS
    public int getSourceX() {
        return sourceX;
    }

    public int getSourceY() {
        return sourceY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDescent() {
        return descent;
    }

    public Vector2f[] getTextureCoords() {
        return textureCoords;
    }
}
