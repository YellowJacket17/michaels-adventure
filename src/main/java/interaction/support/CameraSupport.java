package interaction.support;

import core.GamePanel;

/**
 * This class contains methods to facilitate camera movement.
 */
public class CameraSupport {

    // FIELDS
    private final GamePanel gp;

    /**
     * Boolean indicating whether the camera is currently scrolling or not.
     */
    private boolean cameraScrolling = false;

    /**
     * Variable to store the starting camera offset from the center during a scroll effect.
     */
    private int startingOffsetX, startingOffsetY;

    /**
     * Variable to store the target camera offset from the center during a scroll effect.
     * In other words, this is the offset that'll achieve the desired camera world position.
     */
    private int targetOffsetX, targetOffsetY;

    /**
     * Variable to store the difference between the target and starting camera offsets.
     */
    private int differenceOffsetX, differenceOffsetY;

    /**
     * Boolean to indicate which of the two target camera offsets (x or y) is greater.
     * If true, then the target offset in the x direction is greater.
     */
    private boolean differenceOffsetXGreater = false;

    /**
     * Variable to track the progress of a scrolling effect.
     */
    private double completedOffsetX, completedOffsetY;

    /**
     * Variable to set the total number of frames that a scroll effect will take place for.
     * In other words, how many frames the scroll effect will last for.
     */
    private int totalFrames;

    /**
     * Variable to count the number of frames that have passed so far during a scroll effect.
     */
    private int frameCount;

    /**
     * Variable to store the calculated scroll speed in a direction.
     */
    private double speedX, speedY;

    /**
     * Variable to track the last whole number recorded for the smaller camera offset differences (x or y) from the
     * starting to the target.
     */
    private int lastWholeNumber;


