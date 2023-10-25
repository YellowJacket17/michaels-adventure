package utility;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class contains miscellaneous methods to support the game.
 */
public class UtilityTool {

    // FIELDS
    /**
     * Output stream (console).
     */
    private static final OutputStream out = new BufferedOutputStream(System.out);

    /**
     * Date format (log contents).
     */
    private static final SimpleDateFormat logContentsDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS");

    /**
     * Date format (file name).
     */
    private static final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");

    /**
     * Temporary log file location and name.
     */
    private static final String tempLogFilePath = "./templog.txt";

    /**
     * Boolean indicating whether the temporary log file has been successfully initialized.
     */
    private static boolean tempLogInitialized = false;


    // METHODS
    /**
     * Scales an image.
     *
     * @param original original image to be scaled
     * @param width width the image will be scaled to
     * @param height height the image will be scaled to
     * @return scaled image
     */
    public static BufferedImage scaleImage(BufferedImage original, int width, int height) {

        BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(original, 0, 0, width, height, null);

        g2.dispose();

        return scaledImage;
    }


    /**
     * Pauses code execution for a specified period of time.
     *
     * @param delay number of milliseconds to pause execution for
     */
    public static void wait(int delay) {

        long start = System.currentTimeMillis();
        while(start >= System.currentTimeMillis() - delay);                                                             // Do nothing while waiting.
    }


    /**
     * Prints an error message to the console.
     *
     * @param message error message to be printed
     */
    public static void logInfo(String message) {

        Date date = new Date();
        String formattedDate = logContentsDateFormat.format(date);
        try {
            out.write((formattedDate + "  \u001B[34m" + "INFO\u001B[0m     " + message + "\n").getBytes());
            out.flush();
        } catch (IOException e) {
            System.out.println(formattedDate + "  \u001B[34m" + "INFO\u001B[0m     " + message);
        }
        if (tempLogInitialized) {
            try (FileWriter fw = new FileWriter("./templog.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter outFile = new PrintWriter(bw)) {
                outFile.write(formattedDate + "  " + "INFO     " + message + "\n");
            } catch (IOException e) {}
        }
    }


    /**
     * Prints an error message to the console.
     *
     * @param message error message to be printed
     */
    public static void logError(String message) {

        Date date = new Date();
        String formattedDate = logContentsDateFormat.format(date);
        try {
            out.write((formattedDate + "  \u001B[31m" + "ERROR\u001B[0m    " + message + "\n").getBytes());
            out.flush();
        } catch (IOException e) {
            System.out.println(formattedDate + "  \u001B[31m" + "ERROR\u001B[0m    " + message);
        }
        if (tempLogInitialized) {
            try (FileWriter fw = new FileWriter(tempLogFilePath, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter outFile = new PrintWriter(bw)) {
                outFile.write(formattedDate + "  " + "ERROR    " + message + "\n");
            } catch (IOException e) {}
        }
    }


    /**
     * Prints a warning message to the console.
     *
     * @param message warning message to be printed
     */
    public static void logWarning(String message) {

        Date date = new Date();
        String formattedDate = logContentsDateFormat.format(date);
        try {
            out.write((formattedDate + "  \u001B[93m" + "WARNING\u001B[0m  " + message + "\n").getBytes());
            out.flush();
        } catch (IOException e) {
            System.out.println(formattedDate + "  \u001B[93m" + "WARNING\u001B[0m  " + message);
        }
        if (tempLogInitialized) {
            try (FileWriter fw = new FileWriter(tempLogFilePath, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter outFile = new PrintWriter(bw)) {
                outFile.write(formattedDate + "  " + "WARNING  " + message + "\n");
            } catch (IOException e) {}
        }
    }


    /**
     * Prints a file path to the console.
     *
     * @param filePath file path message to be printed
     */
    public static void logFilePath(String filePath) {

        Date date = new Date();
        String formattedDate = logContentsDateFormat.format(date);
        try {
            out.write((formattedDate + "  \u001B[0m" + "         >> " + filePath + "\u001B[0m\n").getBytes());
            out.flush();
        } catch (IOException e) {
            System.out.println(formattedDate + "  \u001B[0m" + "         >> " + filePath + "\u001B[0m");
        }
        if (tempLogInitialized) {
            try (FileWriter fw = new FileWriter(tempLogFilePath, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter outFile = new PrintWriter(bw)) {
                outFile.write(formattedDate + "  " + "         >> " + filePath + "\n");
            } catch (IOException e) {}
        }
    }


    /**
     * Prints an exception's stack trace to the console.
     * @param exception exception whose stack trace is to be printed
     */
    public static void logStackTrace(Exception exception) {

        exception.printStackTrace();
        try(FileWriter fw = new FileWriter(tempLogFilePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            exception.printStackTrace(out);
        } catch (IOException e) {}
    }


    /**
     * Initializes a temporary log file to write all log messages to.
     */
    public static void initializeTempLog() {

        try (PrintWriter pw = new PrintWriter(tempLogFilePath)) {
            tempLogInitialized = true;
        } catch (FileNotFoundException ex) {}
    }


    /**
     * Writes a crash log file if the temporary log file has already been initialized.
     */
    public static void writeCrashLog() {

        if (tempLogInitialized) {
            Date date = new Date();
            String formattedDate = fileNameDateFormat.format(date);
            String filePath = "./crashlogs/crashlog_" + formattedDate + ".txt";
            try (BufferedReader reader = new BufferedReader(new FileReader(tempLogFilePath));
                 PrintWriter pw = new PrintWriter(filePath)) {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    pw.write(line + "\n");
                }
            } catch (IOException ex) {}
        }
    }
}
