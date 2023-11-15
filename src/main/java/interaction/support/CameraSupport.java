package interaction.support;

import core.GamePanel;
import entity.EntityBase;
import org.joml.Vector2f;

/**
 * This class contains methods to facilitate camera movement.
 */
public class CameraSupport {

    // BASIC FIELD
    private final GamePanel gp;


    // CAMERA TRACK FIELDS
    /**
     * Entity being tracked/followed by the camera.
     * When updated, the camera will position itself to focus on this entity at its center.
     */
    private EntityBase trackedEntity;

    /**
     * Boolean indicating whether to override (i.e., stop) tracking the tracked entity (true) or not (false).
     */
    private boolean overrideEntityTracking = false;


    // CAMERA SCROLL FIELDS
    /**
     * Boolean indicating whether a camera scroll effect is current in effect (true) or not (false).
     */
    private boolean cameraScrolling = false;

    /**
     * Variable to store the starting camera world position (center of camera) of a camera scroll effect.
     */
    private float startingWorldX, startingWorldY;

    /**
     * Variable to store the target camera world position (center of camera) of a camera scroll effect.
     */
    private float targetWorldX, targetWorldY;

    /**
     * Variable to store the difference between the target and starting camera world position (center of camera).
     */
    private float differenceWorldX, differenceWorldY;

    /**
     * Variable to track the progress of a scrolling effect.
     * In other words the current center of the camera is stored.
     */
    private float currentWorldX, currentWorldY;

    private float leftoverWorldX, leftoverWorldY;

    /**
     * Variable to store the calculated number of frames that a camera scroll effect will take to complete.
     */
    private int totalFrames;

    /**
     * Variable to count the number of frames that have passed so far during a camera scroll effect.
     */
    private int frameCount;

    /**
     * Variable to store the calculated camera scroll speed.
     */
    private float speedX, speedY;