    // CONSTRUCTOR
    public CameraSupport(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Updates the state of any active camera effects by one frame.
     */
    public void update() {

        updateCameraScroll();
    }


    /**
     * Snaps the camera back to center on the player.
     */
    public void resetCameraSnap() {

        if (!cameraScrolling) {

            gp.getPlayer().setCameraOffsetX(0);
            gp.getPlayer().setCameraOffsetY(0);
        }
    }


    /**
     * Scrolls the camera back to center on the player.
     *
     * @param speed speed at which to scroll the camera; minimum allowed is 1, maximum is 20
     * @throws IllegalArgumentException if an illegal speed is passed as argument
     */
    public void resetCameraScroll(int speed) {

        if ((!cameraScrolling) && (speed > 0) && (speed <= 20)) {

            setCameraScroll((gp.getPlayer().getWorldX() + (gp.getNativeTileSize() / 2)), gp.getPlayer().getWorldY(), speed);
        } else if ((speed < 1) || (speed > 20)) {

            throw new IllegalArgumentException("Attempted to set a camera scroll speed outside of bounds 1 - 20 (both inclusive)");
        }
    }


    /**
     * Snaps the camera to center on a specified world position.
     *
     * @param worldX world position to snap the camera to (x)
     * @param worldY world position to snap the camera to (y)
     */
    public void setCameraSnap(int worldX, int worldY) {

        // NOTE!
        // The plus half a tile offset for X adjusts for the fact that `centerCameraX` in the Player class has minus
        // this same offset.
        // Cancelling it out here places the desired world X position exactly at the center of the screen.

        if (!cameraScrolling) {

            gp.getPlayer().setCameraOffsetX(gp.getPlayer().getWorldX() - worldX + (gp.getNativeTileSize() / 2));
            gp.getPlayer().setCameraOffsetY(gp.getPlayer().getWorldY() - worldY);
        }
    }


    /**
     * Scrolls the camera to center on a specified world position.
     *
     * @param worldX world position to scroll the camera to (x)
     * @param worldY world position to scroll the camera to (y)
     * @param speed speed at which to scroll the camera; minimum allowed is 1, maximum is 20
     * @throws IllegalArgumentException if an illegal speed is passed as argument
     */
    public void setCameraScroll(int worldX, int worldY, int speed) {

        // NOTE!
        // Offset that moves camera up or left is positive.
        // Offset that moves camera down or right is negative.
        // This is flipped from how the X and Y directions are defined in this application.

        // This method prepares the camera scroll effect.
        // The actual camera scroll effect will be updated in the method `updateCameraScroll()`.

        if ((!cameraScrolling) && (speed > 0) && (speed <= 20)) {

            // Begin the camera scrolling process.
            cameraScrolling = true;

            // Store the current camera offset from the center.
            startingOffsetX = gp.getPlayer().getCameraOffsetX();
            startingOffsetY = gp.getPlayer().getCameraOffsetY();

            // Store the target camera offset from the center.
            // I.e., the offset that'll achieve the desired `worldX` and `worldY`.
            targetOffsetX = gp.getPlayer().getWorldX() - worldX + (gp.getNativeTileSize() / 2);
            targetOffsetY = gp.getPlayer().getWorldY() - worldY;

            // Calculate the difference between the target and current camera offsets.
            // Note that this is NOT the absolute value of the difference.
            differenceOffsetX = targetOffsetX - startingOffsetX;
            differenceOffsetY = targetOffsetY - startingOffsetY;

            if (Math.abs(differenceOffsetX) > Math.abs(differenceOffsetY)) {

                // Offset difference in the X direction is greater than that in the Y direction.
                differenceOffsetXGreater = true;

                // Since the offset difference in the X direction is greater, `speedX` simply equals the passed
                // value for `speed`.
                // Note that `speedX` will always be a whole number in this case.
                if (differenceOffsetX > 0) {

                    // Positive value since we want to move the offset left.
                    speedX = speed;
                } else {

                    // Negative value since we want to move the offset right.
                    speedX = -speed;
                }

                if ((differenceOffsetX % speedX) == 0) {

                    // The offset difference in the X direction is perfectly divisible by `speedX`.
                    totalFrames = Math.abs(differenceOffsetX / (int) speedX);
                } else {

                    // The offset difference in the X direction is NOT perfectly divisible by `speedX`.
                    // This means on the last frame, we'll have a remainder not equal to `speedX`.
                    // To adjust for this, we'll add one extra frame.
                    // On this last frame, we'll make up whatever the remainder is.
                    totalFrames = Math.abs((differenceOffsetX / (int) speedX) + 1);
                }

                // Calculate `speedY`.
                // `speedY` will be added to the Y offset for each frame of `totalFrames` that we go through.
                // `speedY` will be a value that, when multiplied by `totalFrames` (which was determined by `speedX`
                // and the difference in the X direction) will equal the difference in the Y direction.
                // Note that this will likely be a decimal
                speedY = (double) differenceOffsetY / (double) totalFrames;
            } else {

                // Offset difference in the Y direction is greater than (or equal to) that in the X direction.
                differenceOffsetXGreater = false;

                // Since the offset difference in the Y direction is greater, `speedY` simply equals the passed
                // value for `speed`.
                // Note that `speedY` will always be a whole number in this case.
                if (differenceOffsetY > 0) {

                    // Positive value since we want to move the offset up.
                    speedY = speed;
                } else {

                    // Positive value since we want to move the offset down.
                    speedY = -speed;
                }

                if ((differenceOffsetY % speedY) == 0) {

                    // The offset difference in the Y direction is perfectly divisible by `speedY`.
                    totalFrames = Math.abs(differenceOffsetY / (int) speedY);
                } else {

                    // The offset difference in the Y direction is NOT perfectly divisible by `speedY`.
                    // This means on the last frame, we'll have a remainder not equal to `speedY`.
                    // To adjust for this, we'll add one extra frame.
                    // On this last frame, we'll make up whatever the remainder is.
                    totalFrames = Math.abs((differenceOffsetY / (int) speedY) + 1);
                }

                // Calculate `speedX`.
                // `speedX` will be added to the Y offset for each frame of `totalFrames` that we go through.
                // `speedX` will be a value that, when multiplied by `totalFrames` (which was determined by `speedY`
                // and the difference in the Y direction) will equal the difference in the X direction.
                // Note that this will likely be a decimal
                speedX = (double) differenceOffsetX / (double) totalFrames;
            }

            // Declare variables to track the progress as we scroll.
            completedOffsetX = startingOffsetX;
            completedOffsetY = startingOffsetY;

            // Initialize `lastWholeNumber` to the beginning (i.e., current) offset of the direction with the
            // smaller difference.
            // Remember that `lastWholeNumber` tracks the last whole number recorded for the smaller offset difference.
            // The speed for the smaller offset may be a decimal.
            // `lastWholeNumber` will be used to actually track when we should set a new offset in the smaller direction
            // as we scroll.
            if (differenceOffsetXGreater) {

                lastWholeNumber = startingOffsetY;
            } else {

                lastWholeNumber = startingOffsetX;
            }
        } else if ((speed < 1) || (speed > 20)) {

            throw new IllegalArgumentException("Attempted to set a camera scroll speed outside of bounds 1 - 20 (both inclusive)");
        }
    }


    /**
     * Updates the camera scroll effect by one frame if one is in effect.
     */
    private void updateCameraScroll() {

        if (cameraScrolling) {

            if (frameCount == (totalFrames - 1)) {

                // On the last frame, force to the target offset for X and Y to finish this scroll.
                gp.getPlayer().setCameraOffsetX(targetOffsetX);
                gp.getPlayer().setCameraOffsetY(targetOffsetY);

                // Reset variables use for the camera scrolling effect.
                reset();
            } else {

                if (differenceOffsetXGreater) {

                    // Scroll the X offset an amount according to `speedX`.
                    completedOffsetX += speedX;

                    // Set the new offset in the X direction to make it take effect on-screen.
                    // Note that since `speedX` is guaranteed to be a whole number, we can directly set it.
                    gp.getPlayer().setCameraOffsetX((int) completedOffsetX);

                    // Scroll the Y offset an amount according to `speedY`.
                    completedOffsetY += speedY;

                    // Check to see if `completedOffsetY` has iterated at least one whole number.
                    // If so, we'll set the last whole number it passed as the new offset in the Y direction.
                    if (Math.abs(completedOffsetY - lastWholeNumber) >= 1) {

                        // Declare a variable that will be used to track whether `lastWholeNumber` is positive or negative.
                        // Essentially, at some point in the logic below we take the absolute value of `lastWholeNumber`,
                        // but will eventually return it to its original sign.
                        // To return it to its original sign, we'll multiply by either 1 or -1 according to what
                        // `negativeControl` marked the sign as.
                        int negativeControl;

                        // Always round down in the direction we're coming from (ignore negative sign for
                        // negative offsets).
                        negativeControl = 1;

                        if (completedOffsetY < 0) {

                            negativeControl = -1;
                        }

                        if (Math.abs(completedOffsetY) > Math.abs(lastWholeNumber)) {

                            lastWholeNumber = (int) (negativeControl * Math.floor(Math.abs(completedOffsetY)));
                        } else {

                            lastWholeNumber = (int) (negativeControl * Math.ceil(Math.abs(completedOffsetY)));
                        }

                        gp.getPlayer().setCameraOffsetY(lastWholeNumber);
                    }

                } else {

                    // Scroll the Y offset an amount according to `speedY`.
                    completedOffsetY += speedY;

                    // Set the new offset in the Y direction to make it take effect on-screen.
                    // Note that since `speedY` is guaranteed to be a whole number, we can directly set it.
                    gp.getPlayer().setCameraOffsetY((int) completedOffsetY);

                    // Scroll the X offset an amount according to `speedX`.
                    completedOffsetX += speedX;

                    // Check to see if `completedOffsetX` has iterated at least one whole number.
                    // If so, we'll set the last whole number it passed as the new offset in the X direction.
                    if (Math.abs(completedOffsetX - lastWholeNumber) >= 1) {

                        // Declare a variable that will be used to track whether `lastWholeNumber` is positive or negative.
                        // Essentially, at some point in the logic below we take the absolute value of `lastWholeNumber`,
                        // but will eventually return it to its original sign.
                        // To return it to its original sign, we'll multiply by either 1 or -1 according to what
                        // `negativeControl` marked the sign as.
                        int negativeControl;

                        // Always round down in the direction we're coming from (ignore negative sign for
                        // negative offsets).
                        negativeControl = 1;

                        if (completedOffsetX < 0) {

                            negativeControl = -1;
                        }

                        if (Math.abs(completedOffsetX) > Math.abs(lastWholeNumber)) {

                            lastWholeNumber = (int) (negativeControl * Math.floor(Math.abs(completedOffsetX)));
                        } else {

                            lastWholeNumber = (int) (negativeControl * Math.ceil(Math.abs(completedOffsetX)));
                        }

                        gp.getPlayer().setCameraOffsetX(lastWholeNumber);
                    }

                    // Iterate the frame count.
                    frameCount++;
                }
            }
        }
    }


    /**
     * Resets WarpSupport back to its default state.
     * Intended to be called to clean up after a camera scroll effect has finished.
     */
    private void reset() {

        cameraScrolling = false;
        startingOffsetX = 0;
        startingOffsetY = 0;
        targetOffsetX = 0;
        targetOffsetY = 0;
        differenceOffsetX = 0;
        differenceOffsetY = 0;
        differenceOffsetXGreater = false;
        completedOffsetX = 0;
        completedOffsetY = 0;
        totalFrames = 0;
        frameCount = 0;
        speedX = 0;
        speedY = 0;
        lastWholeNumber = 0;
    }
}
