package entity;

import combat.MoveBase;
import core.GamePanel;
import render.Renderer;
import render.Sprite;
import render.ZIndex;
import render.drawable.Drawable;
import utility.LimitedArrayList;
import utility.UtilityTool;

import java.util.Random;

/**
 * This abstract class defines base logic for the player, character, and object classes.
 */
public abstract class EntityBase extends Drawable {

    /*
     * An entity can be one of two types:
     *    - CHARACTER (NPC, party member, or player)
     *    - OBJECT
     *
     * A character is an NPC, party member, or the player.  Essentially, these are people.
     * An object is meant to be something static that is not a person (an item such a key, etc.).
     * Both characters and objects share a base class because they are fundamentally the same thing.
     * The biggest difference between a character and an object is that a character can move around the map and be
     * talked to by the player, while an object cannot.
     * However, an object could be made to move (say, for example, an object is a bike being pushed along).
     *
     * It's not necessarily black-and-white between what is a character and what is an object.
     * The distinction between the two types is largely for organizational purposes.
     * Mainly, the types determine whether the entity is placed in the NPC, party, or object map in GamePanel.
     *
     * New Entity instances of both types are loaded from JSON data via the JsonParser class.
     * Each Entity instance is assigned a unique ID, determined by the order in the JSON file.
     * This unique ID can be used to set logic for a specific entity for things like interactions, etc.
     *
     * When creating new character classes that extend this class, model them after the classes in the `npc` package.
     * When creating new object classes that extend this class, model them after the classes in the `object` package.
     *
     * Note that there's an adjustment to the `worldY` coordinate when rendering an entity of type CHARACTER.
     * Since an entity's `worldY` represents the bottom-left tile it occupies, we need to effectively move the entity
     * sprite "up" slightly (in the negative y-direction) when rendering since rendering is done in respect to the
     * top-left coordinate of a sprite.
     * In other words, if a character is taller than a tile, the y-position needs to adjusted when rendering.
     * If this adjustment were to not happen, then the top-left corner of a character's sprite would be in the same
     * position as the top-left corner of the bottom-left tile it's meant to occupy.
     * This would cause the entire character sprite to render "lower" on the map (in the position y-direction) than it
     * should.
     * This is not an issue for entities of type OBJECT since their heights are forced to be the same as the native tile
     * size.
     *
     *
     * Please note the following:
     *    - Entities are solid/have collision by default unless changed during or after instantiation.
     *    - An entity's `speed` is not required for instantiation and will be zero (Java default) unless defined.
     *      If a character OR object that can move class is made, define a value for `speed` on instantiation.
     *    - An entity is not required to have a `name` (it's not required as part of the Entity class's constructor).
     *      To give a default `name` to an entity, define it in that entity's class.
     *    - An entity has two states for following a path: `onPath` and `onEntity`.
     *      `onPath` is set to true when the entity is finding a path to an arbitrary world position.
     *      `onEntity` is set to true when the entity is finding a path to follow a target entity.
     *      Only one of these is true at a time (i.e., when `onEntity` is true, `onPath` is not also true).
     *      `onPath` is to be used with the `actionPath()` method/action.
     *      `OnEntity` is to be used with the `actionFollowEntity()` method/action.
     *      To initiate an entity to go to an arbitrary world position, call `setStartFollowingPath()`.
     *    - Do not have multiple followers on the same target!
     *      This does not work out well.
     */

    // BASIC FIELDS
    protected final GamePanel gp;

    /**
     * Unique entity ID.
     * To be clear, this ID is unique for each EntityBase instance across the entire game, NOT for just each EntityBase
     * type/subclass.
     */
    protected final int entityId;

    /**
     * Type of entity (CHARACTER or OBJECT).
     */
    protected final EntityType type;

    /**
     * Boolean setting whether this entity has collision (i.e., is solid) or not.
     * An entity has collision by default.
     */
    protected boolean collision = true;

    /**
     * Entity sprite.
     */
    protected Sprite down1, down2, down3, up1, up2, up3, left1, left2, left3, right1, right2, right3;

    /**
     * Boolean tracking whether a render error has occurred.
     * If true, this prevents a render error from repeatedly being printed to the console.
     */
    protected boolean renderError = false;


    // STATE
    /**
     * Entity's current world position.
     */
    protected float worldX, worldY;

    /**
     * Record of entity's previous/starting world position if it is currently moving from one tile to an adjacent one.
     * This is used while an entity is in a state of motion.
     * If an entity is not in a state of motion, this will equal the current world position.
     */
    protected float worldXStart, worldYStart;

    /**
     * Record of entity's target world position if it is currently moving from one tile to an adjacent one.
     * This is used while an entity is in a state of motion.
     * If an entity is not in a state of motion, this will equal the current world position.
     */
    protected float worldXEnd, worldYEnd;

    /**
     * Entity's last world position.
     * This is the location of the previous tile an entity resided on before moving to its current one.
     */
    protected float worldXLast, worldYLast;

    /**
     * Current direction that this entity is facing.
     * Among other things, this determines which directional sprite to render.
     */
    protected EntityDirection directionCurrent;

