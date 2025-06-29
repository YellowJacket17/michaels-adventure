package entity;

import combat.MoveBase;
import core.GamePanel;
import entity.enumeration.*;
import event.enumeration.StockStepInteractionType;
import render.Renderer;
import asset.Sprite;
import render.enumeration.ZIndex;
import render.drawable.Drawable;
import utility.LimitedArrayList;
import utility.UtilityTool;

import java.util.Random;

/**
 * This abstract class defines base logic for all entities, including the player, characters, and objects.
 */
public abstract class EntityBase extends Drawable {

    /*
     * An entity can be one of two types:
     *    - Character (NPC, party member, or player)
     *    - Object (ex. a pickup item in the overworld)
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
     * New EntityBase instances of both types are loaded from JSON data via the JsonParser class.
     * Each EntityBase instance is assigned a unique ID, determined by ordering in the JSON file.
     * This unique ID can be used to set logic for a specific entity for things like interactions, etc.
     *
     * When creating new character classes that extend this class, model them after the classes in the `npc` package.
     * When creating new object classes that extend this class, model them after the classes in the `object` package.
     *
     * In the `update()` method, the actions at the top take precedent over all other actions, in their order of
     * importance.
     * If one of these actions is triggered, then no further actions will be checked.
     * For example, if an entity is in combat, then no pathfinding logic will run, either for following an entity or
     * following a path to a specified tile.
     *
     * Please note the following:
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
     * Entity ID.
     * To be clear, this ID is unique for each EntityBase instance.
     * Each EntityBase instance should have its own unique ID.
     */
    protected final int entityId;

    /**
     * Type of entity (character or object).
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
    protected Sprite
            idleDown, walkDown1, walkDown2,
            idleUp, walkUp1, walkUp2,
            idleLeft, walkLeft1, walkLeft2,
            idleRight, walkRight1, walkRight2,
            combatStanceLeft1, combatStanceLeft2,
            combatStanceRight1, combatStanceRight2,
            combatAttackRight, combatAttackLeft,
            combatFaintLeft1_1, combatFaintLeft1_2, combatFaintRight1_1, combatFaintRight1_2,
            combatFaintLeft2_1, combatFaintLeft2_2, combatFaintRight2_1, combatFaintRight2_2,
            combatFaintLeft3_1, combatFaintLeft3_2, combatFaintRight3_1, combatFaintRight3_2,
            combatFaintLeft4_1, combatFaintLeft4_2, combatFaintRight4_1, combatFaintRight4_2,
            combatFaintLeft5_1, combatFaintLeft5_2, combatFaintRight5_1, combatFaintRight5_2,
            combatFaintLeft6_1, combatFaintLeft6_2, combatFaintRight6_1, combatFaintRight6_2;


    /**
     * Boolean tracking whether a render error has occurred.
     * If true, this prevents a render error from repeatedly being printed to the console.
     */
    protected boolean renderError = false;


    // STATE
    /**
     * Entity's current world position.
     * This is the top-left corner of the tile (i.e., column/row) occupied by this entity.
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
     * This variable is primarily used when generating directions for entities to move in before confirming it's
     * actually feasible.
     */
    protected EntityDirection directionCandidate;

    /**
     * Current walking sprite number to render for a given direction.
     * This is used for walking animation to distinguish whether the left or right foot should be forward,
     * regardless of direction of travel.
     * A value of '1' represents idle.
     * A value of '2' represents left foot forward.
     * A value of '3' represents right foot forward.
     */
    protected int walkSpriteNumCurrent = 1;

    /**
     * Last sprite number rendered for a given direction.
     * This is primarily used for walking animation.
     */
    protected int walkSpriteNumLast = 2;

    /**
     * Current combat stance sprite number to render for a given direction.
     * This is used for combat stance animation to distinguish which frame of the animation to render, regardless of
     * whether this entity is facing left or right.
     * A value of '1' represents first frame.
     * A value of '2' represents second frame.
     */
    protected int combatStanceSpriteNumCurrent = 1;

    /**
     * Current combat attack sprite number to render for a given direction.
     * This is used for combat attack animation to distinguish which frame of animation to render, regardless of whether
     * this entity is facing left or right.
     * In practice, there is only one frame for combat attack animation.
     */
    protected int combatAttackSpriteNumCurrent = 1;

    /**
     * Current combat faint sprite number to render for a given direction.
     * This is used for combat faint animation to distinguish which frame of animation to render, regardless of whether
     * this entity is facing left or right.
     * A value of '1' represents the first frame, a value of '2' represents the second frame, etc.
     * The last frame (a value of '6') represents the final "resting" faint sprite.
     */
    protected int combatFaintSpriteNumCurrent = 6;

    /**
     * Boolean indicating whether this entity is currently in a state of motion or not (used for grid-based movement).
     */
    protected boolean moving = false;

    /**
     * Boolean indicating whether this entity is currently in a state of turning or not.
     * This is primarily used when the player entity turns (i.e., changes direction) from a static state.
     */
    protected boolean turning = false;

    /**
     * Boolean indicating whether this entity is currently in a state of combating or not.
     * Note that this does not indicate whether this entity is a player-side or non-player-side combating entity.
     */
    protected boolean combating = false;

    /**
     * Boolean indicating whether this entity is currently in a state of conversing or not.
     */
    protected boolean conversing = false;

    /**
     * Boolean indicating whether this entity is currently in a state of colliding or not.
     * In other words, this boolean indicates whether this entity is currently colliding with something (other entity,
     * landmark, tile, etc.) that has solid collision.
     */
    protected boolean colliding = false;

    /**
     * Boolean to set whether this entity is rendered on screen or not.
     * Hidden entities cannot be interacted with (no collision detection, etc.).
     */
    protected boolean hidden = false;

