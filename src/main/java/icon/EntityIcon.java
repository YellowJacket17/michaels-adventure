package icon;

import asset.Sprite;
import render.drawable.Drawable;

/**
 * This class defines icons created for entities.
 * These elements are primarily used in menus.
 */
public class EntityIcon extends Drawable {

    // FIELDS
    /**
     * ID of the entity that this is an icon of.
     */
    private final int entityId;

    /**
     * Icon sprite.
     */
    private Sprite idleDown, walkDown1, walkDown2;

    /**
     * Boolean tracking whether the current icon is in a selected state or not.
     * When in a selected state, this icon will animate.
     */
    private boolean selected;

    /**
     * Core time counter (seconds) for controlling animation.
     */
    private double counter;

    /**
     * Maximum allowed value for core animation time counter (seconds).
     */
    private final double counterMax = 0.8;


    // CONSTRUCTOR
    /**
     * Constructs an EntityIcon instance.
     *
     * @param entityId ID of the entity to create an icon of.
     */
    public EntityIcon(int entityId) {
        super();
        this.entityId = entityId;
    }


    /**
     * Updates the state of this entity icon by one frame if selected.
     * A down walking animation is rendered if selected.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        if (selected) {

            counter += dt;

            while (counter > counterMax) {

                counter -= counterMax;
            }

            if (counter <= 0.2) {
                sprite = idleDown;

            } else if (counter <= 0.4) {
                sprite = walkDown1;

            } else if (counter <= 0.6) {
                sprite = idleDown;

            } else if (counter <= 0.8) {
                sprite = walkDown2;

            } else {
                sprite = idleDown;
            }
        }
    }


    // GETTERS
    public int getEntityId() {
        return entityId;
    }

    public boolean isSelected() {
        return selected;
    }


    // SETTERS
    public void setIdleDown(Sprite idleDown) {
        this.idleDown = idleDown;
    }

    public void setWalkDown1(Sprite walkDown1) {
        this.walkDown1 = walkDown1;
    }

    public void setWalkDown2(Sprite walkDown2) {
        this.walkDown2 = walkDown2;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (!selected) {
            counter = 0;
            sprite = idleDown;
        }
    }
}