    /**
     * Last direction this entity was facing.
     * Most of the time, this will equal the current direction.
     * This variable is primarily used to temporarily store the player entity's last direction when the player changes
     * direction manually via input.
     */
    protected EntityDirection directionLast;

    /**
     * Candidate direction this entity has chosen to move in.
     * Potential collisions with other entities, landmarks, tiles, etc. will be checked using this candidate before the
     * entity actually enters a state of motion.
     * This variable is primarily used when generating directions for entities to move in before confirming its actually
     * feasible.
     */
    protected EntityDirection directionCandidate;

    /**
     * Current sprite number to render for a given direction.
     * This is primarily used for walking animation.
     */
    protected int spriteNumCurrent = 1;

    /**
     * Last sprite number rendered for a given direction.
     * This is primarily used for walking animation.
     */
    protected int spriteNumLast = 2;

    /**
     * Boolean tracking whether this entity is currently in a state of motion or not (used for grid-based movement).
     */
    protected boolean moving = false;

    /**
     * Boolean tracking whether the entity is currently in a state of turning or not.
     * This is primarily used when the player entity turns (i.e., changes direction) from a static state.
     */
    protected boolean turning = false;

    /**
     * Boolean tracking whether this entity is currently colliding with something (other entity, landmark, tile, etc.)
     * that has collision.
     */
    protected boolean colliding = false;

    /**
     * Boolean to set whether this entity is rendered on screen or not.
     */
    protected boolean hidden = false;

    /**
     * Default idle action that this entity will do when not being interacted with by the player or some other event.
     */
    protected DefaultIdle defaultIdle;


    // PATHFINDING
    /**
     * Boolean setting whether this entity is currently following a path or not.
     */
    protected boolean onPath = false;

    /**
     * Goal for this entity to reach when following a path.
     * The default value is zero when no path is being followed.
     */
    protected int onPathGoalCol, onPathGoalRow;

    /**
     * Boolean setting whether this entity is following another entity.
     */
    protected boolean onEntity = false;

    /**
     * ID of the entity that this entity is following.
     * A value of -1 means that no entity is being followed.
     */
    protected int onEntityId = -1;


    // BUFFERS
    /**
     * Counts the number of world units this entity has moved thus far while in a state of motion.
     * In practice, this is used to track whether a tile's length has been traversed yet or not.
     */
    protected double worldCounter;

    /**
     * Controls the number of seconds this entity must wait before moving again after exiting a state of motion.
     */
    protected double rest;


    // BASIC ATTRIBUTES
    /**
     * Entity name.
     */
    protected String name;

    /**
     * Entity world walking speed.
     * This is the number of integer world units that this entity will move per second while in a state of motion.
     * Do not confuse this variable with an entity's agility stat.
     */
    protected int speed = 120;


    // COMBAT ATTRIBUTES
    /**
     * Entity's maximum life/health points.
     */
    protected int maxLife;

    /**
     * Entity's remaining life/health points.
     */
    protected int life;

    /**
     * Entity's maximum available skill points for moves in combat.
     */
    protected int maxSkillPoints;

    /**
     * Entity's remaining available skill points for moves in combat.
     */
    protected int skillPoints;

    /**
     * Entity's base attack stat.
     */
    protected int baseAttack;

    /**
     * Entity's base defense stat.
     */
    protected int baseDefense;

    /**
     * Entity's base magic stat.
     */
    protected int baseMagic;

    /**
     * Entity's base agility stat.
     */
    protected int baseAgility;

    /**
     * Entity's total current attack stat.
     */
    protected int attack;

    /**
     * Entity's total current defense stat.
     */
    protected int defense;

    /**
     * Entity's total current magic stat.
     */
    protected int magic;

    /**
     * Entity's total current agility stat.
     */
    protected int agility;

    /**
     * Temporary multiplier to this entity's attack stat.
     * For example, a value of 0.2 would result this entity's attack stat equaling the base attack plus 20% of the base
     * attack.
     */
    protected double attackBuff;

    /**
     * Temporary multiplier to this entity's defense stat.
     * For example, a value of 0.2 would result this entity's defense stat equaling the base defense plus 20% of the
     * base defense.
     */
    protected double defenseBuff;

    /**
     * Temporary multiplier to this entity's magic stat.
     * For example, a value of 0.2 would result this entity's magic stat equaling the base magic plus 20% of the base
     * magic.
     */
    protected double magicBuff;

    /**
     * Temporary multiplier to this entity's agility stat.
     * For example, a value of 0.2 would result this entity's agility stat equaling the base agility plus 20% of the
     * base agility.
     */
    protected double agilityBuff;

    /**
     * Entity's status (faint, etc.).
     */
    protected EntityStatus status;

    /**
     * Amount of experience this entity has earned at its current level.
     * This is not cumulative of lifetime experience earned by this entity.
     */
    protected int exp;

    /**
     * Entity's level.
     */
    protected int level;

    /**
     * Amount of experience this entity requires to reach it next level.
     */
    protected int nextLevelExp;


    // COMBAT MOVES
    /**
     * List to store this entity's combat moves.
     */
    protected final LimitedArrayList<MoveBase> moves = new LimitedArrayList<>(4);


