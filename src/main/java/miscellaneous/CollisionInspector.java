package miscellaneous;

import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import utility.UtilityTool;

import java.util.ArrayList;

/**
 * This class checks entity collision with tiles, landmarks and other entities.
 */
public class CollisionInspector {

    // FIELDS
    private final GamePanel gp;

    /**
     * Argument to be passed when no collision has occurred.
     */
    public static final int NO_COLLISION = 68761926;


    // CONSTRUCTOR
    /**
     * Constructs a CollisionInspector instance.
     *
     * @param gp GamePanel instance
     */
    public CollisionInspector(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Checks if there is a solid tile, entity, or landmark (i.e., collision) at a given world location.
     * This method is intended to be called for A* pathfinding node collision.
     * Hidden entities are not counted.
     *
     * @param targetCol column of the tile/node being checked
     * @param targetRow row of the tile/node being checked
     * @param goalCol column of the final goal tile/node for the pathfinding operation
     * @param goalRow row of the final goal tile/node for the pathfinding operation
     * @param entity entity following the path
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @param incomingDirection direction from which the tile/node will be checked relative to the previous tile/node
     * @return whether a solid entity or landmark occupies the target tile/node (true) or not (false)
     */
    public boolean calculateCollisionNode(
            int targetCol,int targetRow, int goalCol, int goalRow, EntityBase entity,
            boolean ignoreFollowers, EntityDirection incomingDirection) {

        // Tile.
        if (calculateCollisionTile(targetCol, targetRow) != NO_COLLISION) {
            if (!calculateCollisionTileException(targetCol, targetRow,entity, ignoreFollowers, incomingDirection)) {
                return true;
            }
        }

        // Landmark.
        if (calculateCollisionLandmark(targetCol, targetRow)) {
            return true;
        }

        // Entity.
        // Only check entities if the row/column we're checking isn't the same as the goal row/column for the pathfinding operation.
        // This is because a random entity occupying the goal row/column will automatically cause the entire pathfinding operation to fail.
        // However, we don't want the operation to fail if, say, an NPC walks into the goal row/column and then walks away.
        if ((targetCol != goalCol) || (targetRow != goalRow)) {
            if (calculateCollisionEntity(targetCol, targetRow, entity, ignoreFollowers, false, false, false, false)
                    != NO_COLLISION) {
                return true;
            }
        }

        return false;
    }


    /**
     * Checks if there is a solid tile, landmark, or entity (i.e., collision) at a given world location.
     * Hidden entities are not counted.
     * This method is NOT intended to be called for A* pathfinding node collision.
     *
     * @param targetRow row of the tile being checked
     * @param targetCol column of the tile being checked
     * @param entity primary entity being checked for any collisions
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @return ID of the target entity that the primary entity will collide with; will return
     * CollisionInspector.NO_COLLISION if no collision was calculated
     */
    public boolean calculateCollisionAll(int targetCol, int targetRow, EntityBase entity, boolean ignoreFollowers) {

        if (calculateCollisionTile(targetCol, targetRow) != NO_COLLISION) {
            return true;
        }

        if (calculateCollisionLandmark(targetCol, targetRow)) {
            return true;
        }

        if (calculateCollisionEntity(targetCol, targetRow, entity, ignoreFollowers, false, false, false, false)
                != NO_COLLISION) {
            return true;
        }

        return false;
    }


    /**
     * Checks if there is a solid tile (i.e., a tile with collision) at a given world location.
     *
     * @param targetCol column of the tile/node being checked
     * @param targetRow row of the tile/node being checked
     * @return tile number of the solid tile; will return CollisionInspector.NO_COLLISION if no collision was calculated
     */
    public int calculateCollisionTile(int targetCol, int targetRow) {

        try {

            if (gp.getTileM()
                    .getTiles()[gp.getMapM().getLoadedMap().getMapTileNum()[targetCol][targetRow]].hasCollision()) {

                return gp.getMapM().getLoadedMap().getMapTileNum()[targetCol][targetRow];
            }

        } catch (ArrayIndexOutOfBoundsException e) {

            UtilityTool.logWarning("Attempted to calculate out-of-bounds tile collision at column '"
                    + targetCol
                    + "' and row '"
                    + targetRow
                    + "'.");
        }

        return NO_COLLISION;
    }


    /**
     * Checks if there is a solid landmark (i.e., a landmark with collision) at a given world location.
     *
     * @param targetCol world column of the tile/node being checked
     * @param targetRow row of the tile/node being checked
     * @return whether a collision was calculated (true) or not (false)
     */
    public boolean calculateCollisionLandmark(int targetCol, int targetRow) {

        try {

            return gp.getLandmarkM().getCalculatedGlobalLandmarkCollision()[targetCol][targetRow];                      // Check if there's collision on target tile due to a landmark; if true, the entity is hitting a solid landmark/tile and cannot move in that direction.

        } catch (ArrayIndexOutOfBoundsException e) {

            UtilityTool.logWarning("Attempted to calculate out-of-bounds landmark collision at column '"
                    + targetCol
                    + "' and row '"
                    + targetRow
                    + "'.");
            return true;
        }
    }


    /**
     * Checks if there is a solid entity (i.e., an entity with collision) at a given world location.
     * Hidden entities are not counted.
     *
     * @param targetCol column of the tile/node being checked
     * @param targetRow row of the tile/node being checked
     * @param entity primary entity being checked for any collisions
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @param ignorePlayer whether to ignore collision with the player entity (true) or not (false)
     * @param ignoreNpc whether to ignore collision with the NPC entities (true) or not (false)
     * @param ignoreObject whether to ignore collision with object entities (true) or not (false)
     * @param ignoreParty whether to ignore collision with party member entities (true) or not (false)
     * @return ID of the target entity that the primary entity will collide with; will return
     * CollisionInspector.NO_COLLISION if no collision was calculated
     */
    public int calculateCollisionEntity(int targetCol, int targetRow, EntityBase entity, boolean ignoreFollowers,
                                        boolean ignorePlayer, boolean ignoreNpc, boolean ignoreObject,
                                        boolean ignoreParty) {

        int collidingEntityId = NO_COLLISION;

        ArrayList<EntityBase> target = new ArrayList<>();                                                               // Generate a single list of all non-null entities; excludes the entity we're finding a path for.

        if (!ignorePlayer && (gp.getEntityM().getPlayer().getEntityId() != entity.getEntityId())) {
            target.add(gp.getEntityM().getPlayer());
        }

        if (!ignoreNpc) {
            for (EntityBase candidate : gp.getEntityM().getNpc().values()) {
                if ((candidate != null) && (candidate.getEntityId() != entity.getEntityId())) {
                    target.add(candidate);
                }
            }
        }

        if (!ignoreObject) {

            for (EntityBase candidate : gp.getEntityM().getObj().values()) {
                if ((candidate != null) && (candidate.getEntityId() != entity.getEntityId())) {
                    target.add(candidate);
                }
            }
        }

        if (!ignoreParty) {
            for (EntityBase candidate : gp.getEntityM().getParty().values()) {
                if ((candidate != null) && (candidate.getEntityId() != entity.getEntityId())) {
                    target.add(candidate);
                }
            }
        }

        for (EntityBase candidate : target) {                                                                           // Check each entity in the target list to see if any will cause collision to occur.

            int targetColStart = candidate.getColStart();                                                               // Get the candidate entity's starting tile position (will be same as ending not currently moving).
            int targetRowStart = candidate.getRowStart();                                                               // ^^^

            int targetColEnd = candidate.getColEnd();                                                                   // Get the candidate entity's ending tile position (will be same as starting if not currently moving).
            int targetRowEnd = candidate.getRowEnd();                                                                   // ^^^

            if (((targetRow == targetRowStart) && (targetCol == targetColStart)) ||
                    ((targetRow == targetRowEnd) && (targetCol == targetColEnd))) {                                     // Check if the candidate entity occupies the targeted row and column.

                if (candidate.hasCollision() && !candidate.isHidden()) {                                                // Check if the candidate entity is solid (i.e., has collision) or not and visible.

                    if (ignoreFollowers) {

                        boolean followed = gp.getEventM().checkEntityChainUp(entity, candidate);                        // Check if the primary entity is being followed by the target entity (directly or in chain).
                        boolean following = gp.getEventM().checkEntityChainUp(candidate, entity);                       // Check if the primary entity is following the target entity (directly or in chain).

                        if ((!followed) && (!following)) {                                                              // If either condition is true, no collision will be set.

                            collidingEntityId = candidate.getEntityId();                                                // A solid entity occupies this tile.
                            break;
                        }
                    } else {

                        collidingEntityId = candidate.getEntityId();
                        break;
                    }
                }
            }
        }
        return collidingEntityId;
    }


    /**
     * Checks if an exception exists for a tile that otherwise has collision (i.e., is solid) based on the direction
     * from which it is traversed.
     * This method is intended to be called for A* pathfinding node collision.
     *
     * @param targetCol column of the tile/node being checked
     * @param targetRow row of the tile/node being checked
     * @param entity entity following the path
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @param incomingDirection direction from which the tile/node will be checked relative to the previous tile/node
     * @return whether an exception was found (true) or not (false)
     */
    private boolean calculateCollisionTileException(int targetCol, int targetRow, EntityBase entity,
                                                    boolean ignoreFollowers, EntityDirection incomingDirection) {

        int tileNum = gp.getMapM().getLoadedMap().getMapTileNum()[targetCol][targetRow];

        if (((tileNum == 132) || (tileNum == 133) || (tileNum == 134) || (tileNum == 135))                              // Check ledge hop (downward).
                && (incomingDirection == EntityDirection.DOWN)
                && (calculateCollisionAll(targetCol, targetRow + 1, entity, ignoreFollowers))) {                        // Ensure tile being hopped onto is not solid.

            return true;
        }
        return false;
    }
}
