package render.drawable;

import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector4f;
import render.Shader;
import render.Sprite;
import render.ZIndex;
import utility.AssetPool;

import java.util.Arrays;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * This class holds a single drawable to be sent to the GPU and rendered.
 * Note that this is more computationally expensive than rendering in a batch.
 * That said, it is not possible to render quads with rounded corners in a batch.
 */
public class DrawableSingle {

    // FIELDS
    private final GamePanel gp;

    /**
     * Defines two position floats in the vertex array for each vertex.
     */
    private static final int POSITION_SIZE = 2;

    /**
     * Defines four color floats in the vertex array for each vertex.
     */
    private static final int COLOR_SIZE = 4;

    /**
     * Defines two texture coordinate floats in the vertex array for each vertex.
     */
    private static final int TEXTURE_COORDS_SIZE = 2;

    /**
     * Defines one texture ID float in the vertex array for each vertex.
     */
    private static final int TEXTURE_ID_SIZE = 1;

    /**
     * Defines the offset (in bytes) of the start of the position floats in the vertex array for each vertex.
     * Here, the position starts at the beginning of a vertex definition, so it has zero offset.
     */
    private static final int POSITION_OFFSET = 0;

    /**
     * Defines the offset (in bytes) of the start of the color floats in the vertex array for each vertex.
     * Here, the color starts after the position in a vertex definition, so it has an offset determined by the position.
     */
    private static final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;

    /**
     * Defines the offset (in bytes) of the start of the texture coordinate floats in the vertex array for each vertex.
     * Here, the texture coordinates start after the color in a vertex definition, so it has an offset determined by the
     * color.
     */
    private static final int TEXTURE_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;

    /**
     * Defines the offset (in bytes) of the start of the texture ID float in the vertex array for each vertex.
     * Here, the texture ID starts after the texture coordinates in a vertex definition, so it has an offset determined
     * by the texture coordinates.
     */
    private static final int TEXTURE_ID_OFFSET = TEXTURE_COORDS_OFFSET + TEXTURE_COORDS_SIZE * Float.BYTES;

    /**
     * Total number of floats in each vertex of the vertex array.
     * Remember that a vertex represents a corner of a quad being rendered in this case.
     */
    private static final int VERTEX_SIZE = 9;

    /**
     * Drawable to be rendered.
     */
    private Drawable drawable = new Drawable();

    /**
     * Placeholder sprite to reset this drawable with.
     * This allows the same sprite in memory to always be used to reset this drawable.
     */
    private final Sprite placeholderSprite = new Sprite();

    /**
     * Boolean indicating whether a drawable is occupying this single.
     */
    private boolean available;

    /**
     * Vertex array.
     * Note that there are four vertices per quad, hence the multiplication by four.
     */
    private final float[] vertices = new float[4 * VERTEX_SIZE];

    /**
     * Vertex array object ID.
     */
    private int vaoId;

    /**
     * Vertex buffer object ID.
     */
    private int vboId;

    /**
     * Slots available to bind textures for sampling during a draw in this single.
     * In practice in this class, only two texture slots will ever be used: slot 0 (reserved for empty texture) and
     * slot 1 (used if the drawable has a non-null texture).
     * This array is defined so that this class is compatible with the shader, which supports eight textures.
     */
    private final int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    /**
     * Shader attached to this rectangle.
     */
    private final Shader shader;

    /**
     * Layer on which this single will be rendered.
     */
    private ZIndex zIndex = ZIndex.THIRD_LAYER;

    /**
     * Radius of rounded corners.
     */
    private float radius;


    // CONSTRUCTOR
    /**
     * Constructs a BatchRenderer instance.
     *
     * @param gp GamePanel instance
     */
    public DrawableSingle(GamePanel gp) {
        this.gp = gp;
        this.shader = AssetPool.getShader("/shaders/rounded.glsl");
        init();
    }


    // METHODS
    /**
     * Renders this single then clears its drawable.
     */
    public void flush() {

        render();
        clear();
    }


    /**
     * Sets a drawable to render.
     *
     * @param drawable Drawable instance to set
     */
    public void setDrawable(Drawable drawable) {

        drawable.copy(this.drawable);
        loadVertexProperties();
        available = false;
    }


    /**
     * Sets the layer on which this single will be rendered.
     *
     * @param zIndex layer on which to render
     */
    public void setzIndex(ZIndex zIndex) {

        this.zIndex = zIndex;
    }


    /**
     * Sets the radius of the arc at the four corners of the quad.
     *
     * @param radius arc radius
     */
    public void setRadius(float radius) {

        this.radius = radius;
    }