    // CONSTRUCTOR
    /**
     * Constructs an EntityBase instance.
     *
     * @param gp GamePanel instance
     * @param entityId unique entity ID
     * @param type type of entity (CHARACTER or OBJECT)
     */
    public EntityBase(GamePanel gp, int entityId, EntityType type) {
        super();
        this.gp = gp;
        this.type = type;
        this.entityId = entityId;
        this.directionCurrent = EntityDirection.DOWN;
        this.directionLast = EntityDirection.DOWN;
        this.directionCandidate = EntityDirection.DOWN;
        setRest(1);
    }


    // METHODS
    /**
     * Updates the state of this entity by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        // These are core actions that take precedent over all others.
        if (onEntity) {
            actionFollowEntity(dt, onEntityId);
            return;
        }
        if (onPath) {
            actionPath(dt, onPathGoalCol, onPathGoalRow);
            return;
        }

        // If entity is in player's party and not following/active, no update is necessary.
        if (gp.getParty().get(entityId) != null) {}

        // Set other actions.
        setAction(dt);
    }


    /**
     * Adds this entity to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        if (!hidden) {

            sprite = retrieveSprite();                                                                                  // Retrieve the sprite to be rendered.

            if (sprite != null) {

                int worldXAdjustment = (GamePanel.NATIVE_TILE_SIZE / 2) - (sprite.getNativeWidth() / 2);                // Amount in the x-direction that the sprite needs to be adjusted when rendered; ensures sprite is centered on occupied tile.
                int worldYAdjustment = -sprite.getNativeHeight() + GamePanel.NATIVE_TILE_SIZE;                          // Amount in the y-direction that the sprite needs to be adjusted when rendered; ensures bottom of sprite touches bottom of occupied tile.
                transform.position.x = worldX + worldXAdjustment;
                transform.position.y = worldY + worldYAdjustment;
                transform.scale.x = sprite.getNativeWidth();
                transform.scale.y = sprite.getNativeHeight();
                renderer.addDrawable(this, ZIndex.THIRD_LAYER);
            } else if (!renderError) {

                UtilityTool.logError("Failed to add entity "
                        + (((name != null) && (!name.equals(""))) ? (name + " ") : "")
                        + "with ID "
                        + entityId
                        + " to the render pipeline: sprites may not have been properly loaded upon entity initialization.");
                renderError = true;
            }
        }
    }


    /**
     * Cancels this entity's current action (i.e., exits its current state of motion and returns to its previous tile).
     */
    public void cancelAction() {

        moving = false;
        turning = false;
        worldX = worldXStart;
        worldY = worldYStart;
        worldCounter = 0;
        directionCurrent = directionLast;
        spriteNumCurrent = 1;
    }


    /**
     * Forces this entity to enter a new state of motion: step to an adjacent tile in the specified direction.
     *
     * @param direction direction this entity will move in
     */
    public void autoStep(EntityDirection direction) {

        if (!moving) {                                                                                                  // Only force entity to move if it's not already in a state of motion.

            switch (direction) {
                case UP:
                    directionCurrent = EntityDirection.UP;
                    directionCandidate = EntityDirection.UP;
                    directionLast = EntityDirection.UP;
                    worldXEnd = worldX;
                    worldYEnd = worldY - GamePanel.NATIVE_TILE_SIZE;
                    break;
                case DOWN:
                    directionCurrent = EntityDirection.DOWN;
                    directionCandidate = EntityDirection.DOWN;
                    directionLast = EntityDirection.DOWN;
                    worldXEnd = worldX;
                    worldYEnd = worldY + GamePanel.NATIVE_TILE_SIZE;
                    break;
                case LEFT:
                    directionCurrent = EntityDirection.LEFT;
                    directionCandidate = EntityDirection.LEFT;
                    directionLast = EntityDirection.LEFT;
                    worldXEnd = worldX - GamePanel.NATIVE_TILE_SIZE;
                    worldYEnd = worldY;
                    break;
                case RIGHT:
                    directionCurrent = EntityDirection.RIGHT;
                    directionCandidate = EntityDirection.RIGHT;
                    directionLast = EntityDirection.RIGHT;
                    worldXEnd = worldX + GamePanel.NATIVE_TILE_SIZE;
                    worldYEnd = worldY;
                    break;
            }

            moving = true;                                                                                              // When a movement is triggered, the player character enters a state of motion.
            worldXStart = worldX;                                                                                       // Record current position before moving (x).
            worldYStart = worldY;                                                                                       // Record current position before moving (y).
            checkColliding();                                                                                           // Check collision.
        }
    }


    /**
     * Sets this entity to a walking sprite.
     */
    public void setWalkingSprite() {

        if (!moving) {

            if (spriteNumLast == 2) {

                spriteNumCurrent = 3;
            } else {

                spriteNumCurrent = 2;
            }
        }
    }


    /**
     * Sets this entity to an idle sprite.
     */
    public void setIdleSprite() {

        if (!moving) {

            spriteNumCurrent = 1;

            if (spriteNumLast == 2) {                                                                                   // Swap which foot will step forward for the next walking cycle.

                spriteNumLast = 3;
            } else {

                spriteNumLast = 2;
            }
        }
    }


