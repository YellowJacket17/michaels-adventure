package core;

import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import utility.UtilityTool;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Core class for the game that creates the window, houses the main game loop, and initializes the game itself.
 */
public class Window {

    // BASIC FIELDS
    /**
     * Memory address of GLFW window in memory space.
     */
    private long glfwWindow;

    /**
     * GamePanel instance.
     */
    private GamePanel gp;

    /**
     * Boolean indicating whether vSync is enabled (true) or not (false).
     */
    private boolean vSyncEnabled = false;

    /**
     * Refresh rate of monitor (Hz).
     */
    private int monitorRefreshRate;

    /**
     * Target frame rate (FPS).
     */
    private int targetFrameRate = 60;

    /**
     * Boolean indicating whether the main game loop is running (true) or not (false).
     */
    private boolean running = false;


    // WINDOW PROPERTIES
    /**
     * Default window width (pixels).
     */
    private int defaultWidth = 1280;

    /**
     * Default window height (pixels).
     */
    private int defaultHeight = 720;

    /**
     * Window title.
     */
    private final String title = "Michael's Adventure";

    /**
     * Window background color (red component).
     */
    private final float r = 0.0f;

    /**
     * Window background color (green component).
     */
    private final float g = 0.0f;

    /**
     * Window background color (blue component).
     */
    private final float b = 0.0f;

    /**
     * Window background color (alpha component).
     */
    private final float a = 1.0f;


    // CONSTRUCTOR
    /**
     * Constructs a Window instance.
     */
    public Window() {}


