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
     * Checks if the tile in front of an entity has collision.
     *
     * @param entity interacting entity
     */
    public void checkTile(EntityBase entity) {

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

            tileNum = gp.getLoadedMap().getMapTileNum()[entityCol][entityRow];                                          // Check what tile type the entity is predicted to walk into.

            if (gp.getTileM().getTiles()[tileNum].hasCollision()) {                                                     // Check if there's collision on `tileNum`; if true, the entity is hitting a solid tile and cannot move in that direction.

                entity.setColliding(true);
            }

        } catch (ArrayIndexOutOfBoundsException e) {

            UtilityTool.logWarning("Entity "
                    + (((entity.getName() != null) && (!entity.getName().equals("")))
                        ? ("'" + entity.getName() + "' ") : "")
                    + "with ID '"
                    + entity.getEntityId()
                    + "' attempted to exceed the bounds of the world.");
            entity.setColliding(true);
        }
    }


    /**
     * Checks if an entity is colliding with a landmark.
     * If so, the entity being check for collision with a landmark is set as colliding.
     *
     * @param entity interacting entity
     */
    public void checkLandmark(EntityBase entity) {

        if (gp.getLoadedMap() != null) {                                                                                // Only run this logic if there's a loaded map.

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

            ArrayList<LandmarkBase> landmarks = gp.getLoadedMap().getMapLandmarks();                                    // Retrieve all landmarks on the current map.
            boolean collision = false;                                                                                  // Initialize a variable that will track whether a collision has been detected or not.
            int index = 0;                                                                                              // Initialize a variable to track which landmark in the landmarks array is currently being checked.

            while ((index < landmarks.size()) && (!collision)) {                                                        // Check each landmark in the landmarks array as long as a collision has not yet occurred.

                boolean breakLandmark = false;                                                                          // Initialize a variable to track whether the current landmark should keep being checked.
                LandmarkBase landmark = landmarks.get(index);                                                           // Retrieve a landmark to check.
                int row = 0;

                while ((row < landmark.getNumTilesRow()) && (!breakLandmark)) {                                         // Check each row of tiles that the landmark occupies as long as the entity's row hasn't been checked yet.

                    int tileRow = landmark.getRow() - row;

                    if (entityRow == tileRow) {                                                                         // Only check columns in this row if it's the same as the entity row (save's computation resources).

                        int col = 0;

                        while ((col < landmark.getNumTilesCol()) && (!collision)) {                                     // Check each column of tiles that the landmark occupies as long as a collision has not yet occurred.

                            int tileCol = landmark.getCol() + col;

                            if ((entityCol == tileCol) && (landmark.getCollision()[row][col])) {

                                entity.setColliding(true);                                                              // A collision has occurred.
                                collision = true;                                                                       // Break this entire loop of checking landmarks so that we don't waste more computation resources.
                            }
                            col++;                                                                                      // Iterate to the next column.
                        }
                        breakLandmark = true;                                                                           // No need to check further rows; break the current landmark's loop so that we don't waste more computation resources.
                    }
                    row++;                                                                                              // Iterate to the next row.
                }
                index++;                                                                                                // Iterate to the next landmark.
            }
        }
    }


    /**
     * Checks to see if a solid entity or landmark occupies a node; used as part of node collision check for A*
     * pathfinding.
     * Hidden entities are not counted.
     *
     * @param targetRow row of the tile/node being checked
     * @param targetCol column of the tile/node being checked
     * @param goalRow row of the final goal tile/node for the pathfinding operation
     * @param goalCol column of the final goal tile/node for the pathfinding operation
     * @param entity entity following the path
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @return whether a solid entity or landmark occupies the target tile/node (true) or not (false)
     */
    public boolean checkNode(int targetRow, int targetCol, int goalRow, int goalCol, EntityBase entity, boolean ignoreFollowers) {

        // Only check entities if the row/column we're checking isn't the same as the goal row/column for the pathfinding operation.
        // This is because a random entity occupying the goal row/column will automatically cause the entire pathfinding operation to fail.
        // However, we don't want the operation to fail if, say, an NPC walks into the goal row/column and then walks away.
        if ((targetRow != goalRow) || (targetCol != goalCol)) {
            if (calculateNodeEntity(targetRow, targetCol, entity, ignoreFollowers)) {
                return true;
            }
        }

        if (calculateNodeLandmark(targetRow, targetCol)) {
            return true;
        }
        return false;
    }


    /**
     * Checks if an entity (primary) is colliding with another entity (target).
     * If so, the primary entity is set as colliding.
     * Hidden entities are not counted.
     *
     * @param entity primary entity
     * @param target target entity map to be checked for colliding entities
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @return ID of the colliding entity; will return CollisionInspector.NO_COLLISION if no collision was found
     */
    public int checkEntity(EntityBase entity, LimitedLinkedHashMap<Integer, EntityBase> target, boolean ignoreFollowers) {

        return calculateEntityCollision(entity, target, ignoreFollowers);
    }


    /**
     * Checks if an entity is colliding with the player entity.
     * If so, the entity being check for collision with the player entity is set as colliding.
     * Hidden entities are not counted.
     *
     * @param entity entity being checked for collision with player entity
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     */
    public void checkPlayer(EntityBase entity, boolean ignoreFollowers) {

        LimitedLinkedHashMap<Integer, EntityBase> target = new LimitedLinkedHashMap<>(1);                               // Create a target map that only contains the player entity; this is so we can use the `calculateEntityCollision()` method.
        target.put(gp.getPlayer().getEntityId(), gp.getPlayer());
        calculateEntityCollision(entity, target, ignoreFollowers);
    }


    /**
     * Calculates whether a solid landmark occupies a node; used as part of node collision check for A* pathfinding.
     *
     * @param targetRow row of the tile/node being checked
     * @param targetCol column of the tile/node being checked
     * @return whether a solid landmark occupies the target tile/node (true) or not (false)
     */
    private boolean calculateNodeLandmark(int targetRow, int targetCol) {

        if (gp.getLoadedMap() != null) {                                                                                // Only run this logic if there's a loaded map.

            ArrayList<LandmarkBase> landmarks = gp.getLoadedMap().getMapLandmarks();                                    // Generate a single list od all landmarks on the current map.

            int index = 0;                                                                                              // Initialize a variable to track which landmark in the landmarks array is currently being checked.

            while (index < landmarks.size()) {                                                                          // Check each landmark in the landmarks array until a collision is detected.

                boolean breakLandmark = false;                                                                          // Initialize a variable to track whether the current landmark should keep being checked.

                LandmarkBase landmark = landmarks.get(index);                                                           // Retrieve a landmark to check.

                int row = 0;

                while ((row < landmark.getNumTilesRow()) && (!breakLandmark)) {                                         // Check each row of tiles that the landmark occupies as long as the target row hasn't been checked yet.

                    int tileRow = landmark.getRow() - row;

                    if (targetRow == tileRow) {                                                                         // Only check columns in this row if it's the same as the target row (save's computation resources).

                        int col = 0;

                        while (col < landmark.getNumTilesCol()) {                                                       // Check each column of tiles that the landmark occupies until a collision is detected.

                            int tileCol = landmark.getCol() + col;

                            if ((targetCol == tileCol) && (landmark.getCollision()[row][col])) {

                                return true;                                                                            // A solid landmark occupies this tile, therefore collision will occur.
                            }
                            col++;                                                                                      // Iterate to the next column.
                        }
                        breakLandmark = true;                                                                           // No need to check further rows; break the current landmark's loop so that we don't waste anymore computation resources.
                    }
                    row++;                                                                                              // Iterate to the next row.
                }
                index++;                                                                                                // Iterate to the next landmark.
            }
        }
        return false;
    }


    /**
     * Calculates whether a solid entity occupies a node; used as part of node collision check for A* pathfinding.
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

        if (gp.getPlayer().getEntityId() != entity.getEntityId()) {
            target.add(gp.getPlayer());
        }

        for (EntityBase candidate : gp.getNpc().values()) {
            if ((candidate != null) && (candidate.getEntityId() != entity.getEntityId())) {
                target.add(candidate);
            }
        }

        for (EntityBase candidate : gp.getObj().values()) {
            if ((candidate != null) && (candidate.getEntityId() != entity.getEntityId())) {
                target.add(candidate);
            }
        }

        for (EntityBase candidate : gp.getParty().values()) {
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
     * Calculates if an entity (primary) is colliding with another entity (target).
     * If so, the primary entity is set as colliding.
     * Hidden entities are not counted.
     *
     * @param entity primary entity
     * @param target target entity map to be checked for colliding entities
     * @param ignoreFollowers whether to ignore collision of entities following/leading the primary entity (true) or not
     *                        (false)
     * @return ID of the colliding entity; will return CollisionInspector.NO_COLLISION if no collision was found
     */
    private int calculateEntityCollision(EntityBase entity, LimitedLinkedHashMap<Integer, EntityBase> target, boolean ignoreFollowers) {

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
                        }
                    }
                }
            }
        }
        return collidingEntityId;                                                                                       // Return the colliding entity ID.
    }
}