    /**
     * Resets combat stats (attack, defense, magic, and agility) to their base values by removing buffs.
     */
    public void resetStats() {

        setAttackBuff(0);
        setDefenseBuff(0);
        setMagicBuff(0);
        setAgilityBuff(0);
    }


    /**
     * Retrieves the sprite that matches this entity's current direction and sprite number.
     *
     * @return sprite
     */
    protected Sprite retrieveSprite() {

        Sprite sprite = null;

        switch (directionCurrent) {
            case UP:
                if (spriteNumCurrent == 1) {
                    sprite = up1;
                }
                if (spriteNumCurrent == 2) {
                    sprite = up2;
                }
                if (spriteNumCurrent == 3) {
                    sprite = up3;
                }
                break;
            case DOWN:
                if (spriteNumCurrent == 1) {
                    sprite = down1;
                }
                if (spriteNumCurrent == 2) {
                    sprite = down2;
                }
                if (spriteNumCurrent == 3) {
                    sprite = down3;
                }
                break;
            case LEFT:
                if (spriteNumCurrent == 1) {
                    sprite = left1;
                }
                if (spriteNumCurrent == 2) {
                    sprite = left2;
                }
                if (spriteNumCurrent == 3) {
                    sprite = left3;
                }
                break;
            case RIGHT:
                if (spriteNumCurrent == 1) {
                    sprite = right1;
                }
                if (spriteNumCurrent == 2) {
                    sprite = right2;
                }
                if (spriteNumCurrent == 3) {
                    sprite = right3;
                }
                break;
        }
        return sprite;
    }


    /**
     * Checks collision of this entity with other entities, landmarks, tiles, etc.
     */
    protected void checkColliding() {

        // Check tile collision.
        colliding = false;
        gp.getCollisionI().checkTile(this);

        // Check landmark collision.
        gp.getCollisionI().checkLandmark(this);

        // Check object collision.
        gp.getCollisionI().checkEntity(this, gp.getObj());

        // Check NPC collision.
        gp.getCollisionI().checkEntity(this, gp.getNpc());

        // Check party collision.
        gp.getCollisionI().checkEntity(this, gp.getParty());

        // Check player collision.
        gp.getCollisionI().checkPlayer(this);
    }


    /**
     * Sets the amount of time this entity must rest before it's able to enter a new state of motion.
     *
     * @param seconds number of seconds to rest
     */
    protected void setRest(double seconds) {

        rest = seconds;
    }


    /**
     * Updates this entity's world position by one frame if it's in a state of motion, using a walking animation.
     * If this entity reaches its target tile, it will exit the state of motion.
     *
     * @param dt time since last frame (seconds)
     */
    protected void updateAction(double dt) {

        if (moving) {

            worldCounter += speed * dt;                                                                                 // Add to the number of world units the entity has moved while in the current state of motion.

            if (!colliding) {                                                                                           // If colliding is false, the entity can move.

                updateWorldPosition(dt);
            }

            if (worldCounter <= GamePanel.NATIVE_TILE_SIZE / 2) {                                                       // Walking animation; entity will have a foot forward for half of the world units traversed.

                if (spriteNumLast == 2) {

                    spriteNumCurrent = 3;
                } else {

                    spriteNumCurrent = 2;
                }
            } else {

                spriteNumCurrent = 1;
            }

            if (worldCounter >= GamePanel.NATIVE_TILE_SIZE) {                                                           // Check if the entity has moved a number of world units equal to a tile size in the current state of motion.

                moving = false;                                                                                         // If a tile's length has been traversed, the entity exits a state of motion.
                worldCounter = 0;                                                                                       // Reset the world unit counter.
                worldX = worldXEnd;                                                                                     // Set entity position as target position (in case it was slightly overshot).
                worldY = worldYEnd;                                                                                     // ^^^
                worldXLast = worldXStart;                                                                               // Store the last position the entity was at.
                worldYLast = worldYStart;                                                                               // ^^^
                worldXStart = worldXEnd;
                worldYStart = worldYEnd;

                if (spriteNumLast == 2) {                                                                               // Swap which foot will step forward for the next walking cycle.

                    spriteNumLast = 3;
                } else {

                    spriteNumLast = 2;
                }
            }
        }
    }


    /**
     * Updates the world position of this entity according to its speed in its current direction.
     *
     * @param dt time since last frame (seconds)
     */
    protected void updateWorldPosition(double dt) {

        switch (directionCurrent) {                                                                                     // Entity will change position in the appropriate direction.
            case UP:
                worldY -= speed * dt;
                break;
            case DOWN:
                worldY += speed * dt;
                break;
            case LEFT:
                worldX -= speed * dt;
                break;
            case RIGHT:
                worldX += speed * dt;
                break;
        }
    }


    /**
     * Sets this entity's behavior.
     * Override this method in implemented entity classes if custom actions are desired.
     *
     * @param dt time since last frame (seconds)
     */
    protected void setAction(double dt) {

        switch (defaultIdle) {
            case RANDOM_STEPS:
                actionRandomSteps(dt);
                break;
            case RANDOM_TURNS:
                actionRandomTurns(dt);
                break;
        }
    }


