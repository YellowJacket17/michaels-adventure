package core;

import utility.UtilityTool;

import java.awt.*;
import javax.swing.*;

/**
 Entry point to the application.
 */
public class App {

    public static void main(String[] args) {

        UtilityTool.initializeTempLog();

        try {

            UtilityTool.logInfo("Application started.");

            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.setTitle("Michael's Adventure");

            java.net.URL url = ClassLoader.getSystemResource("miscellaneous/test_icon.png");
            Toolkit kit = Toolkit.getDefaultToolkit();
            Image img = kit.createImage(url);
            window.setIconImage(img);

            GamePanel gamePanel = new GamePanel();
            window.add(gamePanel);

            window.pack();                                                                                              // Causes this window to be sized to fit preferred size and layouts of its subcomponents.

            window.setLocationRelativeTo(null);                                                                         // Window will display at the center of the screen.
            window.setVisible(true);

            gamePanel.setupGame();
            UtilityTool.logInfo("Starting game thread.");
            gamePanel.startGameThread();

        } catch (Exception e) {                                                                                         // Will not catch exceptions inside the `run()` method of GamePanel (game thread).

            UtilityTool.logError("An unhandled exception has occurred.");
            UtilityTool.logStackTrace(e);
            UtilityTool.writeCrashLog();
        }
    }
}
