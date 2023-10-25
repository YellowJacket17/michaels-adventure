package tile;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * This class defines a tile of a map.
 */
public class Tile {

    // FIELDS
    /**
     * List of tile sprites. Multiple sprites in the list can be used for animation.
     */
    private final ArrayList<BufferedImage> images = new ArrayList<>();

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
    public ArrayList<BufferedImage> getImages() {
        return images;
    }

    public boolean hasCollision() {
        return collision;
    }

    public int getAnimationGroup() {
        return animationGroup;
    }


    // SETTERS
    public void addImage(BufferedImage image) {
        this.images.add(image);
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public void setAnimationGroup(int animationGroup) {
        this.animationGroup = animationGroup;
    }
}