    /**
     * Searches for a path that this entity will follow to reach a specified goal tile.
     * This initiates a new state of motion if this entity is not already in one.
     *
     * @param goalCol column of the goal tile
     * @param goalRow row of the goal tile
     */
    protected void searchPath(int goalCol, int goalRow) {

        int startCol = (int)(worldX / GamePanel.NATIVE_TILE_SIZE);                                                      // The  column that the entity starts in.
        int startRow = (int)(worldY / GamePanel.NATIVE_TILE_SIZE);                                                      // The row that the entity starts in.

        gp.getPathF().setNodes(startCol, startRow, goalCol, goalRow);                                                   // Pass in the necessary data to the A* pathfinding algorithm to search for a valid path.

        if (gp.getPathF().search(this)) {                                                                               // If this statement is true, a path has been found.

            int nextWorldX = gp.getPathF().getPathList().get(0).getCol() * GamePanel.NATIVE_TILE_SIZE;
            int nextWorldY = gp.getPathF().getPathList().get(0).getRow() * GamePanel.NATIVE_TILE_SIZE;

            if ((worldY > nextWorldY) && (worldX == nextWorldX)) {                                                      // Check if the entity can move up.
                autoStep(EntityDirection.UP);

            } else if ((worldY < nextWorldY) && (worldX == nextWorldX)) {                                               // Check if the entity can move down.
                autoStep(EntityDirection.DOWN);

            } else if ((worldY == nextWorldY) && (worldX > nextWorldX)) {                                               // Check if the entity can move left.
                autoStep(EntityDirection.LEFT);

            } else if ((worldY == nextWorldY) && (worldX < nextWorldX)) {                                               // Check if the entity can move right.
                autoStep(EntityDirection.RIGHT);

            } else {                                                                                                    // The entity has arrived at the goal.
                stopFollowingPath();
            }
        } else {

            stopFollowingPath();                                                                                     // A path to the goal was not found, so exit this state.

            if ((startCol != goalCol) && (startRow != goalRow)) {

                onEntity = false;                                                                                       // A path to the target entity was not found, so exit this state.
                onEntityId = 0;                                                                                         // Reset the target entity.
                UtilityTool.logWarning("Entity"
                        + (((name != null) && (!name.equals(""))) ? (name + " ") : "")
                        + " with ID "
                        + entityId
                        + " was not able to find a path to the following destination: row "
                        + goalRow
                        + ", column "
                        + goalCol
                        + ", map ID "
                        + gp.getLoadedMap().getMapId()
                        + ".");
            }
        }
    }


    /**
     * Action: Sets this entity to follow another entity.
     *
     * @param dt time since last frame (seconds)
     * @param entityId ID of the entity to be followed
     */
    protected void actionFollowEntity(double dt, int entityId) {

        EntityBase target = gp.getEntityById(entityId);

        if (target != null) {

            int goalCol = target.getColLast();                                                                          // Set the goal to be the target entity's last position.
            int goalRow = target.getRowLast();                                                                          // ^^^
            actionPath(dt, goalCol, goalRow);                                                                           // Initiate a pathfinding operation.
        } else {

            onEntity = false;                                                                                           // Entity to follow does not exist, so exit this state.

            UtilityTool.logWarning("Target entity with ID "
                    + entityId
                    + " does not exist, so a path to follow the target was not generated.");
        }
    }


    /**
     * Action: Sets this entity to find and follow a path to a specified goal tile.
     *
     * @param dt time since last frame (seconds)
     * @param goalCol column of the goal tile
     * @param goalRow row of the goal tile
     */
    protected void actionPath(double dt, int goalCol, int goalRow) {

        if (!moving) {

            searchPath(goalCol, goalRow);
        }
        updateAction(dt);                                                                                               // The entity's position on the map will be updated each frame while `onPath` is true (i.e., it will continue walking).
    }


    /**
     * Action: Sets this entity to take a step to a random adjacent tile that's available.
     *
     * @param dt time since last frame (seconds)
     */
    protected void actionRandomSteps(double dt) {

        if (rest > 0) {

            rest -= dt;                                                                                                 // Decrease rest time by one frame each time a new frame is rendered.
        }
        boolean movingFlag = moving;                                                                                    // Set a flag to see if the entity entered this method in a state of motion.

        if (rest <= 0) {

            initiateRandomStep();
        }
        updateAction(dt);

        if ((!moving) && (movingFlag)) {                                                                                // If this statement is true, it means the entity exited a state a motion (i.e., concluded a step) while in this method.

            setRest(2);                                                                                                 // Set the number of seconds the entity must wait before entering a new state of motion.
        }
    }