    /**
     * Renders all drawables in this batch.
     */
    private void render() {

        // Re-buffer all data.
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Bind shader program.
        shader.use();

        // Camera.
        shader.uploadMat4f("uProjection", gp.getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", gp.getCamera().getViewMatrix());
        shader.uploadVec2f("uDimensions", new Vector2f(drawable.transform.scale.x, drawable.transform.scale.y));
        shader.uploadFloat("uRadius", radius);

        // Bind texture.
        if (drawable.getTexture() != null) {
            glActiveTexture(GL_TEXTURE0 + 1);                                                                           // Activate texture in slot 1.
            drawable.getTexture().bind();
        }
        shader.uploadIntArray("uTextures", textureSlots);

        // Bind VAO being used.
        glBindVertexArray(vaoId);

        // Enable vertex attribute pointers.
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw.
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        // Unbind after drawing.
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);                                                                                           // 0 is a flag that states to bind nothing.
        shader.detach();                                                                                                // Detach shader program.
        if (drawable.getTexture() != null) {
            drawable.getTexture().unbind();
        }
    }


    /**
     * Clears the drawable from this single, resetting it to its default initialized state.
     */
    private void clear() {

        Arrays.fill(vertices, 0);
        drawable.transform.position.x = 0;
        drawable.transform.position.y = 0;
        drawable.transform.scale.x = 0;
        drawable.transform.scale.y = 0;
        drawable.getColor().x = 0;
        drawable.getColor().y = 0;
        drawable.getColor().z = 0;
        drawable.getColor().y = 0;
        drawable.setSprite(placeholderSprite);
        radius = 0;
        available = true;
    }


    /**
     * Initializes this single.
     * All necessary data is created on the GPU.
     * In other words, space is allocated on the GPU.
     */
    private void init() {

        // Generate and bind a vertex array object.
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Allocate space for vertices.
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer.
        int eboId = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable buffer attribute pointers.
        int stride = VERTEX_SIZE * Float.BYTES;                                                                          // Size of the vertex array in bytes.
        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, stride, POSITION_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, stride, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, TEXTURE_COORDS_SIZE, GL_FLOAT, false, stride, TEXTURE_COORDS_OFFSET);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, stride, TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }


    /**
     * Generates indices for this quad.
     *
     * @return indices
     */
    private int[] generateIndices() {

        int[] elements = new int[6];                                                                                    // 6 indices per quad (3 per triangle).
        loadElementIndices(elements);
        return elements;
    }


    /**
     * Loads the indices for a single quad in an array of indices.
     *
     * @param elements array of indices that contains the target quad
     */
    private void loadElementIndices(int[] elements) {

        // Triangle 1.
        elements[0] = 3;
        elements[1] = 2;
        elements[2] = 0;

        // Triangle 2.
        elements[3] = 0;
        elements[4] = 2;
        elements[5] = 1;
    }


    /**
     * Loads the vertex properties of this drawable.
     */
    private void loadVertexProperties() {

        // Initialize offset within array (4 vertices per drawable, start with first).
        int offset = 0;

        // Color.
        Vector4f color = drawable.getColor();

        // Texture.
        Vector2f[] textureCoords = drawable.getTextureCoords();
        int textureId = 0;
        if (drawable.getTexture() != null) {
            textureId = 1;                                                                                              // Texture slot 0 is reserved for no bound texture to sprite, hence why slot 1 is set here.
        }

        // Add vertices with appropriate properties.
        // This assumes positioning everything according to bottom-left corner.
        // Add clockwise, starting from top-right.
        // *    *
        // *    *
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }

            // Load position.
            vertices[offset] = drawable.transform.position.x + (xAdd * drawable.transform.scale.x);
            vertices[offset + 1] = drawable.transform.position.y + (yAdd * drawable.transform.scale.y);

            // Load color.
            vertices[offset + 2] = color.x / 255;                                                                       // Red information.
            vertices[offset + 3] = color.y / 255;                                                                       // Green information.
            vertices[offset + 4] = color.z / 255;                                                                       // Blue information.
            vertices[offset + 5] = color.w / 255;                                                                       // Alpha information.

            // Load texture coordinates.
            vertices[offset + 6] = textureCoords[i].x;
            vertices[offset + 7] = textureCoords[i].y;

            // Load texture ID.
            vertices[offset + 8] = textureId;

            // Increment.
            offset += VERTEX_SIZE;
        }
    }


    // GETTERS
    public boolean isAvailable() {
        return available;
    }

    public ZIndex getzIndex() {
        return zIndex;
    }

    public float getRadius() {
        return radius;
    }
}
