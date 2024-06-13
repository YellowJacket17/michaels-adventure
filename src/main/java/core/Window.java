package core;

import miscellaneous.KeyListener;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import utility.UtilityTool;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.openal.ALC10.*;
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
     * Audio context for OpenAL.
     */
    private long audioContext;

    /**
     * Audio device for OpenAL.
     */
    private long audioDevice;

    /**
     * GamePanel instance.
     */
    private GamePanel gp;

    /**
     * Refresh rate of monitor (Hz).
     */
    private int monitorRefreshRate;

    /**
     * Boolean indicating whether the main game loop is running (true) or not (false).
     */
    private boolean running = false;


    // SYSTEM SETTINGS
    /**
     * Boolean indicating whether vSync is enabled (true) or not (false).
     */
    private boolean vSyncEnabled = true;

    /**
     * Boolean tracking whether game speed is tethered to target frame rate (true) or not (false).
     */
    private boolean gameSpeedTethered = true;

    /**
     * Boolean tracking whether full screen is enabled (true) or not (false).
     */
    private boolean fullScreenEnabled = false;

    /**
     * Target frame rate (frames per second).
     */
    private int targetFrameRate;

    /**
     * Variable to temporarily store the screen coordinates (x and y) of this window right before entering full screen
     * mode.
     * This allows these screen coordinates to be restored once full screen mode is exited.
     *
     */
    private Vector2i tempWindowPos = new Vector2i();

    /**
     * Variable to temporarily store the screen size (width and height) of this window right before entering full screen
     * mode.
     * This allows this screen size to be restored once full screen mode is exited.
     */
    private Vector2i tempWindowScale = new Vector2i();


    // WINDOW PROPERTIES
    /**
     * Viewport aspect ratio.
     */
    private final Vector2f aspectRatio = new Vector2f(16f, 9f);

    /**
     * Window title.
     */
    private final String title = "Michael's Adventure";

    /**
     * Window clear color (red component).
     */
    private final float r = 0.0f;

    /**
     * Window clear color (green component).
     */
    private final float g = 0.0f;

    /**
     * Window clear color (blue component).
     */
    private final float b = 0.0f;

    /**
     * Window clear color (alpha component).
     */
    private final float a = 0.0f;


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

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create window.
        glfwWindow = glfwCreateWindow(GamePanel.NATIVE_SCREEN_WIDTH, GamePanel.NATIVE_SCREEN_HEIGHT, title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to initialize GLFW window");
        }

        // Set window icon.
        loadWindowIcon();

        // Create listeners.
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

        // Set target frame rate.
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

        // Initialize audio device.
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        // Setup audio context.
        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        // Create OpenAL capabilities.
        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
        if (!alCapabilities.OpenAL10) {
            UtilityTool.logError("Audio library not supported.");
        }

        // Create OpenGL capabilities.
        // This is critical for LWJGL's interpolation with GLFW's OpenGL context.
        // This makes OpenGL bindings available for use.
        GL.createCapabilities();
    }


    /**
     * Initializes a GamePanel instance and the game.
     */
    public void initGame() {

        gp = new GamePanel();
        gp.init();
        gp.getSystemSetting(0).setActiveOption(vSyncEnabled ? 1 : 0);
        populateFrameRateOptions(generateFrameRateOptions());
        gp.getSystemSetting(2).setActiveOption(gameSpeedTethered ? 1 : 0);
        gp.getSystemSetting(3).setActiveOption(fullScreenEnabled ? 1 : 0);
        fullScreenEnabled = fullScreenEnabled ? false : true;
    }


    /**
     * Starts the main game loop.
     */
    public void run() {

        // Set window clear color and alpha blending.
        glClearColor(r, g, b, a);                                                                                       // Set window clear color.
        glEnable(GL_BLEND);                                                                                             // Enable blending (alpha values).
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);                                                                    // Set blending function.

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

            // Poll for frame rate limit setting changes.
            pollFrameRateLimit();

            // Poll for tether game speed setting changes.
            pollTetherGamedSpeed();

            // Poll for full screen setting changes.
            pollFullScreen();

            // Set target frame pace.
            dtTarget = 1.0 / targetFrameRate;

            if (vSyncEnabled) {

                // Calculate frame pace.
                dtActual = currentTime - lastFrameTime;
                lastFrameTime = currentTime;

                // Generate frame.
                if (gameSpeedTethered) {
                    generateFrame(dtTarget, dtActual);
                } else {
                    generateFrame(dtActual, dtActual);
                }
            } else {

                dtAccumulated += currentTime - lastLoopTime;
                lastLoopTime = currentTime;

                if (dtAccumulated >= dtTarget) {

                    // Calculate frame pace.
                    dtActual = currentTime - lastFrameTime;
                    lastFrameTime = currentTime;

                    // Generate frame.
                    if (gameSpeedTethered) {
                        generateFrame(dtTarget, dtActual);
                    } else {
                        generateFrame(dtActual, dtActual);
                    }

                    // Iterate frame time.
                    dtAccumulated -= dtTarget;
                }
            }
        }

        // Free memory.
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW.
        glfwTerminate();
    }


    /**
     * If running, terminates the main game loop and subsequently shuts down the application.
     */
    public void terminate() {

        running = false;
    }


    /**
     * Prepares, polls, updates, and renders a new frame.
     *
     * @param dtUpdate frame pace to pass to update logic
     * @param dtRender frame pace to pass to render logic
     */
    private void generateFrame(double dtUpdate, double dtRender) {

        // Prepare the frame.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);                                                             // Tell OpenGL how to clear the framebuffer.

        // Poll, update, and render.
        glfwPollEvents();                                                                                               // Poll user input (keyboard, gamepad, etc.).
        gp.update(dtUpdate);                                                                                            // Update all game logic by one frame.
        gp.render(dtRender);                                                                                            // Render the updated frame.

        // Empty buffers.
        glfwSwapBuffers(glfwWindow);
    }


    /**
     * Resizes the window and viewport.
     *
     * @param width new width (pixels)
     * @param height new height (pixels)
     */
    private void resizeWindow(int width, int height) {

        int viewportWidth;
        int viewportHeight;

        if (((float)width / height) > (aspectRatio.x / aspectRatio.y)) {

            viewportWidth = (int)((aspectRatio.x / aspectRatio.y) * height);
            viewportHeight = height;
        } else {

            viewportWidth = width;
            viewportHeight = (int)((aspectRatio.y/ aspectRatio.x) * width);
        }
        glfwSetWindowSize(glfwWindow, width, height);
        glViewport(((width - viewportWidth) / 2), ((height - viewportHeight) / 2), viewportWidth, viewportHeight);
        monitorRefreshRate = getRefreshRate();

        if (running) {
            populateFrameRateOptions(generateFrameRateOptions());
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

        if (running) {
            populateFrameRateOptions(generateFrameRateOptions());
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
                if ((windowScale.x == overlapWidth) && (windowScale.y == overlapHeight)) {
                    return monitor;
                }
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


    /**
     * Polls for changes in VSync in system settings.
     * If a change has occurred, it is immediately applied.
     *
     * @return whether a change has occurred (true) or not (false)
     */
    private boolean pollVSync() {

        if ((gp.getSystemSetting(0).getActiveOption() == 0) && vSyncEnabled) {

            vSyncEnabled = false;
            glfwSwapInterval(0);
            populateFrameRateOptions(generateFrameRateOptions());
            return true;
        } else if ((gp.getSystemSetting(0).getActiveOption() == 1) && !vSyncEnabled) {

            vSyncEnabled = true;
            glfwSwapInterval(1);
            populateFrameRateOptions(generateFrameRateOptions());
            return true;
        }
        return false;
    }


    /**
     * Polls for changes in frame rate limit in system settings.
     * If a change has occurred, it is immediately applied.
     *
     * @return whether a change has occurred (true) or not (false)
     */
    private boolean pollFrameRateLimit() {

        int activeOption = Integer.parseInt(gp.getSystemSetting(1).getOption(gp.getSystemSetting(1).getActiveOption()));

        if (activeOption != targetFrameRate) {

            targetFrameRate = activeOption;
            return true;
        }
        return false;
    }


    /**
     * Polls for changes in tether game speed in system settings.
     * If a change has occurred, it is immediately applied.
     *
     * @return whether a change has occurred (true) or not (false)
     */
    private boolean pollTetherGamedSpeed() {

        if ((gp.getSystemSetting(2).getActiveOption() == 0) && gameSpeedTethered) {

            gameSpeedTethered = false;
            return true;
        } else if ((gp.getSystemSetting(2).getActiveOption() == 1) && !gameSpeedTethered) {

            gameSpeedTethered = true;
            return true;
        }
        return false;
    }


    /**
     * Polls for changes in full screen in system settings.
     * If a change has occurred, it is immediately applied.
     *
     * @return whether a change has occurred (true) or not (false)
     */
    private boolean pollFullScreen() {

        if ((gp.getSystemSetting(3).getActiveOption() == 0) && fullScreenEnabled) {

            long monitor = getClosestMonitor();
            GLFWVidMode videoModeMonitor = glfwGetVideoMode(monitor);

            glfwSetWindowMonitor(glfwWindow, NULL, tempWindowPos.x, tempWindowPos.y, tempWindowScale.x, tempWindowScale.y, videoModeMonitor.refreshRate());
            fullScreenEnabled = false;

            tempWindowPos.x = 0;
            tempWindowPos.y = 0;
            tempWindowScale.x = 0;
            tempWindowScale.y = 0;
            return true;
        } else if ((gp.getSystemSetting(3).getActiveOption() == 1) && !fullScreenEnabled) {

            long monitor = getClosestMonitor();
            GLFWVidMode videoModeMonitor = glfwGetVideoMode(monitor);

            IntBuffer bufferWindowPosX = BufferUtils.createIntBuffer(1);
            IntBuffer bufferWindowPosY = BufferUtils.createIntBuffer(1);
            glfwGetWindowPos(glfwWindow, bufferWindowPosX, bufferWindowPosY);
            Vector2i windowPos = new Vector2i(bufferWindowPosX.get(0), bufferWindowPosY.get(0));
            tempWindowPos.x = windowPos.x;
            tempWindowPos.y = windowPos.y;

            IntBuffer bufferWindowWidth = BufferUtils.createIntBuffer(1);
            IntBuffer bufferWindowHeight = BufferUtils.createIntBuffer(1);
            glfwGetWindowSize(glfwWindow, bufferWindowWidth, bufferWindowHeight);
            Vector2i windowScale = new Vector2i(bufferWindowWidth.get(0), bufferWindowHeight.get(0));
            tempWindowScale.x = windowScale.x;
            tempWindowScale.y = windowScale.y;

            glfwSetWindowMonitor(glfwWindow, monitor, 0, 0, videoModeMonitor.width(), videoModeMonitor.height(), videoModeMonitor.refreshRate());
            fullScreenEnabled = true;
            return true;
        }
        return false;
    }


    /**
     * Generates a list of allowable frame rates.
     * The list is generated based on VSync and monitor refresh rate.
     *
     * @return list of frame rates
     */
    private ArrayList<Integer> generateFrameRateOptions() {

        ArrayList<Integer> frameRateOptions = new ArrayList<>();
        frameRateOptions.add(monitorRefreshRate);

        if (!vSyncEnabled) {

            frameRateOptions.add(30);
            frameRateOptions.add(60);
            frameRateOptions.add(90);
            frameRateOptions.add(120);
            Collections.sort(frameRateOptions);
        }
        return frameRateOptions;
    }


    /**
     * Populates the list of allowable frame rate limits in system settings.
     */
    private void populateFrameRateOptions(ArrayList<Integer> frameRateOptions) {

        boolean activeOptionApplied = false;
        String activeOption = gp.getSystemSetting(1).getOption(gp.getSystemSetting(1).getActiveOption());
        gp.getSystemSetting(1).removeAllOptions();
        int index = 0;

        for (int option : frameRateOptions) {

            gp.getSystemSetting(1).addOption(String.valueOf(option));

            if ((activeOption != null) && (option == Integer.parseInt(activeOption))) {

                gp.getSystemSetting(1).setActiveOption(index);
                activeOptionApplied = true;
            }
            index++;
        }

        if (!activeOptionApplied) {

            gp.getSystemSetting(1).setActiveOption(0);
        }
    }
}