    /**
     * Action: Sets this entity to turn in a random direction.
     *
     * @param dt time since last frame (seconds)
     */
    protected void actionRandomTurns(double dt) {

        if (moving) {

            updateAction(dt);                                                                                           // If entity is currently in a state of motion, it needs to finish that before doing anything else.
            return;
        }

        if (rest > 0) {

            rest -= dt;
        }

        if (rest <= 0) {

            directionCandidate = selectRandomDirection();

            if (directionCandidate != directionCurrent) {

                directionCurrent = directionCandidate;
                directionLast = directionCurrent;
                setRest(2);                                                                                             // Set the number of seconds the entity must wait before entering a new state of motion.
            }
        }
    }


    /**
     * Selects a random entity direction.
     *
     * @return random entity direction
     */
    protected EntityDirection selectRandomDirection() {

        Random random = new Random();
        int i = random.nextInt(100) + 1;                                                                                // Generate random number from 1 to 100 (both inclusive).

        if (i <= 25) {
            return EntityDirection.UP;
        }

        if ((i > 25) && (i <= 50)) {
            return EntityDirection.DOWN;
        }

        if ((i > 50) && (i < 75)) {
            return EntityDirection.LEFT;
        }

        if (i > 75) {
            return EntityDirection.RIGHT;
        }

        return EntityDirection.DOWN;                                                                                    // Default value to return.
    }


    /**
     * Selects the direction this entity's next step will be taken in as part of a random step action.
     * This initiates a new state of motion if this entity is not already in one and is not resting.
     */
    protected void initiateRandomStep() {

        if (!moving) {                                                                                                  // If the entity is static (i.e., not in a state of motion), it's free to make a new move.

            directionCandidate = selectRandomDirection();                                                               // Generate a random direction for the entity to step in.

            checkColliding();                                                                                           // Check collision.

            if (!colliding) {

                switch (directionCandidate) {
                    case UP:
                        worldXEnd = worldX;
                        worldYEnd = worldY - GamePanel.NATIVE_TILE_SIZE;
                        break;
                    case DOWN:
                        worldXEnd = worldX;
                        worldYEnd = worldY + GamePanel.NATIVE_TILE_SIZE;
                        break;
                    case LEFT:
                        worldXEnd = worldX - GamePanel.NATIVE_TILE_SIZE;
                        worldYEnd = worldY;
                        break;
                    case RIGHT:
                        worldXEnd = worldX + GamePanel.NATIVE_TILE_SIZE;
                        worldYEnd = worldY;
                        break;
                }

                moving = true;                                                                                          // If the entity isn't going to collide with anything, allow it to move.
                worldXStart = worldX;                                                                                   // Record current position before moving (x).
                worldYStart = worldY;                                                                                   // Record current position before moving (y).
                directionCurrent = directionCandidate;
                directionLast = directionCandidate;
            }
        }
    }


    /**
     * Calculates and sets this entity's total attack based on its base attack and potential equipments/buffs it has.
     *
     * @return calculated total attack
     */
    private void calculateAttack() {

        int calculated = (int)Math.floor((baseAttack * attackBuff) + baseAttack);

        if (calculated >= 0) {

            attack = calculated;
        } else {

            attack = 0;
        }
    }


    /**
     * Calculates and sets this entity's total defense based on its base defense and potential equipment/buffs it has.
     *
     * @return calculated total defense
     */
    private void calculateDefense() {

        int calculated = (int)Math.floor((baseDefense * defenseBuff) + baseDefense);

        if (calculated >= 0) {

            defense = calculated;
        } else {

            defense = 0;
        }
    }


    /**
     * Calculates and sets this entity's total magic based on its base magic and potential equipment/buffs it has.
     *
     * @return calculated total magic
     */
    private void calculateMagic() {

        int calculated = (int)Math.floor((baseMagic * magicBuff) + baseMagic);

        if (calculated >= 0) {

            magic = calculated;
        } else {

            magic = 0;
        }
    }


    /**
     * Calculates and sets this entity's total agility based on its base agility and potential equipment/buffs it has.
     *
     * @return calculated total agility
     */
    private void calculateAgility() {

        int calculated = (int)Math.floor((baseAgility * agilityBuff) + baseAgility);

        if (calculated >= 0) {

            agility = calculated;
        } else {

            agility = 0;
        }
    }


    // GETTERS
    public int getEntityId() {
        return entityId;
    }

    public EntityType getType() {
        return type;
    }

    public boolean hasCollision() {
        return collision;
    }

    public Sprite getUp1() {
        return up1;
    }

    public Sprite getUp2() {
        return up2;
    }

    public Sprite getUp3() {
        return up3;
    }

    public Sprite getDown1() {
        return down1;
    }

    public Sprite getDown2() {
        return down2;
    }

    public Sprite getDown3() {
        return down3;
    }

    public Sprite getLeft1() {
        return left1;
    }

    public Sprite getLeft2() {
        return left2;
    }

    public Sprite getLeft3() {
        return left3;
    }

    public Sprite getRight1() {
        return right1;
    }

    public Sprite getRight2() {
        return right2;
    }

    public Sprite getRight3() {
        return right3;
    }

    public float getWorldX() {
        return worldX;
    }

    public float getWorldY() {
        return worldY;
    }

    public float getWorldXStart() {
        return worldXStart;
    }

    public float getWorldYStart() {
        return worldYStart;
    }

    public float getWorldXEnd() {
        return worldXEnd;
    }

