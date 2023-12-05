package icon;

import entity.EntityBase;
import core.GamePanel;
import org.joml.Vector2f;
import render.Renderer;
import render.Sprite;
import render.ZIndex;
import utility.UtilityTool;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class instantiates entity icons, stores them, and handles related operations.
 */
public class EntityIconManager {

    /*
     * This class creates and stores entity icons.
     * Entity icons are meant to be a temporary icons created to represent an entity in a menu or similar screen.
     * It's a way of displaying an image of an entity in a menu without using an actual instance of that entity.
     */

    // FIELDS
    private final GamePanel gp;

    /**
     * Map to store entity icons; entity ID is the key, entity icon is the value.
     */
    private final HashMap<Integer, EntityIcon> entityIcons  = new HashMap<>();

    /**
     * Set to store icon render errors.
     * If an icon render error occurs, the ID associated with the icon will be added to this set.
     * This prevents a render error from that icon being printed to the console again.
     */
    private final HashSet<Integer> renderErrors = new HashSet<>();


    // CONSTRUCTOR
    /**
     * Constructs an EntityIconManager instance.
     *
     * @param gp GamePanel instance
     */
    public EntityIconManager(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Adds an entity icon to the render pipeline.
     *
     * @param renderer Renderer instance
     * @param entityId ID of the entity for whom the entity icon will be rendered
     * @param screenX screen x-coordinate of the icon (leftmost, normalized between 0 and 1)
     * @param screenY screen y-coordinate of the icon (topmost, normalized between 0 and 1)
     */
    public void addToRenderPipeline(Renderer renderer, int entityId, float screenX, float screenY) {

        EntityIcon entityIcon = entityIcons.get(entityId);

        if (entityIcon != null) {

            Sprite sprite;

            if (entityIcon.isSelected()) {

                sprite = selectEntitySprite(entityIcon);
            } else {

                sprite = entityIcon.getDown1();
            }

            if (sprite != null) {

                Vector2f worldCoords = gp.getCamera().screenCoordsToWorldCoords(new Vector2f(screenX, screenY));
                entityIcon.transform.position.x = worldCoords.x;
                entityIcon.transform.position.y = worldCoords.y;
                entityIcon.transform.scale.x = sprite.getNativeWidth();
                entityIcon.transform.scale.y = sprite.getNativeHeight();
                entityIcon.setSprite(sprite);
                renderer.addDrawable(entityIcon, ZIndex.FIRST_LAYER);
            } else if (!renderErrors.contains(entityId)) {

                UtilityTool.logError("Failed to draw entity icon with entity ID "
                        + entityId
                        + " to the render pipeline: the entity icon may not have been loaded properly or may not exist.");
                renderErrors.add(entityId);
            }
        }
    }


    /**
     * Creates entity icons for the player entity and all party member entities.
     */
    public void createPartyEntityIcons() {

        // Create an entity icon of the player.
        addEntityIcon(gp.getPlayer().getEntityId());

        // Create an entity icon for each part member.
        for (EntityBase entity : gp.getParty().values()) {
            if (entity != null) {
                addEntityIcon(entity.getEntityId());
            }
        }
    }


    /**
     * Instantiates an entity icon for an entity by entity ID.
     *
     * @param entityId ID of the entity for whom an entity icon will be created
     */
    public void addEntityIcon(int entityId) {

        // TODO : Possibly add error (try/catch) if an entity has a null `down1` image.

        EntityBase entity = gp.getEntityById(entityId);

        if (entity != null) {

            EntityIcon entityIcon = new EntityIcon(entityId);

            Sprite down1 = entity.getDown1();
            if (down1 != null ) {
                entityIcon.setDown1(down1);
            }

            Sprite down2 = entity.getDown2();
            if (down2 != null) {
                entityIcon.setDown2(down2);
            } else {
                entityIcon.setDown2(down1);
            }

            Sprite down3 = entity.getDown3();
            if (down3 != null) {
                entityIcon.setDown3(down3);
            } else {
                entityIcon.setDown3(down1);
            }

            entityIcons.put(entity.getEntityId(), entityIcon);
        } else {

            UtilityTool.logWarning("Attempted to create an icon for an entity with ID "
                    + entityId
                    + " that does not exist.");
        }
    }


    /**
     * Retrieves an entity icon by entity ID.
     *
     * @param entityId ID of entity whose entity icon is being retrieved
     * @return entity icon
     */
    public EntityIcon getEntityIconById(int entityId) {

        EntityIcon entityIcon = entityIcons.get(entityId);

        if (entityIcon == null) {

            UtilityTool.logWarning("Attempted to retrieve an entity icon with entity ID "
                    + entityId
                    + " that does not exist.");

            entityIcon = new EntityIcon(-1);                                                                            // Return a placeholder entity icon to prevent the program from hitting an unhandled exception.
        }
        return entityIcon;
    }


    /**
     * Purges all instantiated entity icons.
     */
    public void purgeAllEntityIcons() {

        entityIcons.clear();
        renderErrors.clear();                                                                                             // Reset draw errors since the list of entity icons was reset.
    }


    /**
     * Selects which sprite to draw for an entity icon if animated.
     * A walking animation is drawn.
     *
     * @param entityIcon entity icon being checked
     */
    private Sprite selectEntitySprite(EntityIcon entityIcon) {

        int animationCounter = entityIcon.getAnimationCounter();
        Sprite sprite;

        if (animationCounter <= 12) {

            sprite = entityIcon.getDown2();
        } else if ((animationCounter > 24) && (animationCounter <= 36)) {

            sprite = entityIcon.getDown3();
        } else {

            sprite = entityIcon.getDown1();
        }
        animationCounter++;

        if (animationCounter >= 48) {

            entityIcon.setAnimationCounter(0);
        } else {

            entityIcon.setAnimationCounter(animationCounter);
        }
        return sprite;
    }
}
