package core;

import utility.UtilityTool;

/**
 Entry point to the application.
 */
public class App {

    public static void main(String[] args) {

        UtilityTool.initializeTempLog();
        UtilityTool.logInfo("Application started.");
        Window window = new Window();

        try {

            window.initWindow();
            window.initGame();
            UtilityTool.logInfo("Starting game loop.");
            window.run();

        } catch (Exception e) {

            UtilityTool.logError("An unhandled exception has occurred.");
            UtilityTool.logStackTrace(e);
            UtilityTool.writeCrashLog();
            window.terminate();
        }
    }
}
