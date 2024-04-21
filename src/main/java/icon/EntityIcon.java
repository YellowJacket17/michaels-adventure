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
    private Sprite down1, down2, down3;

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
     * Adds frame time to this animation counter value.
     *
     * @param dt time since last frame (seconds)
     */
    public void iterateCounter(double dt) {

        counter += dt;
    }

    /**
     * Subtracts the maximum allowed animation counter value from this animation counter value.
     * If this counter value is less than the maximum allowed value, then this counter value will be set to zero.
     */
    public void rollbackCounter() {

        if (counter >= counterMax) {

            counter -= counterMax;
        } else {

            counter = 0;
        }
    }

    /**
     * Resets this animation counter back to zero.
     */
    public void resetCounter() {

        counter = 0;
    }


    // GETTERS
    public int getEntityId() {
        return entityId;
    }

    public Sprite getDown1() {
        return down1;
    }

    public Sprite getDown2() {
        return down2;
    }

    public Sprite getDown3() {
        return down3;
    }

    public boolean isSelected() {
        return selected;
    }

    public double getCounter() {
        return counter;
    }

    public double getCounterMax() {
        return counterMax;
    }


    // SETTERS
    public void setDown1(Sprite down1) {
        this.down1 = down1;
    }

    public void setDown2(Sprite down2) {
        this.down2 = down2;
    }

    public void setDown3(Sprite down3) {
        this.down3 = down3;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (!selected) {
            resetCounter();
        }
    }
}
