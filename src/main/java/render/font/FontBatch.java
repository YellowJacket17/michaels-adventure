package render.font;

import core.GamePanel;
import org.joml.Vector3f;
import asset.Shader;
import asset.AssetPool;
import utility.UtilityTool;

import java.util.Arrays;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * This class holds a batch of characters to be sent to the GPU and rendered in a single call.
 */
public class FontBatch {

    // FIELDS
    private final GamePanel gp;

    /**
     * Defines two position floats in the vertex array for each vertex.
     */
    private static final int POSITION_SIZE = 2;

    /**
     * Defines four color floats in the vertex array for each vertex.
     */
    private static final int COLOR_SIZE = 3;

    /**
     * Defines two texture coordinate floats in the vertex array for each vertex.
     * Note that a texture is not necessarily needed with texture coordinates.
     * Texture coordinates describe points within the quad.
     */
    private static final int TEXTURE_COORDS_SIZE = 2;

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
     * Total number of floats in each vertex of the vertex array.
     */
    private static final int VERTEX_SIZE = 7;

    /**
     * Maximum number of vertices that can be added to this batch.
     * As an aside, 100 vertices equals 25 quads.
     */
    private static final int MAX_BATCH_SIZE = 100;

    /**
     * Actual number of vertices added to this batch (vertex array) thus far.
     */
    private int numVertices;

    /**
     * Vertex array.
     * Note that this allows us to store a number of quads equal to the maximum batch size divided by four, since each
     * quad contains four vertices.
     * Each character to render requires a quad.
     */
    private final float[] vertices = new float[MAX_BATCH_SIZE * VERTEX_SIZE];

    /**
     * Base indices for generating a quad.
     */
    private final int[] indices = {
            0, 1, 3,
            1, 2, 3
    };

    /**
     * Vertex array object ID.
     */
    private int vaoId;

    /**
     * Vertex buffer object ID.
     */
    private int vboId;

    /**
     * Shader attached to this batch.
     */
    private final Shader shader;

    /**
     * Active font in this batch.
     * This is the font used to render text.
     */
    private CFont font;


    // CONSTRUCTOR
    /**
     * Constructs a FontBatch instance.
     *
     * @param gp GamePanel instance
     * @param zIndex rendering layer
     *
     */
    public FontBatch(GamePanel gp) {
        this.gp = gp;
        this.shader = AssetPool.getShader("/shaders/font.glsl");
        init();
    }


    // METHODS
    /**
     * Renders this batch then clears it of all characters.
     */
    public void flush() {

        render();
        clear();
    }


    /**
     * Adds a string of character to this batch.
     *
     * @param text text to render
     * @param x x-coordinate (leftmost)
     * @param y y-coordinate (topmost)
     * @param scale scale factor compared to native font size
     * @param color color (r, g, b)
     */
    public void addString(String text, float x, float y, float scale, Vector3f color) {

        for (int i = 0; i < text.length(); i++) {                                                                       // Add each character from the string to the batch, one at a time.

            char c = text.charAt(i);
            CharInfo charInfo = font.getCharacter(c);

            if (charInfo.getWidth() == 0) {

                UtilityTool.logError("Attempted to render a character (" + c + ") with zero width.");
            }
            addCharacter(x, y, scale, charInfo, color);                                                                 // Add character to batch.                                                    // Adds character to batch.
            x += charInfo.getWidth() * scale;                                                                           // Prepare for next character in string.
        }
    }


