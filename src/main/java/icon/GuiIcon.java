package icon;

import asset.Sprite;
import render.drawable.Drawable;

/**
 * This class defines GUI icons created for the game.
 * These elements are primarily used in menus.
 */
public class GuiIcon extends Drawable {

    // FIELDS
    /**
     * Unique icon ID.
     * To be clear, this ID is unique for each GuiIcon instance across the entire game.
     */
    private final int iconId;

    /**
     * Icon name (means of identifying it).
     */
    private String name;

    /**
     * Icon spite.
     */
    private Sprite active, inactive;

    /**
     * Boolean tracking whether the current icon is in a selected state or not. When in a selected state, this icon will
     * display its active sprite.  When not in a selected state, this icon will display its inactive sprite.
     */
    private boolean selected;


    // CONSTRUCTOR
    /**
     * Constructs a GuiIcon instance.
     *
     * @param iconId unique icon ID
     */
    public GuiIcon(int iconId) {
        super();
        this.iconId = iconId;
    }


    // GETTERS
    public int getIconId() {
        return iconId;
    }

    public String getName() {
        return name;
    }

    public Sprite getActive() {
        return active;
    }


    // SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setActive(Sprite active) {
        this.active = active;
        if (selected) {
            sprite = this.active;
        }
    }

    public void setInactive(Sprite inactive) {
        this.inactive = inactive;
        if (!selected) {
            sprite = this.inactive;
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            sprite = active;
        } else {
            sprite = inactive;
        }
    }
}
