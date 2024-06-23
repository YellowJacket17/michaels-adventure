package render;

import asset.Texture;
import core.GamePanel;
import render.drawable.Transform;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.drawable.Drawable;
import render.drawable.DrawableBatch;
import render.drawable.DrawableSingle;
import render.enumeration.ZIndex;
import render.font.CFont;
import render.font.FontBatch;
import render.font.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class manages the rendering of drawable objects (i.e., sending instructions to the GPU).
 */
public class Renderer {

    // FIELDS
    private final GamePanel gp;

    /**
     * List to store single drawables to render.
     */
    private final ArrayList<DrawableSingle> drawableSingles = new ArrayList<>();

    /**
     * List to store batches of drawables to render.
     */
    private final ArrayList<DrawableBatch> drawableBatches = new ArrayList<>();

    /**
     * Font batch to render.
     */
    private final FontBatch fontBatch;

    /**
     * List to store staged text to render.
     */
    private final ArrayList<Text> stagedText = new ArrayList<>();

    /**
     * Map to store loaded fonts; font name is the key, font is the value.
     */
    private final HashMap<String, CFont> fonts = new HashMap<>();

    /**
     * Drawable to use when rendering all rectangles requested by the `addRectangle()` and `addRoundRectangle()`
     * methods.
     * Working with this single drawable for all rectangles significantly improves memory usage/efficiency, especially when
     * rendering large batches of rectangles each frame.
     */
    Drawable memoryRectangle = new Drawable();


    // CONSTRUCTOR
    /**
     * Constructs a Renderer instance.
     *
     * @param gp GamePanel instance
     */
    public Renderer(GamePanel gp) {
        this.gp = gp;
        this.fontBatch = new FontBatch(gp);
        initializeFonts();
    }


    // METHODS
    /**
     * Renders all added drawables and text.
     */
    public void render() {

        // Render drawable layers in order.
        for (ZIndex zIndex : ZIndex.values()) {

            // Single drawables.
            for (DrawableSingle single : drawableSingles) {
                if (!single.isAvailable() && (single.getzIndex() == zIndex)) {
                    single.flush();
                }
            }

            // Batches of drawables.
            for (DrawableBatch batch : drawableBatches) {
                if (batch.hasDrawable() && (batch.getzIndex() == zIndex)) {
                    batch.flush();
                }
            }
        }

        // Text (always rendered as topmost layer).
        for (int i = 0; i < stagedText.size(); i++) {                                                                   // Loop though each staged string.
            if (i == 0) {
                fontBatch.setFont((fonts.get(stagedText.get(i).getFont())));                                            // Set initial font.
            } else {
                if (!stagedText.get(i - 1).getFont().equals(stagedText.get(i).getFont())) {
                    fontBatch.flush();                                                                                  // Manually flush batch before changing font.
                    fontBatch.setFont((fonts.get(stagedText.get(i).getFont())));                                        // Set next font.
                }
            }
            fontBatch.addString(stagedText.get(i).getText(),
                    stagedText.get(i).getScreenX(), stagedText.get(i).getScreenY(),
                    stagedText.get(i).getScale(), stagedText.get(i).getColor());
        }
        if (!fontBatch.isEmpty()) {
            fontBatch.flush();                                                                                          // Must manually flush at the end to render any remaining characters in the batch.
        }
        stagedText.clear();                                                                                             // Remove all staged text as it has already been rendered.
    }


    /**
     * Adds a drawable to the render pipeline.
     *
     * @param drawable Drawable instance to add
     * @param zIndex layer on which to render; drawables on the same layer will be rendered in the order in which they
     *               were added (bottom to top)
     */
    public void addDrawable(Drawable drawable, ZIndex zIndex) {

        if (drawable != null) {

            addDrawableToBatch(drawable, zIndex);
        }
    }


