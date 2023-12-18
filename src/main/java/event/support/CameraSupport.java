package event.support;

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
    private float startWorldX, startWorldY;

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

    /**
     * Variable to count the number of seconds that have passed so far during a camera scroll effect.
     */
    private double durationCount;

    /**
     * Variable to store the total number of seconds that a camera scroll effect is set to last for.
     */
    private double durationTotal;


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
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        if (cameraScrolling) {

            updateCameraScroll(dt);
        } else if ((trackedEntity != null) && (!overrideEntityTracking)) {

            float centerScreenX = ((float)gp.getCamera().getScreenWidth() / 2) - ((float)gp.getNativeTileSize() / 2);
            float centerScreenY = (float)gp.getCamera().getScreenHeight() / 2;
            gp.getCamera().adjustPosition(
                    new Vector2f(
                            trackedEntity.getWorldX() - centerScreenX,
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
     * @param duration duration (seconds) of the camera scroll; minimum allowed is 1, maximum is 20
     * @throws IllegalArgumentException if an illegal speed is passed as argument
     */
    public void resetCameraScroll(float duration) {

        if ((!cameraScrolling) && (trackedEntity != null) && (duration > 0) && (duration <= 20)) {

            setCameraScroll(
                    trackedEntity.getWorldX() + ((float)gp.getNativeTileSize() / 2),
                    trackedEntity.getWorldY(),
                    duration
            );
            overrideEntityTracking = false;
        } else if ((duration < 1) || (duration > 20)) {

            throw new IllegalArgumentException("Attempted to set a camera scroll duration outside of bounds 1 - 20 (both inclusive)");
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
     * @param duration duration (seconds) of the camera scroll; minimum allowed is 1, maximum is 20
     * @throws IllegalArgumentException if an illegal speed is passed as argument
     */
    public void setCameraScroll(float worldX, float worldY, float duration) {

        if ((!cameraScrolling) && (duration > 0) && (duration <= 20)) {

            cameraScrolling = true;
            durationTotal = duration;

            startWorldX = gp.getCamera().getPositionMatrix().x + ((float)gp.getCamera().getScreenWidth() / 2);
            startWorldY = gp.getCamera().getPositionMatrix().y + ((float)gp.getCamera().getScreenHeight() / 2);

            targetWorldX = worldX;
            targetWorldY = worldY;

            differenceWorldX = targetWorldX - startWorldX;
            differenceWorldY = targetWorldY - startWorldY;

            currentWorldX = startWorldX;
            currentWorldY = startWorldY;

            overrideEntityTracking = true;
        } else if ((duration < 1) || (duration > 20)) {

            throw new IllegalArgumentException("Attempted to set a camera scroll duration outside of bounds 1 - 20 (both inclusive)");
        }
    }


    /**
     * Updates the camera scroll effect by one frame if in effect.
     *
     * @param dt time since last frame (seconds)
     */
    private void updateCameraScroll(double dt) {

        if (cameraScrolling) {

            if (durationCount > durationTotal) {

                float cameraWorldX = targetWorldX - ((float)gp.getCamera().getScreenWidth() / 2);
                float cameraWorldY = targetWorldY - ((float)gp.getCamera().getScreenHeight() / 2);
                gp.getCamera().adjustPosition(new Vector2f(cameraWorldX, cameraWorldY));
                reset();
            } else {

                currentWorldX = startWorldX + (float)(durationCount / durationTotal) * differenceWorldX;
                currentWorldY = startWorldY + (float)(durationCount / durationTotal) * differenceWorldY;
                float cameraWorldX = currentWorldX - ((float)gp.getCamera().getScreenWidth() / 2);
                float cameraWorldY = currentWorldY - ((float)gp.getCamera().getScreenHeight() / 2);
                gp.getCamera().adjustPosition(new Vector2f(cameraWorldX, cameraWorldY));
                durationCount += dt;
            }
        }
    }


    /**
     * Resets CameraSupport back to its default state.
     * Intended to be called to clean up after a camera scroll effect has finished.
     */
    private void reset() {

        cameraScrolling = false;
        startWorldX = 0;
        startWorldY = 0;
        targetWorldX = 0;
        targetWorldY = 0;
        differenceWorldX = 0;
        differenceWorldY = 0;
        currentWorldX = 0;
        currentWorldY = 0;
        durationCount = 0;
        durationTotal = 0;
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
