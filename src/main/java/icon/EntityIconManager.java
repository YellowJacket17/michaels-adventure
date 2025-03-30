package icon;

import entity.EntityBase;
import core.GamePanel;
import org.joml.Vector2f;
import render.Renderer;
import asset.Sprite;
import render.enumeration.ZIndex;
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
     * Updates the state of all entity icons by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        EntityIcon entityIcon;

        for (int key : entityIcons.keySet()) {

            entityIcon = entityIcons.get(key);
            entityIcon.update(dt);
        }
    }


    /**
     * Adds an entity icon to the render pipeline.
     *
     * @param renderer Renderer instance
     * @param entityId ID of the entity for whom the entity icon will be rendered
     * @param screenX screen x-coordinate of the icon (leftmost, normalized from 0 to 1, both inclusive)
     * @param screenY screen y-coordinate of the icon (topmost, normalized from 0 to 1, both inclusive)
     */
    public void addToRenderPipeline(Renderer renderer, int entityId, float screenX, float screenY) {

        EntityIcon entityIcon = entityIcons.get(entityId);

        if (entityIcon != null) {

            Vector2f worldCoords = gp.getCamera().screenCoordsToWorldCoords(new Vector2f(screenX, screenY));
            entityIcon.transform.position.x = worldCoords.x;
            entityIcon.transform.position.y = worldCoords.y;
            entityIcon.transform.scale.x = entityIcon.getNativeSpriteWidth();
            entityIcon.transform.scale.y = entityIcon.getNativeSpriteHeight();
            renderer.addDrawable(entityIcon, ZIndex.SECOND_LAYER);
        } else if (!renderErrors.contains(entityId)) {

            UtilityTool.logError("Failed to add entity icon with entity ID '"
                    + entityId
                    + "' to the render pipeline: icon does not exist.");
            renderErrors.add(entityId);
        }
    }


    /**
     * Creates entity icons for the player entity and all party member entities.
     */
    public void createPartyEntityIcons() {

        // Create an entity icon of the player.
        addEntityIcon(gp.getEntityM().getPlayer().getEntityId());

        // Create an entity icon for each part member.
        for (EntityBase entity : gp.getEntityM().getParty().values()) {
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

        EntityBase entity = gp.getEntityM().getEntityById(entityId);
        EntityIcon entityIcon = new EntityIcon(entityId);

        Sprite idleDown = entity.getIdleDown();
        if (idleDown != null ) {
            entityIcon.setIdleDown(idleDown);
        }

        Sprite walkDown1 = entity.getWalkDown1();
        if (walkDown1 != null) {
            entityIcon.setWalkDown1(walkDown1);
        } else {
            entityIcon.setWalkDown1(idleDown);
        }

        Sprite walkDown2 = entity.getWalkDown2();
        if (walkDown2 != null) {
            entityIcon.setWalkDown2(walkDown2);
        } else {
            entityIcon.setWalkDown2(idleDown);
        }

        entityIcons.put(entity.getEntityId(), entityIcon);
    }


    /**
     * Retrieves an entity icon by entity ID.
     *
     * @param entityId ID of entity whose entity icon is being retrieved
     * @return entity icon
     */
    public EntityIcon getEntityIconById(int entityId) {

        return entityIcons.get(entityId);
    }


    /**
     * Purges all instantiated entity icons.
     */
    public void purgeAllEntityIcons() {

        entityIcons.clear();
        renderErrors.clear();                                                                                           // Reset draw errors since the list of entity icons was reset.
    }
}
