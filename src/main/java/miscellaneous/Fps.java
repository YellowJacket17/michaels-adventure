package miscellaneous;

/**
 * This enum define a target FPS (frames per second).
 */
public enum Fps {

    FPS_30(30),
    FPS_60(60),
    FPS_120(120),
    FPS_144(144);

    public final int value;

    private Fps(int value) {
        this.value = value;
    }
}