    /**
     * Adds a single character to this batch.
     *
     * @param x x-coordinate (leftmost)
     * @param y y-coordinate (topmost)
     * @param scale sale factor compared to native font size
     * @param charInfo character data
     * @param color color (r, g, b)
     */
    private void addCharacter(float x, float y, float scale, CharInfo charInfo, Vector3f color) {

        if (numVertices >= MAX_BATCH_SIZE) {

            flush();                                                                                                    // Flush batch (i.e., render then clear) to start fresh.
        }
        float r = color.x / 255;                                                                                        // Extract red information.
        float g = color.y / 255;                                                                                        // Extract green information.
        float b = color.z / 255;                                                                                        // Extract blue information.

        float x0 = x;                                                                                                   // Top-left corner (remember that positive y-direction is defined as down in this application).
        float y0 = y;                                                                                                   // ^^^
        float x1 = x + (scale * charInfo.getWidth());                                                                   // Bottom-right corner (remember that positive y-direction is defined as down in this application).
        float y1 = y + (scale * (charInfo.getHeight() + charInfo.getDescent()));                                        // ^^^ (also, modifying this value affects how "stretched" the text appears)

        float ux0 = charInfo.getTextureCoords()[0].x;
        float uy0 = charInfo.getTextureCoords()[1].y;                                                                   // Flipped with `uy1` since positive y-direction is defined as down in this application.
        float ux1 = charInfo.getTextureCoords()[1].x;
        float uy1 = charInfo.getTextureCoords()[0].y;

        int index = numVertices * 7;                                                                                    // First vertex with position, color, and texture coordinates; seven floats per vertex.
        vertices[index] = x1;                                                                                           // Position (X).
        vertices[index + 1] = y0;                                                                                       // Position (Y).
        vertices[index + 2] = r;                                                                                        // Color (red).
        vertices[index + 3] = g;                                                                                        // Color (green).
        vertices[index + 4] = b;                                                                                        // Color (blue).
        vertices[index + 5] = ux1;                                                                                      // Texture coordinate (X).
        vertices[index + 6] = uy0;                                                                                      // Texture coordinate (Y).

        index += 7;                                                                                                     // Second vertex with position, color, and texture coordinates.
        vertices[index] = x1;
        vertices[index + 1] = y1;
        vertices[index + 2] = r;
        vertices[index + 3] = g;
        vertices[index + 4] = b;
        vertices[index + 5] = ux1;
        vertices[index + 6] = uy1;

        index += 7;                                                                                                     // Third vertex with position, color, and texture coordinates.
        vertices[index] = x0;
        vertices[index + 1] = y1;
        vertices[index + 2] = r;
        vertices[index + 3] = g;
        vertices[index + 4] = b;
        vertices[index + 5] = ux0;
        vertices[index + 6] = uy1;

        index += 7;                                                                                                     // Fourth vertex with position, color, and texture coordinates.
        vertices[index] = x0;
        vertices[index + 1] = y0;
        vertices[index + 2] = r;
        vertices[index + 3] = g;
        vertices[index + 4] = b;
        vertices[index + 5] = ux0;
        vertices[index + 6] = uy0;

        numVertices += 4;                                                                                               // Four vertices (one character) have now been added.
    }


    /**
     * Renders all characters in this batch.
     */
    private void render() {

        // Clear buffer on GPU.
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        // Upload CPU contents (vertex data).
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Draw buffer that was just uploaded.
        shader.use();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, font.getTextureId());
        shader.uploadTexture("uFontTexture", 0);
        shader.uploadMat4f("uProjection", gp.getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", gp.getCamera().getViewMatrix());
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, (numVertices * 6), GL_UNSIGNED_INT, 0);

        // Unbind after drawing.
        glBindVertexArray(0);
        shader.detach();
        glBindTexture(GL_TEXTURE_2D, 0);
    }


    /**
     * Clears this batch of all characters, resetting it to its default initialized state.
     * Note that the set font is retained.
     */
    private void clear() {

        Arrays.fill(vertices, 0);
        numVertices = 0;
    }


    /**
     * Initializes this batch.
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
        glBufferData(GL_ARRAY_BUFFER, VERTEX_SIZE * MAX_BATCH_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);

        // Generate and bind element buffer object.
        generateEbo();

        // Enable buffer attribute pointers.
        int stride = VERTEX_SIZE * Float.BYTES;                                                                          // Size of the vertex array in bytes.
        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, stride, POSITION_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, stride, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, TEXTURE_COORDS_SIZE, GL_FLOAT, false, stride, TEXTURE_COORDS_OFFSET);
        glEnableVertexAttribArray(2);
    }


    /**
     * Generates an element buffer object large enough to hold the number of vertices specified by the batch size.
     */
    private void generateEbo() {

        int elementSize = MAX_BATCH_SIZE * 3;                                                                                // Multiply by three since there are three indices per triangle.
        int[] elementBuffer = new int[elementSize];

        for (int i = 0; i < elementSize; i++) {

            elementBuffer[i] = indices[(i % 6)] + ((i / 6) * 4);                                                        // Use pattern set by indices array.
        }
        int eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);                                                                   // Bind array.
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);                                           // Buffer array to GPU.
    }


    // GETTERS
    public boolean isEmpty() {
        if (numVertices == 0) {
            return true;
        }
        return false;
    }

    public String getFont() {
        if (font != null) {
            return font.getName();
        }
        return "";
    }


    // SETTER
    public void setFont(CFont font) {
        this.font = font;
    }
}
