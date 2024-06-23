package asset;

import core.GamePanel;
import org.joml.Vector2f;

/**
 * This class defines an illustration to be displayed.
 */
public class Illustration {

    // FIELDS
    /**
     * Sprite containing this illustration.
     */
    private final Sprite sprite;

    /**
     * Argument to be passed when no illustration is to be displayed.
     */
    public static final String NO_ILLUSTRATION = "f6a8a8f1-a38b-4082-9d10-532afca0a4e1";


    // CONSTRUCTOR
    /**
     * Constructs an Illustration instance.
     *
     * @param texture parent texture of this illustration
     */
    public Illustration(Texture texture) {
        Vector2f[] textureCoords = {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
        sprite = new Sprite(texture, textureCoords, texture.getNativeWidth(), texture.getNativeHeight());
    }


    // GETTER
    public Sprite getSprite() {
        return sprite;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Illustration)) {
            return false;
        }
        Illustration oIllustration = (Illustration) o;
        return oIllustration.getSprite().getTexture().equals(this.sprite.getTexture());
    }
}