    // CONSTRUCTOR
    /**
     * Constructs a CameraSupport instance.
     *
     * @param gp GamePanel instance
     */
    public CameraSupport(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Updates the state of any active camera effects by one frame.
     * This includes entity tracking; if the tracked entity is null, this effect will do nothing.
     */
    public void update() {

        if (cameraScrolling) {

            updateCameraScroll();
        } else if ((trackedEntity != null) && (!overrideEntityTracking)) {

            float centerScreenX = (gp.getCamera().getScreenWidth() / 2) - (gp.getNativeTileSize() / 2);
            float centerScreenY = gp.getCamera().getScreenHeight() / 2;
            gp.getCamera().adjustPosition(
                    new Vector2f(trackedEntity.getWorldX() - centerScreenX,
                    trackedEntity.getWorldY() - centerScreenY
                    )
            );
        }
    }


    /**
     * Sets the camera to center on and track an entity.
     * Each time the camera is updated, this will override any manual change made to the camera's position (position
     * matrix) unless the tracked entity is null or tracking is overridden.
     *
     * @param entity entity to track
     */
    public void setTrackedEntity(EntityBase entity) {

        this.trackedEntity = entity;
    }


    /**
     * Snaps the camera back to center on the tracked entity.
     * If the tracked entity is null or a camera scroll effect is in effect, then nothing will happen.
     * Any override on the tracked entity will be removed.
     */
    public void resetCameraSnap() {

        if ((!cameraScrolling) && (trackedEntity != null)) {

            float centerScreenX = ((float)gp.getCamera().getScreenWidth() / 2) - ((float)gp.getNativeTileSize() / 2);
            float centerScreenY = (float)gp.getCamera().getScreenHeight() / 2;
            gp.getCamera().adjustPosition(
                    new Vector2f(
                            (float)trackedEntity.getWorldX() - centerScreenX,
                            (float)trackedEntity.getWorldY() - centerScreenY
                    )
            );
            overrideEntityTracking = false;
        }
    }


    /**
     * Scrolls the camera back to center on the tracked entity.
     * If the tracked entity is null or a camera scroll effect is in effect, then nothing will happen.
     * Any override on the tracked entity will be removed.
     *
     * @param speed speed at which to scroll the camera; minimum allowed is 1, maximum is 20
     * @throws IllegalArgumentException if an illegal speed is passed as argument
     */
    public void resetCameraScroll(float speed) {

        if ((!cameraScrolling) && (trackedEntity != null) && (speed > 0) && (speed <= 20)) {

            float centerScreenX = ((float)gp.getCamera().getScreenWidth() / 2) - ((float)gp.getNativeTileSize() / 2);
            float centerScreenY = (float)gp.getCamera().getScreenHeight() / 2;
            setCameraScroll(
                    (float)trackedEntity.getWorldX() - centerScreenX,
                    (float)trackedEntity.getWorldY() - centerScreenY,
                    speed
            );
            overrideEntityTracking = false;
        } else if ((speed < 1) || (speed > 20)) {

            throw new IllegalArgumentException("Attempted to set a camera scroll speed outside of bounds 1 - 20 (both inclusive)");
        }
    }


    /**
     * Snaps the camera to center on a specified world position.
     * An override on a tracked entity will be put into place.
     *
     * @param worldX world position (center of camera) to snap the camera to (x)
     * @param worldY world position (center of camera) to snap the camera to (y)
     */
    public void setCameraSnap(float worldX, float worldY) {

        if (!cameraScrolling) {

            gp.getCamera().adjustPosition(
                    new Vector2f(
                            worldX - ((float)gp.getCamera().getScreenWidth() / 2),
                            worldY - ((float)gp.getCamera().getScreenHeight() / 2)
                    )
            );
            overrideEntityTracking = true;
        }
    }


    /**
     * Scrolls the camera to center on a specified world position.
     * If a camera scroll effect is already in effect, then nothing will happen.
     * An override on a tracked entity will be put into place.
     *
     * @param worldX world position (center of camera) to scroll the camera to (x)
     * @param worldY world position (center of camera) to scroll the camera to (y)
     * @param speed speed at which to scroll the camera; minimum allowed is 1, maximum is 20
     * @throws IllegalArgumentException if an illegal speed is passed as argument
     */
    public void setCameraScroll(float worldX, float worldY, float speed) {

        if ((!cameraScrolling) && (speed > 0) && (speed <= 20)) {

            cameraScrolling = true;

            startingWorldX = gp.getCamera().getPositionMatrix().x + ((float)gp.getCamera().getScreenWidth() / 2);
            startingWorldY = gp.getCamera().getPositionMatrix().y + ((float)gp.getCamera().getScreenHeight() / 2);

            targetWorldX = worldX;
            targetWorldY = worldY;

            differenceWorldX = targetWorldX - startingWorldX;
            differenceWorldY = targetWorldY - startingWorldY;

            if (differenceWorldX > differenceWorldY) {

                if (targetWorldX < startingWorldX) {

                    speedX = -speed;
                } else {

                    speedX = speed;
                }
                totalFrames = Math.abs((int)Math.ceil(differenceWorldX / speedX));
                leftoverWorldX = differenceWorldX % speedX;
                speedY = differenceWorldY / totalFrames;
            } else {

                if (targetWorldY < startingWorldY) {

                    speedY = -speed;
                } else {

                    speedY = speed;
                }
                totalFrames = Math.abs((int)Math.ceil(differenceWorldY / speedY));
                leftoverWorldY = differenceWorldY % speedY;
                speedX = differenceWorldX / totalFrames;
            }

            currentWorldX = startingWorldX;
            currentWorldY = startingWorldY;

            overrideEntityTracking = true;
        } else if ((speed < 1) || (speed > 20)) {

            throw new IllegalArgumentException("Attempted to set a camera scroll speed outside of bounds 1 - 20 (both inclusive)");
        }
    }


    /**
     * Updates the camera scroll effect by one frame if in effect.
     */
    private void updateCameraScroll() {

        if (cameraScrolling) {

            if (frameCount == (totalFrames - 1)) {

                float cameraWorldX = targetWorldX - ((float)gp.getCamera().getScreenWidth() / 2);
                float cameraWorldY = targetWorldY - ((float)gp.getCamera().getScreenHeight() / 2);
                gp.getCamera().adjustPosition(new Vector2f(cameraWorldX, cameraWorldY));
                reset();
            } else {

                currentWorldX += speedX;
                currentWorldY += speedY;
                float cameraWorldX = currentWorldX - ((float)gp.getCamera().getScreenWidth() / 2);
                float cameraWorldY = currentWorldY - ((float)gp.getCamera().getScreenHeight() / 2);
                gp.getCamera().adjustPosition(new Vector2f(cameraWorldX, cameraWorldY));
                frameCount++;
            }
        }
    }


    /**
     * Resets CameraSupport back to its default state.
     * Intended to be called to clean up after a camera scroll effect has finished.
     */
    private void reset() {

        cameraScrolling = false;
        startingWorldX = 0;
        startingWorldY = 0;
        targetWorldX = 0;
        targetWorldY = 0;
        differenceWorldX = 0;
        differenceWorldY = 0;
        currentWorldX = 0;
        currentWorldY = 0;
        totalFrames = 0;
        frameCount = 0;
        speedX = 0;
        speedY = 0;
    }


    // GETTERS
    public EntityBase getTrackedEntity() {
        return trackedEntity;
    }

    public boolean isOverrideEntityTracking() {
        return overrideEntityTracking;
    }

    public boolean isCameraScrolling() {
        return cameraScrolling;
    }


    // SETTERS
    public void setOverrideEntityTracking(boolean overrideEntityTracking) {
        this.overrideEntityTracking = overrideEntityTracking;
    }
}
