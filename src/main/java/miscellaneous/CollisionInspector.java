package miscellaneous;

import core.GamePanel;
import entity.EntityBase;
import landmark.LandmarkBase;
import utility.UtilityTool;
import utility.LimitedLinkedHashMap;

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
     * Checks if an entity is predicted to collide with a tile that has collision (i.e., is solid).
     * If so, the entity is set as colliding.
     *
     * @param entity target entity
     * @return whether a collision was calculated (true) or not (false)
     */
    public boolean checkTile(EntityBase entity) {

        boolean collision = false;
        int entityCol = entity.getCol();                                                                                // Initialize variable with entity's current tile position.
        int entityRow = entity.getRow();                                                                                // ^^^
        int tileNum;                                                                                                    // We only need to check the type of tile that the entity is trying to walk towards.

        switch (entity.getDirectionCandidate()) {
            case UP:
                entityRow--;                                                                                            // Predicting where entity will be after a move and seeing if collision occurs.
                break;
            case DOWN:
                entityRow++;                                                                                            // Predicting where entity will be after a move and seeing if collision occurs.
                break;
            case LEFT:
                entityCol--;                                                                                            // Predicting where entity will be after a move and seeing if collision occurs.
                break;
            case RIGHT:
                entityCol++;                                                                                            // Predicting where entity will be after a move and seeing if collision occurs.
                break;
        }

        try {

            tileNum = gp.getMapM().getLoadedMap().getMapTileNum()[entityCol][entityRow];                                // Check what tile type the entity is predicted to walk into.

            if (gp.getTileM().getTiles()[tileNum].hasCollision()) {                                                     // Check if there's collision on `tileNum`; if true, the entity is hitting a solid tile and cannot move in that direction.

                entity.setColliding(true);
                collision = true;
            }

        } catch (ArrayIndexOutOfBoundsException e) {

            UtilityTool.logWarning("Entity "
                    + (((entity.getName() != null) && (!entity.getName().equals("")))
                        ? ("'" + entity.getName() + "' ") : "")
                    + "with ID '"
                    + entity.getEntityId()
                    + "' attempted to exceed the bounds of the world.");
            entity.setColliding(true);
            collision = true;
        }
        return collision;
    }


    /**
     * Checks if an entity is predicted to collide with a landmark that has collision (i.e., is solid).
     * If so, the entity is set as colliding.
     *
     * @param entity target entity
     * @return whether a collision was calculated (true) or not (false)
     */
    public boolean checkLandmark(EntityBase entity) {

        boolean collision = false;
        int entityCol = entity.getCol();                                                                            // Initialize variable with entity's current tile position.
        int entityRow = entity.getRow();                                                                            // ^^^

        switch (entity.getDirectionCandidate()) {                                                                   // Check entity's direction candidate, then simulate movement to predict where it would be after a step in that direction.
            case UP:
                entityRow--;                                                                                        // Predicting where entity will be after a move and seeing if collision occurs.
                break;
            case DOWN:
                entityRow++;                                                                                        // Predicting where entity will be after a move and seeing if collision occurs.
                break;
            case LEFT:
                entityCol--;                                                                                        // Predicting where entity will be after a move and seeing if collision occurs.
                break;
            case RIGHT:
                entityCol++;                                                                                        // Predicting where entity will be after a move and seeing if collision occurs.
                break;
        }

        try {

            if (gp.getLandmarkM().getCalculatedGlobalLandmarkCollision()[entityCol][entityRow]) {                   // Check if there's collision on target tile due to a landmark; if true, the entity is hitting a solid landmark/tile and cannot move in that direction.

                entity.setColliding(true);
                collision = true;
            }

        } catch (IndexOutOfBoundsException e) {

            UtilityTool.logWarning("Entity "
                    + (((entity.getName() != null) && (!entity.getName().equals("")))
                    ? ("'" + entity.getName() + "' ") : "")
                    + "with ID '"
                    + entity.getEntityId()
                    + "' attempted to exceed the bounds of the world.");
            entity.setColliding(true);
            collision = true;
        }
        return collision;
    }


    /**
     * Checks to see if a solid entity or landmark occupies a node; used as part of node collision check for A*
     * pathfinding.
     * Hidden entities are not counted.
     *
     * @param targetCol column of the tile/node being checked
     * @param targetRow row of the tile/node being checked
     * @param goalCol column of the final goal tile/node for the pathfinding operation
     * @param goalRow row of the final goal tile/node for the pathfinding operation
     * @param entity entity following the path
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @return whether a solid entity or landmark occupies the target tile/node (true) or not (false)
     */
    public boolean checkNode(
            int targetCol,int targetRow, int goalCol, int goalRow, EntityBase entity, boolean ignoreFollowers) {

        // Only check entities if the row/column we're checking isn't the same as the goal row/column for the pathfinding operation.
        // This is because a random entity occupying the goal row/column will automatically cause the entire pathfinding operation to fail.
        // However, we don't want the operation to fail if, say, an NPC walks into the goal row/column and then walks away.
        if ((targetRow != goalRow) || (targetCol != goalCol)) {
            if (calculateNodeEntity(targetRow, targetCol, entity, ignoreFollowers)) {
                return true;
            }
        }

        if (calculateNodeLandmark(targetCol, targetRow)) {
            return true;
        }
        return false;
    }


    /**
     * Checks if an entity (primary) is predicted to collide with another entity (target) that has collision (i.e., is
     * solid).
     * If so, the primary entity is set as colliding.
     * Hidden entities are not counted.
     *
     * @param entity primary entity
     * @param target target entity map to be checked for colliding entities
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @return ID of the target entity that the primary entity will collide with; will return
     * CollisionInspector.NO_COLLISION if no collision was calculated
     */
    public int checkEntity(
            EntityBase entity, LimitedLinkedHashMap<Integer, EntityBase> target, boolean ignoreFollowers) {

        return calculateEntityCollision(entity, target, ignoreFollowers);
    }


    /**
     * Checks if an entity is colliding with the player entity.
     * (If the player entity has collision (i.e., is solid)).
     * If so, the entity being check for collision with the player entity is set as colliding.
     * Hidden entities are not counted.
     *
     * @param entity entity being checked for collision with player entity
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @return ID of the player entity if a collision will occur; will return CollisionInspector.NO_COLLISION if no
     * collision was calculated
     */
    public int checkPlayer(EntityBase entity, boolean ignoreFollowers) {

        LimitedLinkedHashMap<Integer, EntityBase> target = new LimitedLinkedHashMap<>(1);                               // Create a target map that only contains the player entity; this is so we can use the `calculateEntityCollision()` method.
        target.put(gp.getEntityM().getPlayer().getEntityId(), gp.getEntityM().getPlayer());
        return calculateEntityCollision(entity, target, ignoreFollowers);
    }


    /**
     * Calculates whether a landmark with collision (i.e., a solid landmark) occupies a node; used as part of node
     * collision check for A* pathfinding.
     *
     * @param targetCol column of the tile/node being checked
     * @param targetRow row of the tile/node being checked
     * @return whether a solid landmark occupies the target tile/node (true) or not (false)
     */
    private boolean calculateNodeLandmark(int targetCol, int targetRow) {

        if (gp.getLandmarkM().getCalculatedGlobalLandmarkCollision()[targetCol][targetRow]) {                           // Check if there's collision on target tile due to a landmark; if true, the entity is hitting a solid landmark/tile and cannot move in that direction.

            return true;
        }
        return false;
    }


    /**
     * Calculates whether an entity with collision (i.e., a solid entity) occupies a node; used as part of node
     * collision check for A* pathfinding.
     * Hidden entities are not counted.
     *
     * @param targetRow row of the tile/node being checked
     * @param targetCol column of the tile/node being checked
     * @param entity entity following the path
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @return whether a solid entity occupies the target tile/node (true) or not (false)
     */
    private boolean calculateNodeEntity(int targetRow, int targetCol, EntityBase entity, boolean ignoreFollowers) {

        ArrayList<EntityBase> target = new ArrayList<>();                                                               // Generate a single list of all non-null entities; excludes the entity we're finding a path for.

        if (gp.getEntityM().getPlayer().getEntityId() != entity.getEntityId()) {
            target.add(gp.getEntityM().getPlayer());
        }

        for (EntityBase candidate : gp.getEntityM().getNpc().values()) {
            if ((candidate != null) && (candidate.getEntityId() != entity.getEntityId())) {
                target.add(candidate);
            }
        }

        for (EntityBase candidate : gp.getEntityM().getObj().values()) {
            if ((candidate != null) && (candidate.getEntityId() != entity.getEntityId())) {
                target.add(candidate);
            }
        }

        for (EntityBase candidate : gp.getEntityM().getParty().values()) {
            if ((candidate != null) && (candidate.getEntityId() != entity.getEntityId())) {
                target.add(candidate);
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

                            return true;                                                                                // A solid entity occupies this tile.
                        }
                    } else {

                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Calculates if an entity (primary) is predicted to collide with another entity (target) that has collision (i.e.,
     * is solid).
     * If so, the primary entity is set as colliding.
     * Hidden entities are not counted.
     *
     * @param entity primary entity
     * @param target target entity map to be checked for colliding entities
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @return ID of the target entity that the primary entity will collide with; will return
     * CollisionInspector.NO_COLLISION if no collision was calculated
     */
    private int calculateEntityCollision(
            EntityBase entity, LimitedLinkedHashMap<Integer, EntityBase> target, boolean ignoreFollowers) {

        int collidingEntityId = NO_COLLISION;                                                                           // Initialize with a default value.

        int entityCol = entity.getCol();                                                                                // Initialize variable with primary entity's current tile position.
        int entityRow = entity.getRow();                                                                                // ^^^

        switch (entity.getDirectionCandidate()) {                                                                       // Check the primary entity's direction candidate, then simulate movement to predict where it would be after a step in that direction.
            case UP:
                entityRow--;
                break;
            case DOWN:
                entityRow++;
                break;
            case LEFT:
                entityCol--;
                break;
            case RIGHT:
                entityCol++;
                break;
        }

        for (EntityBase candidate : target.values()) {                                                                  // Scan target entity map.

            if ((candidate != null)
                    && (candidate.getEntityId() != entity.getEntityId())) {                                             // Only check if the candidate entity is not null and if the candidate entity is not the same as the primary entity.

                int targetColStart = candidate.getColStart();                                                           // Get the candidate entity's starting tile position (will be same as ending not currently moving).
                int targetRowStart = candidate.getRowStart();                                                           // ^^^

                int targetColEnd = candidate.getColEnd();                                                               // Get the candidate entity's ending tile position (will be same as starting if not currently moving).
                int targetRowEnd = candidate.getRowEnd();                                                               // ^^^

                if (((entityRow == targetRowStart) && (entityCol == targetColStart)) ||
                        ((entityRow == targetRowEnd) && (entityCol == targetColEnd))) {                                 // Check if the primary entity will step into the candidate entity.

                    if (candidate.hasCollision() && !candidate.isHidden()) {                                            // Check whether the candidate entity is solid (i.e., has collision) or not and visible.

                        if (ignoreFollowers) {

                            boolean followed = gp.getEventM().checkEntityChainUp(entity, candidate);                    // Check if the primary entity is being followed by the candidate entity (directly or in a chain).
                            boolean following = gp.getEventM().checkEntityChainUp(candidate, entity);                   // Check if the primary entity is following the candidate entity (directly or in a chain).

                            if ((!followed) && (!following)) {                                                          // If either condition is true, no collision will be set.

                                entity.setColliding(true);
                                collidingEntityId = candidate.getEntityId();
                            }
                        } else {

                            entity.setColliding(true);
                            collidingEntityId = candidate.getEntityId();
                            break;
                        }
                    }
                }
            }
        }
        return collidingEntityId;                                                                                       // Return the colliding entity ID.
    }
}
