package render.font;

import org.joml.Vector3f;
import render.enumeration.ZIndex;

/**
 * This class represents a piece of text.
 */
public class Text {

    // FIELDS
    /**
     * Text contents.
     */
    private final String text;

    /**
     * X-coordinate (leftmost) of this text.
     */
    private final float screenX;

    /**
     * Y-coordinate (topmost) of this text.
     */
    private final float screenY;

    /**
     * Scale factor compared to native font size.
     */
    private final float scale;

    /**
     * Color (r, g, b).
     */
    private final Vector3f color;

    /**
     * Font name.
     */
    private final String font;

    /**
     * Layer of this text.
     */
    private final ZIndex zIndex;


    // CONSTRUCTOR
    /**
     * Constructs a Text instance
     *
     * @param text text contents
     * @param screenX x-coordinate (leftmost) of text
     * @param screenY y-coordinate (topmost) of text
     * @param scale scale factor compared to native font size
     * @param color color in hexadecimal format
     * @param font font name
     */
    public Text(String text, float screenX, float screenY, float scale, Vector3f color, String font, ZIndex zIndex) {
        this.text = text;
        this.screenX = screenX;
        this.screenY = screenY;
        this.scale = scale;
        this.color = color;
        this.font = font;
        this.zIndex = zIndex;
    }


    // GETTERS
    public String getText() {
        return text;
    }

    public float getScreenX() {
        return screenX;
    }

    public float getScreenY() {
        return screenY;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getColor() {
        return color;
    }

    public String getFont() {
        return font;
    }

    public ZIndex getzIndex() {
        return zIndex;
    }
}