    public float getWorldYEnd() {
        return worldYEnd;
    }

    public  float getWorldXLast() {
        return worldXLast;
    }

    public float getWorldYLast() {
        return worldYLast;
    }

    public int getCol() {
        return (int)(worldX / GamePanel.NATIVE_TILE_SIZE);
    }

    public int getRow() {
        return (int)(worldY / GamePanel.NATIVE_TILE_SIZE);
    }

    public int getColStart() {
        return (int)(worldXStart / GamePanel.NATIVE_TILE_SIZE);
    }

    public int getRowStart() {
        return (int)(worldYStart / GamePanel.NATIVE_TILE_SIZE);
    }

    public int getColEnd() {
        return (int)(worldXEnd / GamePanel.NATIVE_TILE_SIZE);
    }

    public int getRowEnd() {
        return (int)(worldYEnd / GamePanel.NATIVE_TILE_SIZE);
    }

    public int getColLast() {
        return (int)(worldXLast / GamePanel.NATIVE_TILE_SIZE);
    }

    public int getRowLast() {
        return (int)(worldYLast / GamePanel.NATIVE_TILE_SIZE);
    }

    public EntityDirection getDirectionCurrent() {
        return directionCurrent;
    }

    public EntityDirection getDirectionLast() {
        return directionLast;
    }