    /**
     * Default action/behavior that this entity will do when not being interacted with by the player or some other
     * event.
     */
    protected DefaultAction defaultAction = DefaultAction.STATIC;

    /**
     * Boolean indicating whether this entity is currently performing a combat attack animation.
     */
    protected boolean playingCombatAttackAnimation = false;

    /**
     * Boolean indicating whether this entity is currently performing a combat faint animation.
     */
    protected boolean playingCombatFaintAnimation = false;

    /**
     * Boolean indicating whether this entity is currently in a state of hopping or not.
     */
    protected boolean hopping = false;


    // PATHFINDING
    /**
     * Boolean setting whether this entity is currently following a path or not.
     * Note that this variable is not used when this entity is following another entity.
     * It is only used when a path to an arbitrary goal in the world is being followed.
     * If an entity is being followed, this variable will always be set to a default value of false.
     */
    protected boolean onPath = false;

    /**
     * Goal for this entity to reach when following a path.
     * The default value is zero when no path is being followed.
     * Note that this variable is not used when this entity is following another entity.
     * It is only used when a path to an arbitrary foal in the world is being followed.
     * If an entity is being followed, this variable will always be set to a default value of zero.
     */
    protected int onPathGoalCol, onPathGoalRow;

    /**
     * ID of the entity that this entity is following.
     */
    protected int onEntityId = NO_ENTITY_FOLLOWED;

    /**
     * Argument to be passed when this entity is not following another entity.
     */
    protected static final int NO_ENTITY_FOLLOWED = 33712339;


    // FADE EFFECT
    /**
     * Variable to store the active fade effect type being performed by this entity (null if none).
     */
    protected FadeEffectType activeFadeEffect = FadeEffectType.NONE;

    /**
     * Variable to store the calculated alpha change that will occur per second during an active fade effect.
     */
    protected double fadeEffectAlphaPerSecond;


    // COUNTERS/BUFFERS
    /**
     * Counts the number of world units this entity has moved thus far while in a state of motion.
     * In practice, this is used to track whether a tile's length has been traversed yet or not.
     */
    protected double worldCounter;

    /**
     * Controls the number of seconds this entity must wait before executing its default action.
     */
    protected double rest;

    /**
     * Counts time passed (seconds) while this entity is animating.
     * This is used to control animations, such as the pace at which to swap sprites.
     * In practice, this is used for idle combat animation and fainting combat animation.
     * Note that the walking animation (i.e., animation while in a state of motion) does not use this counter, instead
     * using `worldCounter`.
     */
    protected double animationCounter;

    /**
     * Maximum number of seconds allocated to a full combat stance animation cycle.
     * Increasing this value will extend the duration of the combat stance animation cycle.
     * In other words, increasing this value will make the combat stance animation appear to run more slowly.
     */
    protected double animationCounterCombatStanceMax = 0.8;

    /**
     * Maximum number of seconds allocated to a full combat attack animation cycle.
     * Increasing this value will extend the duration of the combat attack animation cycle.
     * In other words, increasing this value will make the combat attack animation appear to run more slowly.
     */
    protected double animationCounterCombatAttackMax = 0.6;

    /**
     * Maximum number of seconds allocated to a full combat faint animation.
     * Increasing this value will extend the duration of the combat faint animation.
     * In other words, increasing this value will make the combat faint animation appear to run more slowly.
     */
    protected double animationCounterCombatFaintMax = 0.6;


    // BASIC ATTRIBUTES
    /**
     * Entity name.
     */
    protected String name = "???";

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
     * Primary attribute.
     */
    protected int maxSkill;

    /**
     * Entity's remaining available skill points for moves in combat.
     * Primary attribute.
     */
    protected int skill;

    /**
     * Entity's base attack stat.
     * Secondary attribute.
     */
    protected int baseAttack;

    /**
     * Entity's base defense stat.
     * Secondary attribute.
     */
    protected int baseDefense;

    /**
     * Entity's base magic stat.
     * Secondary attribute.
     */
    protected int baseMagic;

