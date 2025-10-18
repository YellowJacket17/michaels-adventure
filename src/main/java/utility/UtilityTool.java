package utility;

import org.lwjgl.BufferUtils;
import utility.exceptions.AssetLoadException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class contains miscellaneous methods to support the game.
 */
public class UtilityTool {

    // FIELDS
    /**
     * Boolean indicating whether verbose logging is active (true) or not (false).
     */
    public static final boolean VERBOSE_LOGGING = true;

    /**
     * Output stream (console).
     */
    private static final OutputStream OUT = new BufferedOutputStream(System.out);

    /**
     * Date format (log contents).
     */
    private static final SimpleDateFormat LOG_CONTENTS_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS");

    /**
     * Date format (file name).
     */
    private static final SimpleDateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");

    /**
     * Temporary log file path from root directory.
     */
    private static final String TEMP_LOG_FILE_PATH = "./templog.txt";

    /**
     * Boolean indicating whether the temporary log file has been successfully initialized.
     */
    private static boolean tempLogInitialized = false;


    // METHODS
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
     * Prints an informational message to the console and temporary log file.
     *
     * @param message informational message to be printed
     */
    public static void logInfo(String message) {

        Date date = new Date();
        String formattedDate = LOG_CONTENTS_DATE_FORMAT.format(date);
        try {
            OUT.write((formattedDate + "  \u001B[34m" + "INFO\u001B[0m     " + message + "\n").getBytes());
            OUT.flush();
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
     * Prints an error message to the console and temporary log file.
     *
     * @param message error message to be printed
     */
    public static void logError(String message) {

        Date date = new Date();
        String formattedDate = LOG_CONTENTS_DATE_FORMAT.format(date);
        try {
            OUT.write((formattedDate + "  \u001B[31m" + "ERROR\u001B[0m    " + message + "\n").getBytes());
            OUT.flush();
        } catch (IOException e) {
            System.out.println(formattedDate + "  \u001B[31m" + "ERROR\u001B[0m    " + message);
        }
        if (tempLogInitialized) {
            try (FileWriter fw = new FileWriter(TEMP_LOG_FILE_PATH, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter outFile = new PrintWriter(bw)) {
                outFile.write(formattedDate + "  " + "ERROR    " + message + "\n");
            } catch (IOException e) {}
        }
    }


    /**
     * Prints a warning message to the console and temporary log file.
     *
     * @param message warning message to be printed
     */
    public static void logWarning(String message) {

        Date date = new Date();
        String formattedDate = LOG_CONTENTS_DATE_FORMAT.format(date);
        try {
            OUT.write((formattedDate + "  \u001B[93m" + "WARNING\u001B[0m  " + message + "\n").getBytes());
            OUT.flush();
        } catch (IOException e) {
            System.out.println(formattedDate + "  \u001B[93m" + "WARNING\u001B[0m  " + message);
        }
        if (tempLogInitialized) {
            try (FileWriter fw = new FileWriter(TEMP_LOG_FILE_PATH, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter outFile = new PrintWriter(bw)) {
                outFile.write(formattedDate + "  " + "WARNING  " + message + "\n");
            } catch (IOException e) {}
        }
    }


    /**
     * Prints a file path to the console and temporary log file.
     *
     * @param filePath file path message to be printed
     */
    public static void logFilePath(String filePath) {

        Date date = new Date();
        String formattedDate = LOG_CONTENTS_DATE_FORMAT.format(date);
        try {
            OUT.write((formattedDate + "  \u001B[0m" + "         >> " + filePath + "\u001B[0m\n").getBytes());
            OUT.flush();
        } catch (IOException e) {
            System.out.println(formattedDate + "  \u001B[0m" + "         >> " + filePath + "\u001B[0m");
        }
        if (tempLogInitialized) {
            try (FileWriter fw = new FileWriter(TEMP_LOG_FILE_PATH, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter outFile = new PrintWriter(bw)) {
                outFile.write(formattedDate + "  " + "         >> " + filePath + "\n");
            } catch (IOException e) {}
        }
    }


    /**
     * Prints an exception's stack trace to the console and temporary log file.
     * @param exception exception whose stack trace is to be printed
     */
    public static void logStackTrace(Exception exception) {

        exception.printStackTrace();
        try(FileWriter fw = new FileWriter(TEMP_LOG_FILE_PATH, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            exception.printStackTrace(out);
        } catch (IOException e) {}
    }


    /**
     * Initializes a temporary log file to write all log messages to.
     */
    public static void initializeTempLog() {

        try (PrintWriter pw = new PrintWriter(TEMP_LOG_FILE_PATH)) {
            tempLogInitialized = true;
        } catch (FileNotFoundException ex) {}
    }


    /**
     * Writes a crash log file if the temporary log file has already been initialized.
     */
    public static void writeCrashLog() {

        if (tempLogInitialized) {
            Date date = new Date();
            String formattedDate = FILE_NAME_DATE_FORMAT.format(date);
            String filePath = "./crashlogs/crashlog_" + formattedDate + ".txt";
            try (BufferedReader reader = new BufferedReader(new FileReader(TEMP_LOG_FILE_PATH));
                 PrintWriter pw = new PrintWriter(filePath)) {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    pw.write(line + "\n");
                }
            } catch (IOException ex) {}
        }
    }


    /**
     * Builds message listing entity names.
     * (Example: "EntityName1, EntityName2, and EntityName3")
     *
     * @param entityNames names of entities to be listed
     * @return message
     */
    public static String buildEntityListMessage(ArrayList<String> entityNames) {

        String build = "";
        int i = 0;

        for (String entityName : entityNames) {

            if (i == (entityNames.size() - 1)) {

                if (i > 0) {

                    build += "and ";
                }
                build += entityName + "'s";
            } else {

                build += entityName;

                if (entityNames.size() > 2) {

                    build += ", ";
                } else {

                    build += " ";
                }
            }
            i++;
        }
        return build;
    }


    /**
     * Extracts a keyset from a map.
     *
     * @param map map from which to extract keys
     * @return keyset as ArrayList
     */
    public static ArrayList<Integer> extractKeySetAsArrayList(Map<Integer, Integer> map) {

        Set<Integer> set = map.keySet();
        return new ArrayList<>(set);
    }


    /**
     * Loads a resource as a ByteBuffer.
     *
     * @param filePath file path of resource from resources directory
     * @param bufferSize allocated buffer size (bytes)
     * @return resource as ByteBuffer
     */
    public static ByteBuffer ioResourceToByteBuffer(String filePath, int bufferSize) {

        ByteBuffer buffer;

        try (InputStream is = UtilityTool.class.getResourceAsStream(filePath);
             ReadableByteChannel rbc = Channels.newChannel(is)) {

            buffer = BufferUtils.createByteBuffer(bufferSize);

            while (true) {

                int bytes = rbc.read(buffer);

                if (bytes == -1) {
                    break;
                }

                if (buffer.remaining() == 0) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                }
            }

        } catch (Exception e) {

            throw new AssetLoadException("Failed to load resource from resources '" + filePath + "'");
        }
        buffer.flip();
        return buffer;
    }


    /**
     * Resizes an existing ByteBuffer.
     *
     * @param buffer ByteBuffer to resize
     * @param newCapacity new allocated buffer size (bytes)
     * @return resized ByteBuffer
     */
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {

        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}
