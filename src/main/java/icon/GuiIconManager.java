package icon;

import core.GamePanel;
import render.Renderer;
import render.Sprite;
import utility.AssetPool;
import utility.UtilityTool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class instantiates GUI icons, stores them, and handles related operations.
 */
public class GuiIconManager {

    /*
     * WARNING: Attempting to instantiate an icon with an ID that's already in use overwrite the existing in the HashMap.
     *
     * This class loads and stores menu icons.
     * Icons are loaded in their native aspect ratios.
     */

    // FIELDS
    private final GamePanel gp;

    /**
     * Map to store icons; icon ID is the key, icon is the value
     */
    private final HashMap<Integer, GuiIcon> icons = new HashMap<>();

    /**
     * Set to store icon draw errors. If an icon draw error occurs, the ID associated with the icon will be added to
     * this set. This prevents a draw error from that icon being printed to the console again.
     */
    private final Set<Integer> drawErrors = new HashSet<>();


    // CONSTRUCTOR
    /**
     * Constructs a GuiIconManager instance.
     *
     * @param gp GamePanel instance
     */
    public GuiIconManager(GamePanel gp) {
        this.gp = gp;
        loadIcons();
    }


    // METHODS
    /**
     * Draws a GUI icon by ID.
     *
     * @param renderer Renderer instance
     * @param iconId ID of the icon to be drawn
     * @param screenX x-coordinate (leftmost side of the image) where the icon will be drawn
     * @param screenY y-coordinate (topmost side of the image) where the icon will be drawn
     */
    public void draw(Renderer renderer, int iconId, int screenX, int screenY) {

        GuiIcon guiIcon = icons.get(iconId);

        if (guiIcon != null) {

            Sprite sprite;

            if (guiIcon.isSelected()) {

                sprite = guiIcon.getActive();
            } else {

                sprite = guiIcon.getInactive();
            }

            if (sprite != null) {

                guiIcon.transform.position.x = screenX;
                guiIcon.transform.position.x = screenY;
                renderer.addDrawable(guiIcon);
            } else if (!drawErrors.contains(iconId)) {

                UtilityTool.logError("Failed to draw icon"
                        + " with ID "
                        + iconId
                        + ": images may not have been properly loaded upon icon initialization.");
                drawErrors.add(iconId);
            }
        }
    }


    /**
     * Retrieves a GUI icon by ID.
     *
     * @param iconId ID of the icon to be retrieved
     * @return icon
     */
    public GuiIcon getIconById(int iconId) {

        GuiIcon guiIcon = icons.get(iconId);

        if (guiIcon == null) {

            UtilityTool.logWarning("Attempted to retrieve an icon with ID "
                    + iconId
                    + " that does not exist.");

            guiIcon = new GuiIcon(-1);                                                                                // Return a placeholder icon to prevent the program from hitting an unhandled exception.
        }
        return guiIcon;
    }


    /**
     * Stages icon sprites to be loaded from resources directory.
     * In other words, this is the method that instantiates menu icons for the game.
     */
    private void loadIcons() {

        GuiIcon guiIcon;

        // Party menu icon - Icon ID 0.
        guiIcon = new GuiIcon(0);
        guiIcon.setName("Party menu");
        guiIcon.setActive(AssetPool.getSpritesheet(5).getSprite(5));
        guiIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(6));
        icons.put(guiIcon.getIconId(), guiIcon);

        // Inventory menu icon - Icon ID 1.
        guiIcon = new GuiIcon(1);
        guiIcon.setName("Inventory menu");
        guiIcon.setActive(AssetPool.getSpritesheet(5).getSprite(7));
        guiIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(8));
        icons.put(guiIcon.getIconId(), guiIcon);

        // Settings menu icon - Icon ID 2.
        guiIcon = new GuiIcon(2);
        guiIcon.setName("Settings menu");
        guiIcon.setActive(AssetPool.getSpritesheet(5).getSprite(9));
        guiIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(10));
        icons.put(guiIcon.getIconId(), guiIcon);

        // Character summary icon 1 - Icon ID 3.
        guiIcon = new GuiIcon(3);
        guiIcon.setName("Character summary 1");
        guiIcon.setActive(AssetPool.getSpritesheet(5).getSprite(0));
        guiIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(1));
        icons.put(guiIcon.getIconId(), guiIcon);

        // Character summary icon 2 - Icon ID 4.
        guiIcon = new GuiIcon(4);
        guiIcon.setName("Character summary 2");
        guiIcon.setActive(AssetPool.getSpritesheet(5).getSprite(0));
        guiIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(1));
        icons.put(guiIcon.getIconId(), guiIcon);

        // Character summary icon 3 - Icon ID 5.
        guiIcon = new GuiIcon(5);
        guiIcon.setName("Character summary 3");
        guiIcon.setActive(AssetPool.getSpritesheet(5).getSprite(0));
        guiIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(1));
        icons.put(guiIcon.getIconId(), guiIcon);

        // Item menu backdrop - Icon ID 6.
        guiIcon = new GuiIcon(6);
        guiIcon.setName("Item menu stackable backdrop");
        guiIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(3));
        icons.put(guiIcon.getIconId(), guiIcon);

        // Item menu backdrop - Icon ID 7.
        guiIcon = new GuiIcon(7);
        guiIcon.setName("Item menu non-stackable backdrop");
        guiIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(4));
        icons.put(guiIcon.getIconId(), guiIcon);

        // Item menu selector - Icon ID 8.
        guiIcon = new GuiIcon(8);
        guiIcon.setName("Item menu selector");
        guiIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(2));
        icons.put(guiIcon.getIconId(), guiIcon);
    }
}
