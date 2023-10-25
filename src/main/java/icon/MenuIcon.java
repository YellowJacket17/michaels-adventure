package icon;

import java.awt.image.BufferedImage;

/**
 * This class defines menu icons created for the game.
 */
public class MenuIcon {

    // FIELDS
    /**
     * Unique icon ID.
     * To be clear, this ID is unique for each MenuIcon instance across the entire game.
     */
    private final int iconId;

    /**
     * Icon name (means of identifying it).
     */
    private String name;

    /**
     * Icon spite.
     */
    private BufferedImage active, inactive;

    /**
     * Boolean tracking whether the current icon is in a selected state or not. When in a selected state, this icon will
     * display its active sprite.  When not in a selected state, this icon will display its inactive sprite.
     */
    private boolean selected;


    // CONSTRUCTOR
    /**
     * Constructs a MenuIcon instance.
     *
     * @param iconId unique icon ID
     */
    public MenuIcon(int iconId) {
        this.iconId = iconId;
    }


    // GETTERS
    public int getIconId() {
        return iconId;
    }

    public String getName() {
        return name;
    }

    public BufferedImage getActive() {
        return active;
    }

    public BufferedImage getInactive() {
        return inactive;
    }

    public boolean isSelected() {
        return selected;
    }


    // SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setActive(BufferedImage active) {
        this.active = active;
    }

    public void setInactive(BufferedImage inactive) {
        this.inactive = inactive;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
