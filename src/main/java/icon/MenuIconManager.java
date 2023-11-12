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
 * This class instantiates icons, stores them, and handles related operations.
 */
public class MenuIconManager {

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
    private final HashMap<Integer, MenuIcon> icons = new HashMap<>();

    /**
     * Set to store icon draw errors. If an icon draw error occurs, the ID associated with the icon will be added to
     * this set. This prevents a draw error from that icon being printed to the console again.
     */
    private final Set<Integer> drawErrors = new HashSet<>();


    // CONSTRUCTOR
    /**
     * Constructs a MenuIconManager instance.
     *
     * @param gp GamePanel instance
     */
    public MenuIconManager(GamePanel gp) {
        this.gp = gp;
        loadIcons();
    }


    // METHODS
    /**
     * Draws a menu icon by ID.
     *
     * @param renderer Renderer instance
     * @param iconId ID of the icon to be drawn
     * @param screenX x-coordinate (leftmost side of the image) where the icon will be drawn
     * @param screenY y-coordinate (topmost side of the image) where the icon will be drawn
     */
    public void draw(Renderer renderer, int iconId, int screenX, int screenY) {

        MenuIcon menuIcon = icons.get(iconId);

        if (menuIcon != null) {

            Sprite sprite;

            if (menuIcon.isSelected()) {

                sprite = menuIcon.getActive();
            } else {

                sprite = menuIcon.getInactive();
            }

            if (sprite != null) {

                menuIcon.transform.position.x = screenX;
                menuIcon.transform.position.x = screenY;
                renderer.addDrawable(menuIcon);
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
     * Retrieves a menu icon by ID.
     *
     * @param iconId ID of the icon to be retrieved
     * @return icon
     */
    public MenuIcon getIconById(int iconId) {

        MenuIcon menuIcon = icons.get(iconId);

        if (menuIcon == null) {

            UtilityTool.logWarning("Attempted to retrieve an icon with ID "
                    + iconId
                    + " that does not exist.");

            menuIcon = new MenuIcon(-1);                                                                                // Return a placeholder icon to prevent the program from hitting an unhandled exception.
        }
        return menuIcon;
    }


    /**
     * Stages icon sprites to be loaded from resources directory.
     * In other words, this is the method that instantiates menu icons for the game.
     */
    private void loadIcons() {

        MenuIcon menuIcon;

        // Party menu icon - Icon ID 0.
        menuIcon = new MenuIcon(0);
        menuIcon.setName("Party menu");
        menuIcon.setActive(AssetPool.getSpritesheet(5).getSprite(5));
        menuIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(6));
        icons.put(menuIcon.getIconId(), menuIcon);

        // Inventory menu icon - Icon ID 1.
        menuIcon = new MenuIcon(1);
        menuIcon.setName("Inventory menu");
        menuIcon.setActive(AssetPool.getSpritesheet(5).getSprite(7));
        menuIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(8));
        icons.put(menuIcon.getIconId(), menuIcon);

        // Settings menu icon - Icon ID 2.
        menuIcon = new MenuIcon(2);
        menuIcon.setName("Settings menu");
        menuIcon.setActive(AssetPool.getSpritesheet(5).getSprite(9));
        menuIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(10));
        icons.put(menuIcon.getIconId(), menuIcon);

        // Character summary icon 1 - Icon ID 3.
        menuIcon = new MenuIcon(3);
        menuIcon.setName("Character summary 1");
        menuIcon.setActive(AssetPool.getSpritesheet(5).getSprite(0));
        menuIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(1));
        icons.put(menuIcon.getIconId(), menuIcon);

        // Character summary icon 2 - Icon ID 4.
        menuIcon = new MenuIcon(4);
        menuIcon.setName("Character summary 2");
        menuIcon.setActive(AssetPool.getSpritesheet(5).getSprite(0));
        menuIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(1));
        icons.put(menuIcon.getIconId(), menuIcon);

        // Character summary icon 3 - Icon ID 5.
        menuIcon = new MenuIcon(5);
        menuIcon.setName("Character summary 3");
        menuIcon.setActive(AssetPool.getSpritesheet(5).getSprite(0));
        menuIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(1));
        icons.put(menuIcon.getIconId(), menuIcon);

        // Item menu backdrop - Icon ID 6.
        menuIcon = new MenuIcon(6);
        menuIcon.setName("Item menu stackable backdrop");
        menuIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(3));
        icons.put(menuIcon.getIconId(), menuIcon);

        // Item menu backdrop - Icon ID 7.
        menuIcon = new MenuIcon(7);
        menuIcon.setName("Item menu non-stackable backdrop");
        menuIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(4));
        icons.put(menuIcon.getIconId(), menuIcon);

        // Item menu selector - Icon ID 8.
        menuIcon = new MenuIcon(8);
        menuIcon.setName("Item menu selector");
        menuIcon.setInactive(AssetPool.getSpritesheet(5).getSprite(2));
        icons.put(menuIcon.getIconId(), menuIcon);
    }
}
