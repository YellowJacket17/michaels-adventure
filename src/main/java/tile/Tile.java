package tile;

import asset.Sprite;

import java.util.ArrayList;

/**
 * This class defines a tile of a map.
 */
public class Tile {

    // FIELDS
    /**
     * List of tile sprites. Multiple sprites in the list can be used for animation.
     */
    private final ArrayList<Sprite> sprites = new ArrayList<>();

    /**
     * Boolean setting whether this tile has collision (i.e., is solid) or not.
     * A tile has collision by default.
     */
    private boolean collision = false;

    /**
     * Animation group that this tile is part of.
     * Animation groups can have values of 0, 1, 2, etc.
     * A value of -1 means that this tile is not animated.
     */
    private int animationGroup = -1;


    // CONSTRUCTOR
    /**
     * Constructs a Tile instance.
     */
    public Tile() {}


    // GETTERS
    public ArrayList<Sprite> getSprites() {
        return sprites;
    }

    public boolean hasCollision() {
        return collision;
    }

    public int getAnimationGroup() {
        return animationGroup;
    }


    // SETTERS
    public void addSprite(Sprite sprite) {
        this.sprites.add(sprite);
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public void setAnimationGroup(int animationGroup) {
        this.animationGroup = animationGroup;
    }
}