    /**
     * Entity's base agility stat.
     * Secondary attribute.
     */
    protected int baseAgility;

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
     * Entity's status (healthy, faint, etc.).
     */
    protected EntityStatus status = EntityStatus.HEALTHY;

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
     * @param entityId entity ID (unique to each instance)
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
        setSprites();
    }


    // METHODS
    /**
     * Updates the state of this entity by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        // These are core actions that take precedent over all others.
        if (hidden) {return;}

        if (activeFadeEffect != FadeEffectType.NONE) {
            updateFadeEffect(dt);
        }

        if (playingCombatAttackAnimation) {
            updateCombatAttackAnimation(dt);
            return;
        }

        if (playingCombatFaintAnimation) {
            updateCombatFaintAnimation(dt);
            return;
        }

        if (combating) {
            updateCombatStanceAnimation(dt);
            return;
        }

        if (hopping) {
            updateHoppingState(dt);
            return;
        }

        if (conversing) {return;}

        if (isOnEntity()) {
            actionFollowEntity(dt, onEntityId);
            return;
        }

        if (onPath) {
            actionPath(dt, onPathGoalCol, onPathGoalRow);
            return;
        }

        // Set other actions.
        setAction(dt);
    }


    /**
     * Adds this entity to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        if (!hidden && gp.isRenderWorld() && !gp.getIllustrationS().isIllustrationActive()) {

            if (playingCombatFaintAnimation || (combating && (status == EntityStatus.FAINT))) {

                addToRenderPipelineFaint(renderer);
            } else {

                addToRenderPipelineStandard(renderer);
            }
        }

//        if (hidden) {
//
//            renderer.addRectangle(new Vector4f(255, 255, 255, 255),
//                    new Transform(
//                            new Vector2f(worldX, worldY),
//                            new Vector2f(32.0f, 32.0f)),
//                    ZIndex.THIRD_LAYER
//            );
//        }
    }


    /**
     * Cancels this entity's current action (i.e., exits its current state of motion or turning and returns to its
     * previous tile, if applicable).
     * The rest time until a new default action can execute is reset to zero.
     */
    public void cancelAction() {

        moving = false;
        turning = false;
        hopping = false;
        worldX = worldXStart;
        worldY = worldYStart;
        worldCounter = 0;
        directionCurrent = directionLast;
        walkSpriteNumCurrent = 1;
        rest = 0;
    }


    /**
     * Forces this entity to enter a new state of motion: step to an adjacent tile in the specified direction.
     * Note that the entity will be forced to move in the given direction regardless of if the tile being moved to has
     * collision (i.e., is solid).
     * In other words, this entity's collision state is not calculated and applied in this method.
     * It is recommended that this method be called after it has been determined that the given direction is valid.
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
            gp.getEventM().handleStockStepInteraction(getColEnd(), getRowEnd(), entityId,
                    StockStepInteractionType.GRASS_RUSTLE);
            gp.getEventM().handleStockStepInteraction(getColEnd(), getRowEnd(), entityId,
                    StockStepInteractionType.LEDGE_HOP);
        }
    }


    /**
     * Stages this entity to render in an idle sprite.
     * Note that this only stages the sprite number to be retrieved when rendering and does not set the actual sprite.
     * This will only be rendered if this entity is in a state of combating and not playing any other scripted
     * animation (ex. attacking animation).
     */
    public void stageIdleSprite() {

        if (!moving) {

            walkSpriteNumCurrent = 1;

            if (walkSpriteNumLast == 2) {                                                                               // Swap which foot will step forward for the next walking cycle.

                walkSpriteNumLast = 3;
            } else {

                walkSpriteNumLast = 2;
            }
        }
    }


    /**
     * Stages this entity to render in a walking sprite.
     * Either this entity's left or right foot will be forward, depending on which was forward last.
     * Note that this only stages the sprite number to be retrieved when rendering and does not set the actual sprite.
     * This will only be rendered if this entity is in a state of combating and not playing any other scripted
     * animation (ex. attacking animation).
     */
    public void stageWalkingSprite() {

        if (!moving) {

            if (walkSpriteNumLast == 2) {

                walkSpriteNumCurrent = 3;
            } else {

                walkSpriteNumCurrent = 2;
            }
        }
    }


    /**
     * Stages this entity to render in a combat stance sprite.
     * Note that this only stages the sprite number to be retrieved when rendering and does not set the actual sprite.
     * This will only be rendered if this entity is in a state of combating and not playing any other scripted
     * animation (ex. attacking animation).
     */
    public void stageCombatStanceSprite() {

        if (animationCounter < animationCounterCombatStanceMax / 2) {

            combatStanceSpriteNumCurrent = 1;
        } else {

            combatStanceSpriteNumCurrent = 2;
        }
    }


    /**
     * Stages this entity to render in a combat attack sprite.
     * Note that this only stages the sprite number to be retrieved when rendering and does not set the actual sprite.
     * This will only be rendered if this entity is playing a combat attack animation.
     */
    public void stageCombatAttackSprite() {

        combatAttackSpriteNumCurrent = 1;
    }


    /**
     * Stages this entity to render in a combat faint sprite.
     * Note that this only stages the sprite number to be retrieved when rendering and does not set the actual sprite.
     * This will only be rendered if this entity is playing a combat faint animation.
     */
    public void stageCombatFaintSprite() {

        double oneFifthAnimationCounterMax = animationCounterCombatFaintMax / 5;

        if (animationCounter < oneFifthAnimationCounterMax) {

            combatFaintSpriteNumCurrent = 1;
        } else if (animationCounter < (oneFifthAnimationCounterMax * 2)) {

            combatFaintSpriteNumCurrent = 2;
        } else if (animationCounter < (oneFifthAnimationCounterMax * 3)) {

            combatFaintSpriteNumCurrent = 3;
        } else if (animationCounter < (oneFifthAnimationCounterMax * 4)) {

            combatFaintSpriteNumCurrent = 4;
        } else if (animationCounter < (oneFifthAnimationCounterMax * 5)) {

            combatFaintSpriteNumCurrent = 5;
        } else {

            combatFaintSpriteNumCurrent = 6;
        }
    }


    /**
     * Initiates a combat attack animation for this entity.
     * If the animation is already playing, nothing will happen.
     */
    public void initiateCombatAttackAnimation() {

        if (!playingCombatAttackAnimation) {

            if (playingCombatFaintAnimation) {

                playingCombatFaintAnimation = false;
            }
            playingCombatAttackAnimation = true;
            animationCounter = 0;
        }
    }


    /**
     * Initiates a combat faint animation for this entity.
     * If the animation is already playing, nothing will happen.
     */
    public void initiateCombatFaintAnimation() {

        if (!playingCombatFaintAnimation) {

            if (playingCombatAttackAnimation) {

                playingCombatAttackAnimation = false;
            }
            playingCombatFaintAnimation = true;
            animationCounter = 0;
        }
    }


    /**
     * Initiates a fade effect (up or down).
     * At the beginning of a fade up effect, this entity will be set to a visible state (i.e., not hidden).
     * At the end of a fade down effect, this entity will be set to a hidden state.
     * If a fade effect is already active, then nothing will happen.
     *
     * @param type type of fade effect (up or down)
     * @param duration fade effect duration (seconds)
     * @param preventAction whether this entity can execute its default action (true) or not (false) while the fade effect
     *                      is executing; will cancel any current action
     */
    public void initiateFadeEffect(FadeEffectType type, double duration, boolean preventAction) {

        if (activeFadeEffect == FadeEffectType.NONE) {

            switch (type) {
                case FADE_UP:
                    setAlpha(0);
                    activeFadeEffect = FadeEffectType.FADE_UP;
                    fadeEffectAlphaPerSecond = (255 - color.w) / duration;
                    hidden = false;
                    break;
                case FADE_DOWN:
                    activeFadeEffect = FadeEffectType.FADE_DOWN;
                    fadeEffectAlphaPerSecond = color.w / duration;
                    break;
            }

            if (preventAction && (rest < duration)) {

                cancelAction();
                setRest(duration);                                                                                      // Increase `rest` to ensure that a new default action cannot initiate during the fade effect.
            }
        }
    }


    /**
     * Initiates a hop.
     * This entity will enter both a state of hopping and a state of motion.
     * This entity will move downward (positive y-direction) two tiles during a hop.
     * Other directions for hopping are not supported.
     */
    public void initiateHop() {

        gp.getSoundS().playEffect("testEffect6");
        cancelAction();
        hopping = true;
        moving = true;
        worldXStart = worldX;
        worldYStart = worldY;
        worldXEnd = worldX;
        worldYEnd = worldY + (GamePanel.NATIVE_TILE_SIZE * 2);                                                          // An entity moves two tiles during a hop.
        directionCurrent = EntityDirection.DOWN;
        directionCandidate = EntityDirection.DOWN;
        directionLast = EntityDirection.DOWN;
    }


    /**
     * Resets primary combat attributes (life points, skill points, and status) to their maximum/default values.
     */
    public void resetPrimaryAttributes() {

        setLife(maxLife);
        setSkill(maxSkill);
        setStatus(EntityStatus.HEALTHY);
    }


    /**
     * Resets secondary combat attributes (attack, defense, magic, and agility) to their base values by removing buffs.
     */
    public void resetSecondaryAttributes() {

        setAttackBuff(0);
        setDefenseBuff(0);
        setMagicBuff(0);
        setAgilityBuff(0);
    }


    /**
     * Sets the amount of time this entity must rest before executing its default action/behavior.
     *
     * @param seconds number of seconds to rest
     */
    public void setRest(double seconds) {

        rest = seconds;
    }


    /**
     * Resets the amount of time this entity must rest before executing its default action/behavior to an initial,
     * default value.
     * Each default action may have its own value.
     * Uses include when this entity is first loaded into a map and/or when this entity exits a prescribed state.
     */
    public void resetDefaultActionInitialRest() {

        switch (defaultAction) {
            case RANDOM_STEPS:
                setRest(1);
                break;
            case RANDOM_TURNS:
                setRest(1);
                break;
            case STATIC:
                setRest(0);
                break;
        }
    }


    /**
     * Adds this entity to the render pipeline (standard sprites).
     *
     * @param renderer Renderer instance
     */
    protected void addToRenderPipelineStandard(Renderer renderer) {

        if (playingCombatAttackAnimation) {

            setCombatAttackSprite();
        } else if (combating) {

            setCombatStanceSprite();
        } else {

            setWalkingSprite();
        }

        if (sprite != null) {

            int worldXAdjustment = (GamePanel.NATIVE_TILE_SIZE / 2) - (sprite.getNativeWidth() / 2);                    // Amount in the x-direction that the sprite needs to be adjusted when rendered; ensures sprite is centered on occupied tile.
            int worldYAdjustment = -sprite.getNativeHeight() + GamePanel.NATIVE_TILE_SIZE;                              // Amount in the y-direction that the sprite needs to be adjusted when rendered; ensures bottom of sprite touches bottom of occupied tile.
            transform.position.x = worldX + worldXAdjustment;
            transform.position.y = worldY + worldYAdjustment;
            transform.scale.x = sprite.getNativeWidth();
            transform.scale.y = sprite.getNativeHeight();
            renderer.addDrawable(this, ZIndex.THIRD_LAYER);

        } else if (!renderError) {

            UtilityTool.logError("Failed to add entity "
                    + (((name != null) && (!name.equals(""))) ? ("'" + name + "' ") : "")
                    + "with ID '"
                    + entityId
                    + "' to the render pipeline: sprites may not have been properly loaded upon entity "
                    + "initialization.");
            renderError = true;
        }

    }


    /**
     * Adds this entity to the render pipeline (faint sprites).
     * A complete combat faint sprite spans across two sprites, which must both be added to the render pipeline, side by
     * side.
     *
     * @param renderer Renderer instance
     */
    protected void addToRenderPipelineFaint(Renderer renderer) {

        try {

            int worldXAdjustment = (GamePanel.NATIVE_TILE_SIZE / 2) - (sprite.getNativeWidth() / 2);                    // Amount in the x-direction that the sprite needs to be adjusted when rendered; ensures sprite is centered on occupied tile.
            int worldYAdjustment = -sprite.getNativeHeight() + GamePanel.NATIVE_TILE_SIZE + 4;                          // Amount in the y-direction that the sprite needs to be adjusted when rendered; note that faint sprites have sn additional +4 offset compared to standard sprites.

            switch (directionCurrent) {
                case LEFT:
                    setCombatFaintSpriteTwo();
                    break;
                case RIGHT:
                    setCombatFaintSpriteOne();
                    break;
            }
            transform.position.x = worldX + worldXAdjustment;
            transform.position.y = worldY + worldYAdjustment;
            transform.scale.x = sprite.getNativeWidth();
            transform.scale.y = sprite.getNativeHeight();
            renderer.addDrawable(this, ZIndex.THIRD_LAYER);

            switch (directionCurrent) {
                case LEFT:
                    worldXAdjustment -= sprite.getNativeWidth();
                    setCombatFaintSpriteOne();
                    break;
                case RIGHT:
                    worldXAdjustment += sprite.getNativeWidth();
                    setCombatFaintSpriteTwo();
                    break;
            }
            transform.position.x = worldX + worldXAdjustment;
            renderer.addDrawable(this, ZIndex.THIRD_LAYER);

        } catch (NullPointerException e) {

            if (!renderError) {

                UtilityTool.logError("Failed to add entity "
                        + (((name != null) && (!name.equals(""))) ? ("'" + name + "' ") : "")
                        + "with ID '"
                        + entityId
                        + "' to the render pipeline: sprites may not have been properly loaded upon entity "
                        + "initialization.");
                renderError = true;
            }
        }
    }


    /**
     * Sets loaded entity sprites.
     * Default sprite and default width and height, and default opacity (alpha) should be set here.
     * If not manually set, default opacity is completely opaque.
     */
    protected abstract void setSprites();


    /**
     * Sets this entity's sprite to match this entity's current direction and staged walking sprite number.
     */
    protected void setWalkingSprite() {

        switch (directionCurrent) {
            case UP:
                if (walkSpriteNumCurrent == 1) {
                    sprite = idleUp;
                } else if (walkSpriteNumCurrent == 2) {
                    sprite = walkUp1;
                } else if (walkSpriteNumCurrent == 3) {
                    sprite = walkUp2;
                }
                break;
            case DOWN:
                if (walkSpriteNumCurrent == 1) {
                    sprite = idleDown;
                } else if (walkSpriteNumCurrent == 2) {
                    sprite = walkDown1;
                } else if (walkSpriteNumCurrent == 3) {
                    sprite = walkDown2;
                }
                break;
            case LEFT:
                if (walkSpriteNumCurrent == 1) {
                    sprite = idleLeft;
                } else if (walkSpriteNumCurrent == 2) {
                    sprite = walkLeft1;
                } else if (walkSpriteNumCurrent == 3) {
                    sprite = walkLeft2;
                }
                break;
            case RIGHT:
                if (walkSpriteNumCurrent == 1) {
                    sprite = idleRight;
                } else if (walkSpriteNumCurrent == 2) {
                    sprite = walkRight1;
                } else if (walkSpriteNumCurrent == 3) {
                    sprite = walkRight2;
                }
                break;
        }
    }


    /**
     * Sets this entity's sprite to match this entity's current direction and staged combat stance sprite number.
     */
    protected void setCombatStanceSprite() {

        switch (directionCurrent) {
            case LEFT:
                if (combatStanceSpriteNumCurrent == 1) {
                    sprite = combatStanceLeft1;
                } else if (combatStanceSpriteNumCurrent == 2) {
                    sprite = combatStanceLeft2;
                }
                break;
            case RIGHT:
                if (combatStanceSpriteNumCurrent == 1) {
                    sprite = combatStanceRight1;
                } else if (combatStanceSpriteNumCurrent == 2) {
                    sprite = combatStanceRight2;
                }
                break;
        }
    }


    /**
     * Sets this entity's sprite to match this entity's current direction and staged combat attack sprite number.
     */
    protected void setCombatAttackSprite() {

        switch (directionCurrent) {
            case LEFT:
                sprite = combatAttackLeft;
                break;
            case RIGHT:
                sprite = combatAttackRight;
                break;
        }
    }


    /**
     * Sets this entity's sprite to match this entity's current direction and staged combat faint sprite number.
     * This method sets the sprite to be the first (left) half of the complete combat faint sprite.
     * As a reminder, a complete combat faint sprite spans across two sprites.
     */
    protected void setCombatFaintSpriteOne() {

        switch (directionCurrent) {
            case LEFT:
                switch (combatFaintSpriteNumCurrent) {
                    case 1:
                        sprite = combatFaintLeft1_1;
                        break;
                    case 2:
                        sprite = combatFaintLeft2_1;
                        break;
                    case 3:
                        sprite = combatFaintLeft3_1;
                        break;
                    case 4:
                        sprite = combatFaintLeft4_1;
                        break;
                    case 5:
                        sprite = combatFaintLeft5_1;
                        break;
                    case 6:
                        sprite = combatFaintLeft6_1;
                        break;
                }
                break;
            case RIGHT:
                switch (combatFaintSpriteNumCurrent) {
                    case 1:
                        sprite = combatFaintRight1_1;
                        break;
                    case 2:
                        sprite = combatFaintRight2_1;
                        break;
                    case 3:
                        sprite = combatFaintRight3_1;
                        break;
                    case 4:
                        sprite = combatFaintRight4_1;
                        break;
                    case 5:
                        sprite = combatFaintRight5_1;
                        break;
                    case 6:
                        sprite = combatFaintRight6_1;
                        break;
                }
                break;
        }
    }


    /**
     * Sets this entity's sprite to match this entity's current direction and staged combat faint sprite number.
     * This method sets the sprite to be the second (right) half of the complete combat faint sprite.
     * As a reminder, a complete combat faint sprite spans across two sprites.
     */
    protected void setCombatFaintSpriteTwo() {

        switch (directionCurrent) {
            case LEFT:
                switch (combatFaintSpriteNumCurrent) {
                    case 1:
                        sprite = combatFaintLeft1_2;
                        break;
                    case 2:
                        sprite = combatFaintLeft2_2;
                        break;
                    case 3:
                        sprite = combatFaintLeft3_2;
                        break;
                    case 4:
                        sprite = combatFaintLeft4_2;
                        break;
                    case 5:
                        sprite = combatFaintLeft5_2;
                        break;
                    case 6:
                        sprite = combatFaintLeft6_2;
                        break;
                }
                break;
            case RIGHT:
                switch (combatFaintSpriteNumCurrent) {
                    case 1:
                        sprite = combatFaintRight1_2;
                        break;
                    case 2:
                        sprite = combatFaintRight2_2;
                        break;
                    case 3:
                        sprite = combatFaintRight3_2;
                        break;
                    case 4:
                        sprite = combatFaintRight4_2;
                        break;
                    case 5:
                        sprite = combatFaintRight5_2;
                        break;
                    case 6:
                        sprite = combatFaintRight6_2;
                        break;
                }
                break;
        }
    }


    /**
     * Checks and updates collision state of this entity with other entities, landmarks, tiles, etc.
     */
    protected void updateCollisionState() {

        colliding = false;                                                                                              // Note that colliding is set to 'true' in the methods below, if applicable.

        int targetCol = getCol();
        int targetRow = getRow();

        switch (directionCandidate) {
            case UP:
                targetRow--;
                break;
            case DOWN:
                targetRow++;
                break;
            case LEFT:
                targetCol--;
                break;
            case RIGHT:
                targetCol++;
                break;
        }

        if (gp.getCollisionI().calculateCollisionAll(targetCol, targetRow, this, true)) {

            colliding = true;
        }
    }


    /**
     * Updates this entity's world position by one frame if it's in a state of motion, using a walking animation.
     * If this entity reaches its target tile, it will exit the state of motion.
     *
     * @param dt time since last frame (seconds)
     */
    protected void updateWalkingAction(double dt) {

        if (moving) {

            worldCounter += speed * dt;                                                                                 // Add to the number of world units the entity has moved while in the current state of motion.

            if (!colliding) {                                                                                           // If colliding is false, the entity can move.

                updateWorldPosition(dt);
            }

            if (worldCounter <= GamePanel.NATIVE_TILE_SIZE / 2) {                                                       // Walking animation; entity will have a foot forward for half of the world units traversed.

                if (walkSpriteNumLast == 2) {

                    walkSpriteNumCurrent = 3;
                } else {

                    walkSpriteNumCurrent = 2;
                }
            } else {

                walkSpriteNumCurrent = 1;
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

                if (walkSpriteNumLast == 2) {                                                                               // Swap which foot will step forward for the next walking cycle.

                    walkSpriteNumLast = 3;
                } else {

                    walkSpriteNumLast = 2;
                }
            }
        }
    }


    /**
     * Updates this entity's world position by one frame if it's in a state of hopping.
     * If this entity reaches its target tile, it will exit the state of hopping (and motion).
     *
     * @param dt time since last frame (seconds)
     */
    protected void updateHoppingState(double dt) {

        if (hopping) {

            worldCounter += speed * dt;
            updateWorldPosition(dt);

            if (worldCounter <= GamePanel.NATIVE_TILE_SIZE / 2) {                                                       // Walking animation; entity will have a foot forward for half of the world units traversed.

                if (walkSpriteNumLast == 2) {

                    walkSpriteNumCurrent = 3;
                } else {

                    walkSpriteNumCurrent = 2;
                }
            } else {

                walkSpriteNumCurrent = 1;
            }

            if (worldCounter >= (GamePanel.NATIVE_TILE_SIZE * 2)) {

                hopping = false;
                moving = false;
                worldCounter = 0;
                worldX = worldXEnd;
                worldY = worldYEnd;
                worldXLast = worldXStart;
                worldYLast = worldYStart;
                worldXStart = worldXEnd;
                worldYStart = worldYEnd;

                if (walkSpriteNumLast == 2) {

                    walkSpriteNumLast = 3;
                } else {

                    walkSpriteNumLast = 2;
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
     * Updates this entity's combat stance animation by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    protected void updateCombatStanceAnimation(double dt) {

        animationCounter += dt;

        if (animationCounter >= animationCounterCombatStanceMax) {

            animationCounter = 0;
        }
        stageCombatStanceSprite();
    }


    /**
     * Updates this entity's combat attack animation by one frame.
     * If the animation has completed, then it is exited.
     *
     * @param dt time since last frame (seconds)
     */
    protected void updateCombatAttackAnimation(double dt) {

        animationCounter += dt;

        if (animationCounter >= animationCounterCombatAttackMax) {

            playingCombatAttackAnimation = false;
            animationCounter = 0;

            if (combating) {

                combatStanceSpriteNumCurrent = 1;                                                                       // Reset combat stance animation to first sprite in cycle, if applicable
            }
        } else {

            stageCombatAttackSprite();
        }
    }


    /**
     * Updates this entity's combat faint animation by one frame.
     * If the animation has completed, then it is exited.
     *
     * @param dt time since last frame (seconds)
     */
    protected void updateCombatFaintAnimation(double dt) {

        animationCounter += dt;
        stageCombatFaintSprite();

        if (animationCounter >= animationCounterCombatFaintMax) {

            playingCombatFaintAnimation = false;
            animationCounter = 0;
        }
    }


    /**
     * Updates an active fade effect (up or down) by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    protected void updateFadeEffect(double dt) {

        float alphaCandidate;

        switch (activeFadeEffect) {
            case FADE_UP:
                alphaCandidate = color.w + (float)(dt * fadeEffectAlphaPerSecond);
                if (alphaCandidate >= 255) {
                    setAlpha(255);
                    activeFadeEffect = FadeEffectType.NONE;
                    fadeEffectAlphaPerSecond = 0;
                } else {
                    color.w = alphaCandidate;
                }
                break;
            case FADE_DOWN:
                alphaCandidate = color.w - (float)(dt * fadeEffectAlphaPerSecond);
                if (alphaCandidate < 0) {
                    hidden = true;
                    setAlpha(255);
                    activeFadeEffect = FadeEffectType.NONE;
                    fadeEffectAlphaPerSecond = 0;
                } else {
                    color.w = alphaCandidate;
                }
                break;
        }
    }


    /**
     * Sets this entity's default action/behavior.
     * Override this method in implemented EntityBase classes if custom actions are desired.
     *
     * @param dt time since last frame (seconds)
     */
    protected void setAction(double dt) {

        if (rest > 0) {

            rest -= dt;                                                                                                 // Decrease rest time by one frame each time a new frame is rendered.
        }

        switch (defaultAction) {
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
            }
        } else {

            if (onPath) {

                stopFollowingPath();                                                                                    // Entity has either arrived at destination or path to destination could not be found, so exit this state.
            }

            if (onEntityId != NO_ENTITY_FOLLOWED) {

                onEntityId = NO_ENTITY_FOLLOWED;
            }

            if ((startCol != goalCol) && (startRow != goalRow)) {                                                       // Check if a path could not be found for a reason other than already arrived at destination.

                UtilityTool.logError("Entity"
                        + (((name != null) && (!name.equals(""))) ? (" '" + name + "'") : "")
                        + " with ID '"
                        + entityId
                        + "' was not able to find a path to the following destination: row '"
                        + goalRow
                        + "', column '"
                        + goalCol
                        + "', map ID '"
                        + gp.getMapM().getLoadedMap().getMapId()
                        + "'.");
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

        EntityBase target = gp.getEntityM().getEntityById(entityId);

        if ((moving) || (getCol() != target.getColLast()) || (getRow() != target.getRowLast())) {                       // Only update if the entity being followed has changed position.

            actionPath(dt, target.getColLast(), target.getRowLast());                                                   // Initiate a pathfinding operation.
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
        updateWalkingAction(dt);                                                                                        // The entity's position on the map will be updated each frame while `onPath` is true (i.e., it will continue walking).
    }


    /**
     * Action: Sets this entity to take a step to a random adjacent tile that's available.
     *
     * @param dt time since last frame (seconds)
     */
    protected void actionRandomSteps(double dt) {

        boolean movingFlag = moving;                                                                                    // Set a flag to see if the entity entered this method in a state of motion.

        if (rest <= 0) {

            initiateRandomStep();
        }
        updateWalkingAction(dt);

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

            updateWalkingAction(dt);                                                                                    // If entity is currently in a state of motion, it needs to finish that before doing anything else.
            return;
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
            updateCollisionState();                                                                                     // Check and update colliding state of entity.

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
                gp.getEventM().handleStockStepInteraction(getColEnd(), getRowEnd(),
                        entityId, StockStepInteractionType.GRASS_RUSTLE);
            }
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

    public Sprite getIdleUp() {
        return idleUp;
    }

    public Sprite getWalkUp1() {
        return walkUp1;
    }

    public Sprite getWalkUp2() {
        return walkUp2;
    }

    public Sprite getIdleDown() {
        return idleDown;
    }

    public Sprite getWalkDown1() {
        return walkDown1;
    }

    public Sprite getWalkDown2() {
        return walkDown2;
    }

    public Sprite getIdleLeft() {
        return idleLeft;
    }

    public Sprite getWalkLeft1() {
        return walkLeft1;
    }

    public Sprite getWalkLeft2() {
        return walkLeft2;
    }

    public Sprite getIdleRight() {
        return idleRight;
    }

    public Sprite getWalkRight1() {
        return walkRight1;
    }

    public Sprite getWalkRight2() {
        return walkRight2;
    }

    public Sprite getCombatStanceLeft1() {
        return combatStanceLeft1;
    }

    public Sprite getCombatStanceLeft2() {
        return combatStanceLeft2;
    }

    public Sprite getCombatStanceRight1() {
        return combatStanceRight1;
    }

    public Sprite getCombatStanceRight2() {
        return combatStanceRight2;
    }

    public Sprite getCombatAttackRight() {
        return combatAttackRight;
    }

    public Sprite getCombatAttackLeft() {
        return combatAttackLeft;
    }

    public Sprite getCombatFaintLeft1_1() {
        return combatFaintLeft1_1;
    }

    public Sprite getCombatFaintLeft1_2() {
        return combatFaintLeft1_2;
    }

    public Sprite getCombatFaintRight1_1() {
        return combatFaintRight1_1;
    }

    public Sprite getCombatFaintRight1_2() {
        return combatFaintRight1_2;
    }

    public Sprite getCombatFaintLeft2_1() {
        return combatFaintLeft2_1;
    }

    public Sprite getCombatFaintLeft2_2() {
        return combatFaintLeft2_2;
    }

    public Sprite getCombatFaintRight2_1() {
        return combatFaintRight2_1;
    }

    public Sprite getCombatFaintRight2_2() {
        return combatFaintRight2_2;
    }

    public Sprite getCombatFaintLeft3_1() {
        return combatFaintLeft3_1;
    }

    public Sprite getCombatFaintLeft3_2() {
        return combatFaintLeft3_2;
    }

    public Sprite getCombatFaintRight3_1() {
        return combatFaintRight3_1;
    }

    public Sprite getCombatFaintRight3_2() {
        return combatFaintRight3_2;
    }

    public Sprite getCombatFaintLeft4_1() {
        return combatFaintLeft4_1;
    }

    public Sprite getCombatFaintLeft4_2() {
        return combatFaintLeft4_2;
    }

    public Sprite getCombatFaintRight4_1() {
        return combatFaintRight4_1;
    }

    public Sprite getCombatFaintRight4_2() {
        return combatFaintRight4_2;
    }

    public Sprite getCombatFaintLeft5_1() {
        return combatFaintLeft5_1;
    }

    public Sprite getCombatFaintLeft5_2() {
        return combatFaintLeft5_2;
    }

    public Sprite getCombatFaintRight5_1() {
        return combatFaintRight5_1;
    }

    public Sprite getCombatFaintRight5_2() {
        return combatFaintRight5_2;
    }

    public Sprite getCombatFaintLeft6_1() {
        return combatFaintLeft6_1;
    }

    public Sprite getCombatFaintLeft6_2() {
        return combatFaintLeft6_2;
    }

    public Sprite getCombatFaintRight6_1() {
        return combatFaintRight6_1;
    }

    public Sprite getCombatFaintRight6_2() {
        return combatFaintRight6_2;
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

    public boolean isCombating() {
        return combating;
    }

    public boolean isConversing() {
        return conversing;
    }

    public boolean isColliding() {
        return colliding;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isPlayingCombatAttackAnimation() {
        return playingCombatAttackAnimation;
    }

    public boolean isPlayingCombatFaintAnimation() {
        return playingCombatFaintAnimation;
    }

    public boolean isHopping() {
        return hopping;
    }

    public boolean isOnPath() {
        return onPath;
    }

    public boolean isOnEntity() {
        return onEntityId != NO_ENTITY_FOLLOWED;
    }

    public int getOnEntityId() {
        return onEntityId;
    }

    public FadeEffectType getActiveFadeEffect() {
        return activeFadeEffect;
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

    public int getMaxSkill() {
        return maxSkill;
    }

    public int getSkill() {
        return skill;
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

    public void setCombating(boolean combating) {
        this.combating = combating;
        if (combating) {
            cancelAction();
            Random random = new Random();
            int i = random.nextInt(101);                                                                                // Generate random number from 0 (inclusive) to 100 (inclusive, since 101 is exclusive).
            animationCounter = ((double)i / 100.0) * animationCounterCombatStanceMax;                                   // Randomize animation counter so that not all entities are animating in sync.
        } else {
            resetDefaultActionInitialRest();
        }
    }

    public void setConversing(boolean conversing) {
        this.conversing = conversing;
        if (conversing) {
            cancelAction();
        } else {
            resetDefaultActionInitialRest();
        }
    }

    public void setColliding(boolean colliding) {
        this.colliding = colliding;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setDefaultAction(DefaultAction defaultAction) {
        this.defaultAction = defaultAction;
    }

    public void setHopping(boolean hopping) {
        this.hopping = hopping;
    }

    public void startFollowingPath(int goalCol, int goalRow) {
        if (!isOnEntity()) {
            this.onPathGoalCol = goalCol;
            this.onPathGoalRow = goalRow;
            this.onPath = true;
        }
    }

    public void stopFollowingPath() {
        onPathGoalCol = 0;
        onPathGoalRow = 0;
        onPath = false;
        setRest(0);
    }

    public void startFollowingEntity(int entityId) {
        if (this.entityId != entityId) {                                                                                // Ensure that we're not trying to make the entity follow itself.
            EntityBase target = gp.getEntityM().getEntityById(entityId);
            EntityBase followed = gp.getEntityM().getEntityById(gp.getEventM().checkEntityChainDown(target));           // If a chain of followers is following the target entity, then this entity will be placed at the back of the chain (i.e., this entity will actually follow the entity at the end of the chain).
            onEntityId = followed.getEntityId();
            followed.setColLast(this.getCol());                                                                         // The follower will always find a path to the followed's last position; setting it this way prevents the follower from instantly moving once following begins.
            followed.setRowLast(this.getRow());                                                                         // ^^^
        }
    }

    public void stopFollowingEntity() {
        onEntityId = NO_ENTITY_FOLLOWED;
        setRest(0);
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

    public void setMaxSkill(int maxSkill) {
        if (maxSkill >= 0) {
            this.maxSkill = maxSkill;
        }
    }

    public void setSkill(int skill) {
        if ((skill >= 0) && (skill <= maxSkill)) {
            this.skill = skill;
        } else if (skill < 0) {
            this.skill = 0;
        } else if (skill > maxSkill) {
            this.skill = maxSkill;
        }
    }

    public void addSkillPoints(int addition) {
        int result = skill + addition;
        if ((result >= 0) && (result <= maxSkill)) {
            skill = result;
        } else if (result > maxSkill) {
            skill = maxSkill;
        } else if (result < 0) {
            skill = 0;
        }
    }

    public void subtractSkillPoints(int subtraction) {
        int result = skill - subtraction;
        if ((result >= 0) && (result <= maxSkill)) {
            skill = result;
        } else if (result < 0) {
            skill = 0;
        } else if (result > maxSkill) {
            skill = maxSkill;
        }
    }

    public void setBaseAttack(int baseAttack) {
        if (baseAttack >= 0) {
            this.baseAttack = baseAttack;
        } else {
            this.baseAttack = 0;
        }
    }

    public void setBaseDefense(int baseDefense) {
        if (baseDefense >= 0) {
            this.baseDefense = baseDefense;
        } else {
            this.baseDefense = 0;
        }
    }

    public void setBaseMagic(int baseMagic) {
        if (baseMagic >= 0) {
            this.baseMagic = baseMagic;
        } else {
            this.baseMagic = 0;
        }
    }

    public void setBaseAgility(int baseAgility) {
        if (baseAgility >= 0) {
            this.baseAgility = baseAgility;
        } else {
            this.baseAgility = 0;
        }
    }

    public void setAttackBuff(int attackBuff) {
        this.attackBuff = attackBuff;
    }

    public void setDefenseBuff(int defenseBuff) {
        this.defenseBuff = defenseBuff;
    }

    public void setMagicBuff(int magicBuff) {
        this.magicBuff = magicBuff;
    }

    public void setAgilityBuff(int agilityBuff) {
        this.agilityBuff = agilityBuff;
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