    // METHODS
    /**
     * Initializes the GLFW window.
     */
    public void initWindow() {

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW.
        glfwDefaultWindowHints();                                                                                       // Enable default window hints (resizeable, default close operation, etc.).
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);                                                                       // Hide window during setup process.
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);                                                                      // Enable window resizing.
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);                                                                     // Initialize window in non-maximized position.
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);

        // Create window.
        glfwWindow = glfwCreateWindow(defaultWidth, defaultHeight, title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to initialize GLFW window");
        }

        // Set window icon.
        loadWindowIcon();

        // Listeners.
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                resizeWindow(width, height);
            }
        });
        glfwSetWindowPosCallback(glfwWindow, new GLFWWindowPosCallback() {
            @Override
            public void invoke(long window, int x, int y) {
                repositionWindow(x, y);
            }
        });

        // Obtain monitor refresh rate.
        monitorRefreshRate = getRefreshRate();
        targetFrameRate = monitorRefreshRate;

        // Make OpenGL context current.
        glfwMakeContextCurrent(glfwWindow);

        // Set VSync.
        if (vSyncEnabled) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }

        // Make window visible.
        glfwShowWindow(glfwWindow);

        // Create capabilities.
        // This is critical for LWJGL's interpolation with GLFW's OpenGL context.
        // This makes OpenGL bindings available for use.
        GL.createCapabilities();

        // Enable blending (alpha values).
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    }


    /**
     * Initializes a GamePanel instance and the game.
     */
    public void initGame() {

        gp = new GamePanel();
        gp.init();
        gp.getSystemSetting(0).setActiveOption(vSyncEnabled ? 1 : 0);
    }


    /**
     * Starts the main game loop.
     */
    public void run() {

        // Initialize variables for tracking time (applicable to both enabled and disabled VSync).
        // Note that `glfwGetTime()` returns time (seconds) elapsed since GLFW was initialized.
        double currentTime = 0;                                                                                         // Time at the start of the current loop.
        double lastFrameTime = glfwGetTime();                                                                           // Time at the start of the last frame.
        double dtTarget = 0;                                                                                            // Target time between each rendered frame (frame timing).
        double dtActual = 0;                                                                                            // Actual time between each rendered frame (frame timing).

        // Initialize variables for tracking time (applicable to only disabled VSync).
        double lastLoopTime = glfwGetTime();                                                                            // Time at the start of the last loop.
        double dtAccumulated = 0;                                                                                       // Accumulated loop time.

        // Indicate that the main game loop is starting.
        running = true;

        // Main game loop.
        while (!glfwWindowShouldClose(glfwWindow) && running) {

            // Set current time.
            currentTime = glfwGetTime();

            // Poll for VSync changes.
            if (pollVSync()) {
                lastLoopTime = glfwGetTime();
            }

            // Set target frame pace.
            dtTarget = 1.0 / targetFrameRate;

            if (vSyncEnabled) {

                // Calculate frame pace.
                dtActual = currentTime - lastFrameTime;
                lastFrameTime = currentTime;

                // Generate frame.
                generateFrame(dtTarget, dtActual);

            } else {

                dtAccumulated += currentTime - lastLoopTime;
                lastLoopTime = currentTime;

                if (dtAccumulated >= dtTarget) {

                    // Calculate frame pace.
                    dtActual = currentTime - lastFrameTime;
                    lastFrameTime = currentTime;

                    // Generate frame.
                    generateFrame(dtTarget, dtActual);

                    // Iterate frame time.
                    dtAccumulated -= dtTarget;
                }
            }
        }

        // Free memory.
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW.
        glfwTerminate();
    }


    /**
     * Terminates the main game loop and subsequently shuts down the application.
     */
    public void terminate() {

        running = false;
    }


    /**
     * Prepares, polls, updates, and renders a new frame.
     *
     * @param dtTarget target frame pacing
     * @param dtActual actual frame pacing
     */
    private void generateFrame(double dtTarget, double dtActual) {

        // Prepare the frame.
        glClearColor(r, g, b, a);                                                                                       // Set window clear color.
        glClear(GL_COLOR_BUFFER_BIT);                                                                                   // Tell OpenGL how to clear the buffer.

        // Poll, update, and render.
        glfwPollEvents();                                                                                               // Poll user input (keyboard, gamepad, etc.).
        gp.update(dtTarget);                                                                                            // Update all game logic by one frame.
        gp.render(dtActual);                                                                                            // Render the updated frame.

        // Empty buffers.
        glfwSwapBuffers(glfwWindow);
    }


    /**
     * Polls for changes in VSync settings.
     * If a change has occurred, it is immediately applied.
     *
     * @return whether a change has occurred (true) or not (false)
     */
    private boolean pollVSync() {

        if ((gp.getSystemSetting(0).getActiveOption() == 0) && vSyncEnabled) {

            vSyncEnabled = false;
            targetFrameRate = monitorRefreshRate;
            glfwSwapInterval(0);
            return true;
        } else if ((gp.getSystemSetting(0).getActiveOption() == 1) && !vSyncEnabled) {

            vSyncEnabled = true;
            targetFrameRate = monitorRefreshRate;
            glfwSwapInterval(1);
            return true;
        }
        return false;
    }


    /**
     * Resizes the window and viewport.
     *
     * @param width new width (pixels)
     * @param height new height (pixels)
     */
    private void resizeWindow(int width, int height) {

        glfwSetWindowSize(glfwWindow, width, height);
        glViewport(0, 0, width, height);
        monitorRefreshRate = getRefreshRate();

        if (((monitorRefreshRate != targetFrameRate) && vSyncEnabled)
                || (monitorRefreshRate < targetFrameRate)) {

            targetFrameRate = monitorRefreshRate;
        }
    }


    /**
     * Repositions the window.
     *
     * @param x new x (monitor screen coordinates)
     * @param y new y (monitor screen coordinates)
     */
    private void repositionWindow(int x, int y) {

        glfwSetWindowPos(glfwWindow, x, y);
        monitorRefreshRate = getRefreshRate();

        if (((monitorRefreshRate != targetFrameRate) && vSyncEnabled)
            || (monitorRefreshRate < targetFrameRate)) {

            targetFrameRate = monitorRefreshRate;
        }
    }


    /**
     * Loads the window icon.
     */
    private void loadWindowIcon() {

        IntBuffer bufferWidth = BufferUtils.createIntBuffer(1);
        IntBuffer bufferHeight = BufferUtils.createIntBuffer(1);
        IntBuffer bufferChannels = BufferUtils.createIntBuffer(1);

        ByteBuffer icon = UtilityTool.ioResourceToByteBuffer("/miscellaneous/test_icon.png", 4096);

        try (GLFWImage.Buffer icons = GLFWImage.malloc(1)) {

            ByteBuffer pixels = stbi_load_from_memory(icon, bufferWidth, bufferHeight, bufferChannels, 4);

            icons
                    .position(0)
                    .width(bufferWidth.get(0))
                    .height(bufferHeight.get(0))
                    .pixels(pixels);

            glfwSetWindowIcon(glfwWindow, icons);

            stbi_image_free(pixels);
        }
    }


    /**
     * Retrieves the refresh rate of the monitor that the majority of this window occupies.
     *
     * @return refresh rate (Hz)
     */
    private int getRefreshRate() {

        long monitor = getClosestMonitor();
        GLFWVidMode videoModeMonitor = glfwGetVideoMode(monitor);
        return videoModeMonitor.refreshRate();
    }


    /**
     * Retrieves the monitor that the majority of this window occupies.
     *
     * @return monitor handle
     */
    private long getClosestMonitor() {

        // Window position (top-left corner).
        IntBuffer bufferWindowPosTopLeftX = BufferUtils.createIntBuffer(1);
        IntBuffer bufferWindowPosTopLeftY = BufferUtils.createIntBuffer(1);
        glfwGetWindowPos(glfwWindow, bufferWindowPosTopLeftX, bufferWindowPosTopLeftY);
        Vector2i windowPosTopLeft = new Vector2i(bufferWindowPosTopLeftX.get(0), bufferWindowPosTopLeftY.get(0));

        // Window dimensions.
        // TODO : Perhaps just use the dimensions already recorded as global variables.
        IntBuffer bufferWindowWidth = BufferUtils.createIntBuffer(1);
        IntBuffer bufferWindowHeight = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(glfwWindow, bufferWindowWidth, bufferWindowHeight);
        Vector2i windowScale = new Vector2i(bufferWindowWidth.get(0), bufferWindowHeight.get(0));

        // Window position (bottom-right corner).
        Vector2i windowPosBottomRight = new Vector2i(windowPosTopLeft.x + windowScale.x, windowPosTopLeft.y + windowScale.y);

        // Retrieve handles of all connected monitors.
        PointerBuffer monitors = glfwGetMonitors();
        HashMap<Long, Integer> monitorOverlappingArea = new HashMap<>();

        // Calculate monitor-window overlapping area for each monitor.
        for (int i = 0; i < monitors.capacity(); i++) {

            long monitor = monitors.get(i);

            // Monitor position (top-left corner).
            IntBuffer bufferMonitorPosTopLeftX = BufferUtils.createIntBuffer(1);
            IntBuffer bufferMonitorPosTopLeftY = BufferUtils.createIntBuffer(1);
            glfwGetMonitorPos(monitor, bufferMonitorPosTopLeftX, bufferMonitorPosTopLeftY);
            Vector2i monitorPosTopLeft = new Vector2i(bufferMonitorPosTopLeftX.get(0), bufferMonitorPosTopLeftY.get(0));

            // Monitor video dimensions.
            GLFWVidMode videoModeMonitor = glfwGetVideoMode(monitor);
            Vector2i monitorScale = new Vector2i(videoModeMonitor.width(), videoModeMonitor.height());

            // Monitor position (bottom-right corner).
            Vector2i monitorPosBottomRight = new Vector2i(monitorPosTopLeft.x + monitorScale.x, monitorPosTopLeft.y + monitorScale.y);

            // Calculate monitor-window overlapping area.
            int overlapWidth = Math.min(monitorPosBottomRight.x, windowPosBottomRight.x) - Math.max(monitorPosTopLeft.x, windowPosTopLeft.x);
            int overlapHeight = Math.min(monitorPosBottomRight.y, windowPosBottomRight.y) - Math.max(monitorPosTopLeft.y, windowPosTopLeft.y);
            if ((overlapWidth < 0) || (overlapHeight < 0)) {
                monitorOverlappingArea.put(monitor, 0);
            } else {
                // TODO : Perhaps do an immediate return here if the overlapping area equals the window area.
                monitorOverlappingArea.put(monitor, overlapWidth * overlapHeight);
            }
        }

        // Return monitor with largest monitor-window overlapping area.
        long closestMonitor = 0;
        long largestOverlappingArea = -1;
        for (long monitor : monitorOverlappingArea.keySet()) {
            if (monitorOverlappingArea.get(monitor) > largestOverlappingArea) {
                closestMonitor = monitor;
                largestOverlappingArea = monitorOverlappingArea.get(monitor);
            }
        }
        return closestMonitor;
    }
}