    /**
     * Adds a string of characters to the render pipeline.
     *
     * @param text text to add
     * @param x x-coordinate (leftmost)
     * @param y y-coordinate (topmost)
     * @param scale scale factor compared to native font size
     * @param color color (r, g, b)
     * @param font name of font to use
     */
    public void addString(String text, float x, float y, float scale, Vector3f color, String font) {

        stagedText.add(new Text(text, x, y, scale, color, font));
    }


    /**
     * Adds a rectangle with square corners to the render pipeline.
     *
     * @param color color of this rectangle (r, g, b, a)
     * @param transform position (top-left coordinate) and scale (width and height) of this rectangle
     * @param zIndex layer on which to render; drawables on the same layer will be rendered in the order in which they
     *               were added (bottom to top)
     */
    public void addRectangle(Vector4f color, Transform transform, ZIndex zIndex) {

        updateMemoryRectangle(color, transform);
        addDrawableToBatch(memoryRectangle, zIndex);
    }


    /**
     * Adds a rectangle with round corners to the render pipeline.
     *
     * @param color color of this rectangle (r, g, b, a)
     * @param transform position (top-left coordinate) and scale (width and height) of this rectangle
     * @param zIndex layer on which to render; drawables on the same layer will be rendered in the order in which they
     *               were added (bottom to top)
     * @param radius arc radius at four corners of this rectangle
     */
    public void addRoundRectangle(Vector4f color, Transform transform, ZIndex zIndex, float radius) {

        updateMemoryRectangle(color, transform);
        addDrawableToSingle(memoryRectangle, zIndex, radius);
    }


    /**
     * Retrieves a loaded font.
     *
     * @param name font name
     * @return font
     */
    public CFont getFont(String name) {

        return fonts.get(name);
    }


    /**
     * Adds a single drawable to render.
     *
     * @param drawable drawable to add
     * @param zIndex layer on which to render
     * @param radius arc radius at four corners of quad
     */
    private void addDrawableToSingle(Drawable drawable, ZIndex zIndex, float radius) {

        boolean added = false;

        for (DrawableSingle single : drawableSingles) {

            if (single.isAvailable()) {

                single.setDrawable(drawable);
                single.setzIndex(zIndex);
                single.setRadius(radius);
                added = true;
                break;
            }
        }

        if (!added) {

            DrawableSingle newSingle = new DrawableSingle(gp);
            newSingle.setzIndex(zIndex);
            newSingle.setRadius(radius);
            newSingle.setDrawable(drawable);
            drawableSingles.add(newSingle);
        }
    }


    /**
     * Adds a drawable to a batch to render.
     *
     * @param drawable drawable to add
     * @param zIndex layer on which to render
     */
    private void addDrawableToBatch(Drawable drawable, ZIndex zIndex) {

        boolean added = false;

        for (DrawableBatch batch : drawableBatches) {

            if (batch.hasRoom()) {

                Texture texture = drawable.getTexture();

                if (((texture == null) || (batch.hasTexture(texture) || batch.hasTextureRoom()))
                        && (batch.getzIndex() == zIndex)) {

                    batch.addDrawable(drawable);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {

            DrawableBatch newBatch = new DrawableBatch(gp);
            newBatch.setzIndex(zIndex);
            newBatch.addDrawable(drawable);
            drawableBatches.add(newBatch);
        }
    }


    /**
     * Updates the drawable used when rendering all requested rectangles (both non-rounded and rounded).
     *
     * @param color color of updated rectangle (r, g, b, a)
     * @param transform position (top-left coordinate) and scale (width and height) of updated rectangle
     */
    private void updateMemoryRectangle(Vector4f color, Transform transform) {

        memoryRectangle.transform.position.set(transform.position);
        memoryRectangle.transform.scale.set(transform.scale);
        memoryRectangle.setColor(color);
    }


    /**
     * Loads available fonts.
     */
    private void initializeFonts() {

        CFont font = new CFont("/fonts/Arimo-mO92.ttf", 128);
        fonts.put(font.getName(), font);

        font = new CFont("/fonts/ArimoBold-dVDx.ttf", 128);
        fonts.put(font.getName(), font);
    }
}
