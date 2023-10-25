package icon;

import java.awt.image.BufferedImage;

/**
 * This class defines icons created for entities.
 */
public class EntityIcon {

    // FIELDS
    /**
     * ID of the entity that this is an icon of.
     */
    private final int entityId;

    /**
     * Icon sprite.
     */
    private BufferedImage down1, down2, down3;

    /**
     * Boolean tracking whether the current icon is in a selected state or not.
     * When in a selected state, this icon will animate.
     */
    private boolean selected;

    /**
     * Variable to control animation when this icon is in a selected state.
     */
    private int animationCounter;


    // CONSTRUCTOR
    /**
     * Constructs an EntityIcon instance.
     *
     * @param entityId ID of the entity to create an icon of.
     */
    public EntityIcon(int entityId) {
        this.entityId = entityId;
    }


    // GETTERS
    public int getEntityId() {
        return entityId;
    }

    public BufferedImage getDown1() {
        return down1;
    }

    public BufferedImage getDown2() {
        return down2;
    }

    public BufferedImage getDown3() {
        return down3;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getAnimationCounter() {
        return animationCounter;
    }


    // SETTERS
    public void setDown1(BufferedImage down1) {
        this.down1 = down1;
    }

    public void setDown2(BufferedImage down2) {
        this.down2 = down2;
    }

    public void setDown3(BufferedImage down3) {
        this.down3 = down3;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (!selected) {
            animationCounter = 0;
        }
    }

    public void setAnimationCounter(int animationCounter) {
        this.animationCounter = animationCounter;
    }
}