    public EntityDirection getDirectionCandidate() {
        return directionCandidate;
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isTurning() {
        return turning;
    }

    public boolean isColliding() {
        return colliding;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isOnPath() {
        return onPath;
    }

    public boolean isOnEntity() {
        return onEntity;
    }

    public int getOnEntityId() {
        return onEntityId;
    }

    public String getName() {
        return name;
    }

    public int getMaxLife() {
        return maxLife;
    }

    public int getLife() {
        return life;
    }

    public int getMaxSkillPoints() {
        return maxSkillPoints;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public int getBaseMagic() {
        return baseMagic;
    }

    public int getBaseAgility() {
        return baseAgility;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getMagic() {
        return magic;
    }

    public double getAttackBuff() {
        return attackBuff;
    }

    public double getDefenseBuff() {
        return defenseBuff;
    }

    public double getMagicBuff() {
        return magicBuff;
    }

    public double getAgilityBuff() {
        return agilityBuff;
    }

    public int getAgility() {
        return agility;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public int getNextLevelExp() {
        return nextLevelExp;
    }

    public LimitedArrayList<MoveBase> getMoves() {
        return moves;
    }


    // SETTERS
    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public void setWorldX(int worldX) {
        this.worldX = worldX;
        this.worldXStart = worldX;
        this.worldXEnd = worldX;
        this.worldXLast = worldX;
    }

    public void setWorldY(int worldY) {
        this.worldY = worldY;
        this.worldYStart = worldY;
        this.worldYEnd = worldY;
        this.worldYLast = worldY;
    }

    public void setWorldXEnd(int worldXEnd) {
        this.worldXEnd = worldXEnd;
    }

    public void setWorldYEnd(int worldYEnd) {
        this.worldYEnd = worldYEnd;
    }

    public void setWorldXLast(int worldXLast) {
        this.worldXLast = worldXLast;
    }

    public void setWorldYLast(int worldYLast) {
        this.worldYLast = worldYLast;
    }

    public void setCol(int col) {
        worldX = col * GamePanel.NATIVE_TILE_SIZE;
        worldXStart = col * GamePanel.NATIVE_TILE_SIZE;
        worldXEnd = col * GamePanel.NATIVE_TILE_SIZE;
        worldXLast = col * GamePanel.NATIVE_TILE_SIZE;
    }

    public void setRow(int row) {
        worldY = row * GamePanel.NATIVE_TILE_SIZE;
        worldYStart = row * GamePanel.NATIVE_TILE_SIZE;
        worldYEnd = row * GamePanel.NATIVE_TILE_SIZE;
        worldYLast = row * GamePanel.NATIVE_TILE_SIZE;
    }

    public void setColEnd(int col) {
        worldXEnd = col * GamePanel.NATIVE_TILE_SIZE;
    }

    public void setRowEnd(int row) {
        worldYEnd = row * GamePanel.NATIVE_TILE_SIZE;
    }

    public void setColLast(int col) {
        worldXLast = col * GamePanel.NATIVE_TILE_SIZE;
    }

    public void setRowLast(int row) {
        worldYLast = row * GamePanel.NATIVE_TILE_SIZE;
    }

    public void setDirectionCurrent(EntityDirection directionCurrent) {
        this.directionCurrent = directionCurrent;
        directionLast = directionCurrent;
    }

    public void setDirectionCandidate(EntityDirection directionCandidate) {
        this.directionCandidate = directionCandidate;
    }

    public void setColliding(boolean colliding) {
        this.colliding = colliding;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setDefaultAction(DefaultIdle defaultIdle) {
        this.defaultIdle = defaultIdle;
    }

    public void startFollowingPath(int goalCol, int goalRow) {
        if (!onEntity) {
            this.onPathGoalCol = goalCol;
            this.onPathGoalRow = goalRow;
            this.onPath = true;
        }
    }

    public void stopFollowingPath() {
        onPathGoalCol = 0;
        onPathGoalRow = 0;
        onPath = false;
    }

    public void startFollowingEntity(int entityId) {
        if (this.entityId != entityId) {                                                                                // Ensure that we're not trying to make the entity follow itself.
            EntityBase target = gp.getEntityById(entityId);
            if (target != null) {
                EntityBase followed = gp.getEntityById(gp.getEventM().checkEntityChainDown(target));                    // If a chain of followers is following the target entity, then this entity will be placed at the back of the chain (i.e., this entity will actually follow the entity at the end of the chain).
                onEntityId = followed.getEntityId();
                onEntity = true;
                followed.setColLast(this.getCol());                                                                     // The follower will always find a path to the followed's last position; setting it this way prevents the follower from instantly moving once following begins.
                followed.setRowLast(this.getRow());                                                                     // Same as previous comment.
            }
        }
    }

    public void stopFollowingEntity() {
        onEntityId = -1;
        onEntity = false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpeed(int speed) {
        if (speed > 0) {
            this.speed = speed;
        }
    }

    public void setMaxLife(int maxLife) {
        if (maxLife >= 0) {
            this.maxLife = maxLife;
        }
    }

    public void setLife(int life) {
        if ((life >= 0) && (life <= maxLife)) {
            this.life = life;
        } else if (life < 0) {
            this.life = 0;
        } else if (life > maxLife) {
            this.life = maxLife;
        }
    }

    public void addLife(int addition) {
        int result = life + addition;
        if ((result >= 0) && (result <= maxLife)) {
            life = result;
        } else if (result > maxLife) {
            life = maxLife;
        } else if (result < 0) {
            life = 0;
        }
    }

    public void subtractLife(int subtraction) {
        int result = life - subtraction;
        if ((result >= 0) && (result <= maxLife)) {
            life = result;
        } else if (result < 0) {
            life = 0;
        } else if (result > maxLife) {
            life = maxLife;
        }
    }

    public void setMaxSkillPoints(int maxSkillPoints) {
        if (maxSkillPoints >= 0) {
            this.maxSkillPoints = maxSkillPoints;
        }
    }

    public void setSkillPoints(int skillPoints) {
        if ((skillPoints >= 0) && (skillPoints <= maxSkillPoints)) {
            this.skillPoints = skillPoints;
        } else if (skillPoints < 0) {
            this.skillPoints = 0;
        } else if (skillPoints > maxSkillPoints) {
            this.skillPoints = maxSkillPoints;
        }
    }

    public void addSkillPoints(int addition) {
        int result = skillPoints + addition;
        if ((result >= 0) && (result <= maxSkillPoints)) {
            skillPoints = result;
        } else if (result > maxSkillPoints) {
            skillPoints = maxSkillPoints;
        } else if (result < 0) {
            skillPoints = 0;
        }
    }

    public void subtractSkillPoints(int subtraction) {
        int result = skillPoints - subtraction;
        if ((result >= 0) && (result <= maxSkillPoints)) {
            skillPoints = result;
        } else if (result < 0) {
            skillPoints = 0;
        } else if (result > maxSkillPoints) {
            skillPoints = maxSkillPoints;
        }
    }

    public void setBaseAttack(int baseAttack) {
        if (baseAttack >= 0) {
            this.baseAttack = baseAttack;
        } else {
            this.baseAttack = 0;
        }
        calculateAttack();
    }

    public void setBaseDefense(int baseDefense) {
        if (baseDefense >= 0) {
            this.baseDefense = baseDefense;
        } else {
            this.baseDefense = 0;
        }
        calculateDefense();
    }

    public void setBaseMagic(int baseMagic) {
        if (baseMagic >= 0) {
            this.baseMagic = baseMagic;
        } else {
            this.baseMagic = 0;
        }
        calculateMagic();
    }

    public void setBaseAgility(int baseAgility) {
        if (baseAgility >= 0) {
            this.baseAgility = baseAgility;
        } else {
            this.baseAgility = 0;
        }
        calculateAgility();
    }

    public void setAttackBuff(int attackBuff) {
        this.attackBuff = attackBuff;
        calculateAttack();
    }

    public void setDefenseBuff(int defenseBuff) {
        this.defenseBuff = defenseBuff;
        calculateDefense();
    }

    public void setMagicBuff(int magicBuff) {
        this.magicBuff = magicBuff;
        calculateMagic();
    }

    public void setAgilityBuff(int agilityBuff) {
        this.agilityBuff = agilityBuff;
        calculateAgility();
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    public void setExp(int exp) {
        if (exp >= 0) {
            this.exp = exp;
        } else {
            this.exp = 0;
        }
    }

    public void setLevel(int level) {
        if (level >= 0) {
            this.level = level;
        } else {
            this.level = 0;
        }
    }

    public void setNextLevelExp(int nextLevelExp) {
        if (nextLevelExp >= 0) {
            this.nextLevelExp = nextLevelExp;
        } else {
            this.nextLevelExp = 0;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityBase oEntity = (EntityBase)o;
        return entityId == oEntity.entityId;
    }
}
