package render;

import entity.EntityBase;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector2f;

/**
 * This class defines the system-level camera.
 * Note that this is not the camera used in actual gameplay, but rather the overarching viewport of the renderer.
 */
public class Camera {

    /*
     * Below are general definitions for the projection, view, and position matrices.
     *
     * The perspective matrix retains perspective.
     * For example, as an object gets further away from the camera, it appears smaller.
     *
     * The orthographic matrix is fixed perspective.
     * No matter the distance from the camera, objects remain the same size.
     *
     * Both the perspective and orthographic matrices have a near and far clipping plain.
     *
     * The projection matrix is either the perspective matrix or the orthographic matrix.
     * In our case, it's the orthographic matrix since we're working on a 2D game.
     *
     * The view matrix defines where the camera is looking from.
     *
     * The position matrix (aPos) defines world coordinates.
     * Essentially, this defines the world coordinates of objects placed in the game.
     *
     * Matrix multiplication is: projection*view*position
     * The order is position multiplied by view, then that result multiplied by projection.
     *
     * In summary:
     * Projection matrix tells us how big we want the screen space to be.
     * View matrix tells us where the camera is in relation to the world.
     * Position matrix (aPos) tells us what the world coordinates are.
     * By doing projection*view*position, we go from world coordinates to normalized coordinates (-1 to 1).
     * The normalized coordinates are what are actually drawn on screen.
     */

    // BASIC FIELDS
    /**
     * Projection matrix.
     * The projection matrix determines how large the screen space is.
     * In this case, the projection matrix is an orthographic matrix of fixed perspective.
     * Note that units here are not necessarily pixels, but our own units.
     */
    private final Matrix4f projectionMatrix;

    /**
     * View matrix.
     * The view matrix determines where the system camera is in relation to the world.
     */
    private final Matrix4f viewMatrix;

    /**
     * Position matrix.
     * The position matrix determines what the world coordinates are.
     */
    private final Vector2f positionMatrix;

    /**
     * Visible screen width.
     * Note that this are NOT necessarily pixels being defined: it's our own screen coordinate system.
     */
    private int screenWidth;

    /**
     * Visible screen height.
     * Note that this are NOT pixels necessarily being defined: it's our own screen coordinate system.
     */
    private int screenHeight;


    // CONSTRUCTOR
    /**
     * Constructs a Camera instance.
     *
     * @param screenWidth visible screen width (NOT necessarily pixels); the larger the value, the more zoomed out the
     *                    camera will appear
     * @param screenHeight visible screen height (NOT necessarily pixels); the larger the value, the more zoomed out the
     *                    camera will appear
     */
    public Camera(int screenWidth, int screenHeight) {
        this.positionMatrix = new Vector2f(0, 0);
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection(screenWidth, screenHeight);
        adjustView();
    }


    // METHODS
    /**
     * Adjusts the projection matrix.
     * The passed visible screen size (width and height) will be applied.
     *
     * @param screenWidth visible screen width (NOT necessarily pixels); the larger the value, the more zoomed out the
     *                    camera will appear
     * @param screenHeight visible screen height (NOT necessarily pixels); the larger the value, the more zoomed out the
     *                    camera will appear
     */
    public void adjustProjection(int screenWidth, int screenHeight) {

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        projectionMatrix.identity();                                                                                    // Sets the projection matrix to equal the identity matrix.
        projectionMatrix.ortho(0.0f, (float)screenWidth, (float)screenHeight, 0.0f, 0.0f, 100.0f);                      // Screen coordinate (0, 0) is defined at the top-left; note that this flips everything rendered on screen in the y-direction.
    }


    /**
     * Adjusts the view matrix.
     * The current position matrix will be applied.
     */
    public void adjustView() {

        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);                                                         // Camera pointing in -1 of the z-direction.
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();                                                                                          // Modifies the view matrix directly
        viewMatrix.lookAt(new Vector3f(positionMatrix.x, positionMatrix.y, 20.0f),
                cameraFront.add(positionMatrix.x, positionMatrix.y, 0.0f),
                cameraUp);                                                                                              // Modifies the view matrix directly.
    }


    /**
     * Adjusts the position matrix.
     *
     * @param position system camera position (top-left coordinate)
     */
    public void adjustPosition(Vector2f position) {

        this.positionMatrix.set(position);
        adjustView();
    }


    /**
     * Retrieves the projection matrix.
     *
     * @return perspective matrix
     */
    public Matrix4f getProjectionMatrix() {

        return projectionMatrix;
    }


    /**
     * Retrieves the view matrix.
     *
     * @return view matrix
     */
    public Matrix4f getViewMatrix() {

        return viewMatrix;
    }


    /**
     * Retrieves the position matrix.
     *
     * @return position matrix
     */
    public Vector2f getPositionMatrix() {

        return positionMatrix;
    }


    /**
     * Converts screen coordinates to world coordinates.
     * For example, if a UI element is desired to always be placed at a certain point in the viewport (i.e., on the
     * visible screen), then this method will convert that desired position to world coordinates so that it is rendered
     * in the correct place.
     *
     * @param screenCoords normalized screen coordinates (leftmost x and topmost y), where (0, 0)
     *                     represents the top-left corner of the screen, (1, 1) the bottom-right
     * @return world coordinates (leftmost x and topmost y)
     */
    public Vector2f screenCoordsToWorldCoords(Vector2f screenCoords) {

        float worldX = positionMatrix.x + (screenCoords.x * screenWidth);
        float worldY = positionMatrix.y + (screenCoords.y * screenHeight);
        return new Vector2f(worldX, worldY);
    }


    /**
     * Converts world coordinates to screen coordinates.
     * For example, if a UI element is desired to always be placed at a certain point in the world (i.e., somewhere on
     * the loaded map), then this method will convert that desired position to screen coordinates so that it can be
     * known where said position lays in respect to the viewport.
     *
     * @param worldCoords world coordinates (leftmost x and topmost y)
     * @return normalized screen coordinates (leftmost x and topmost y), where (0, 0) represents the top-left corner of
     *         the screen, (1, 1) the bottom-right
     */
    public Vector2f worldCoordsToScreenCoords(Vector2f worldCoords) {

        float screenX = (worldCoords.x - positionMatrix.x) / screenWidth;
        float screenY = (worldCoords.y - positionMatrix.y) / screenHeight;
        return new Vector2f(screenX, screenY);
    }


    /**
     * Converts a screen width to world width.
     * For example, if a UI element is meant to always span a certain length of the viewport (i.e., on the visible
     * screen), then this method will convert that desired length to a world length so that it is always rendered at the
     * correct length.
     *
     * @param width normalized screen width, where 0 is no length and 1 is the full screen width.
     * @return world width
     */
    public float screenWidthToWorldWidth(float width) {

        return width * screenWidth;
    }


    /**
     * Converts a screen height to world height.
     * For example, if a UI element is meant to always span a certain length of the viewport (i.e., on the visible
     * screen), then this method will convert that desired length to a world length so that it is always rendered at the
     * correct length.
     *
     * @param height normalized screen height, where 0 is no length and 1 is the full screen height.
     * @return world height
     */
    public float screenHeightToWorldHeight(float height) {

        return height * screenHeight;
    }


    /**
     * Converts a world width to screen width.
     *
     * @param width world width
     * @return normalized screen width
     */
    public float worldWidthToScreenWidth(float width) {

        return width / screenWidth;
    }


    /**
     * Converts a world height to screen height.
     *
     * @param height world height
     * @return normalized screen height
     */
    public float worldHeightToScreenHeight(float height) {

        return height / screenHeight;
    }


    // GETTERS
    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
