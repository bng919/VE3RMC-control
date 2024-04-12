/*
 * Copyright (C) 2024  Benjamin Graham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package utils;

import utils.enums.Verbosity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <P>Class to manage storage of configuration files, status messages, and data.
 * All information printed to the console from any class in this program should use
 * {@link Log#debug(String)}, {@link Log#info(String)}, {@link Log#warn(String)}, or {@link Log#error(String)}.</P>
 *
 * <P>This class must be instantiated at the beginning of main program.</P>
 *
 * <P>This class is was created to meet the specific logging requirements for this project, however a external logging
 * utility such as Log4j would likely be better suited. In particular, Log4j would have proper management of threads
 * (see the todo)
 * below</P>
 */
public class Log {

    public static Verbosity verbose = Verbosity.INFO;
    private static String basePath;
    private static String dataPath;
    private static String configPath;
    private ZonedDateTime start;
    private static String timeStamp;
    private static File createLog;
    private static FileWriter log;
    private static DateTimeFormatter plainFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
    private static DateTimeFormatter richFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
    private static int storeDecodedCount = 0;
    private static int storeAudioCount = 0;

    /**
     * Constructor must be called at beginning of main program to configure the log directory and copy configuration files.
     * @param path Directory to create the current run log directory. Normally use ".\\Logs\\"
     * @param verbose Verbosity at which to print status messages.
     */
    public Log(String path, Verbosity verbose) { //TODO: Move to static initializer, then constructor can be private
        start = ZonedDateTime.now(ZoneId.of("UTC"));
        timeStamp = start.format(plainFormatter);
        Log.basePath = path + timeStamp;
        Log.verbose = verbose;

        dataPath = basePath + "\\PassData";
        if(! new File(dataPath).mkdirs()) {
            throw new RuntimeException("Error creating log folder.");
        }
        configPath = basePath + "\\Configuration";
        if(! new File(configPath).mkdirs()) {
            throw new RuntimeException("Error creating log folder.");
        }

        createLog = new File(basePath + "\\log.txt");
        try {
            if(!createLog.createNewFile()) {
                throw new RuntimeException("Error creating log file.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating log file.");
        }

        // Print intro messages
        prefixAndStore("AGSC started " + start.format(richFormatter));
        String hostname = null;
        try {
            hostname = new BufferedReader(
                    new InputStreamReader(Runtime.getRuntime().exec("hostname").getInputStream()))
                    .readLine();
        } catch (IOException e) {
            hostname = "Unknown";
        }
        prefixAndStore("Log saved to: " + createLog.getAbsolutePath());
        prefixAndStore("Run on: " + hostname);

        // Copy configuration files to log
        try {
            storeConfig(new FileReader(ConfigurationUtils.getStrProperty("TLE_PATH")), "tle.txt");
            storeConfig(new FileReader(ConfigurationUtils.getConfigPath()), "config.properties");
            storeConfig(new FileReader(ConfigurationUtils.getStrProperty("ROTATOR_CALIBRATION_PATH")), "rotatorCalibration.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Determine the next available audio recording file name.
     * @return Next available file.
     */
    public static File getNextAudioFile() {
        String audioPath = dataPath + "\\audio";
        if(! new File(audioPath).mkdirs()) {
            throw new RuntimeException("Error creating audio folder.");
        }
        File nextAudioFile = new File(dataPath + "\\audio\\recording" + storeAudioCount + ".wav");
        storeAudioCount++;
        return nextAudioFile;
    }

    /**
     * Print a {@link String} to the console and log file.
     * @param out
     */
    private static void printToConsoleAndFile(String out) {
        String formattedOut = out + "\n";
        System.out.print(formattedOut);
        // TODO: This nested try-catch is a poor way of resolving simultaneous calls to the log from different threads.
        //       Using a proper log utility such as Log4j would be better, however due to the small number of threads
        //       and relatively infrequent log calls, this setup works.
        try {
            log = new FileWriter(createLog, true);
            log.append(formattedOut);
            log.close();
        } catch (IOException e) {
            try {
                log = new FileWriter(createLog, true);
                log.append(formattedOut);
                log.close();
            } catch (IOException e2) {
                throw new RuntimeException("Could not write to log file.");
            }
        }
    }

    /**
     * Add the current time in UTC as a prefix to a string, then print to console and log file.
     * @param out Message to prefix with time stamp.
     */
    private static void prefixAndStore(String out) {
        String prefixedOut = ZonedDateTime.now(ZoneId.of("UTC")).format(richFormatter) + " " + out;
        printToConsoleAndFile(prefixedOut);
    }

    /**
     * If depending on a boolean, add the current time in UTC as a prefix to a string, then print to console and log file.
     * @param out Message to prefix with time stamp.
     * @param addTimeStamp Select if timestamp should be added. If false, out will be printed and stored without modification.
     */
    private static void prefixAndStore(String out, boolean addTimeStamp) {
        String prefixedOut;
        if (addTimeStamp) {
            prefixedOut = ZonedDateTime.now(ZoneId.of("UTC")).format(richFormatter) + " " + out;
        } else {
            prefixedOut = out;
        }
        printToConsoleAndFile(prefixedOut);
    }

    /**
     * Print a message to console and log without timestamp or verbosity level.
     * Use sparingly (i.e. only for printing options for the user to select, or data that must be printed across multiple lines),
     * most messages should have a verbosity and timestamp prefixed.
     * @param msg Message to be printed.
     */
    public static void noPrefix(String msg) {
        prefixAndStore(msg, false);
    }

    /**
     * Print a message at the INFO verbosity level. Message will only be printed to console and log if verbosity is DEBUG.
     * Use for detailed messages that normally would be unimportant to the user, but provide insight into the programs
     * functionality for debugging.
     * @param msg Message to be printed.
     */
    public static void debug(String msg) {
        if (verbose.isEqOrHigher(Verbosity.DEBUG)) {
            prefixAndStore("DEBUG: " + msg);
        }
    }

    /**
     * Print a message at the INFO verbosity level. Message will only be printed to console and log if verbosity is INFO
     * or higher (i.e. DEBUG). Use for general messages about the programs operation.
     * @param msg Message to be printed.
     */
    public static void info(String msg) {
        if (verbose.isEqOrHigher(Verbosity.INFO)) {
            prefixAndStore("INFO: " + msg);
        }
    }

    /**
     * Print a message at the WARN verbosity level. Message will only be printed to console and log if verbosity is WARN
     * or higher (i.e. INFO, DEBUG). Use for messages of high importance including notices of issues with potential to
     * cause critical issues or invalidate data being collected.
     * @param msg Message to be printed.
     */
    public static void warn(String msg) {
        if (verbose.isEqOrHigher(Verbosity.WARN)) {
            prefixAndStore("WARN: " + msg);
        }
    }

    /**
     * Print a message at the ERROR verbosity level. Message will only be printed to console and log if verbosity is ERROR
     * or higher (i.e. WARN, INFO, DEBUG). Use only for critical error messages pertaining to issues resulting in degradation of
     * service or immediate program termination.
     * @param msg Message to be printed.
     */
    public static void error(String msg) {
        if (verbose.isEqOrHigher(Verbosity.ERROR)) {
            prefixAndStore("ERROR: " + msg);
        }
    }

    /**
     * <P>Store a packet to the log. Creates a .bin and .txt to store the packet as binary data and as a human-readable hexdump.</P>
     * <P>Note: The .txt file must be opened with UTF8 encoding. Windows notepad does not always determine the correct encoding
     * and it cannot be changed. If the hexdump is not formatted correctly, open with Notepad++ and select UTF8.</P>
     * @param d Packet data.
     */
    public static void storeDecodedData(byte[] d) {
        // Create the DecodedData directory within the log dataPath. Done in this method so DecodedData dir isn't created
        // unless at least one packet is received.
        String decodedPath = dataPath + "\\DecodedData\\Packet" + storeDecodedCount;
        if(! new File(decodedPath).mkdirs()) {
            throw new RuntimeException("Error creating log folder.");
        }

        // Store as binary data
        File dataFile = new File(decodedPath + "\\data.bin");
        try {
            dataFile.createNewFile();
            FileOutputStream outStream = new FileOutputStream(dataFile, false);
            outStream.write(d, 0, d.length);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            Log.error("Could not write data to bin file in Log.");
            throw new RuntimeException(e);
        }

        // Store hexdump as text
        File textFile = new File(decodedPath + "\\data.txt");
        try {
            textFile.createNewFile();
            Writer textWriter = new OutputStreamWriter(new FileOutputStream(textFile), StandardCharsets.UTF_8);
            textWriter.write(HexadecimalUtils.hexDump(d));
            textWriter.close();
        } catch (IOException e) {
            Log.error("Could not write data to plain text file in Log.");
            throw new RuntimeException(e);
        }

        // Globally keep track of how many packets have been stored
        storeDecodedCount++;
    }

    /**
     * Copies a configuration file to the log. All configuration files must be stored in this way for reproducibility
     * of the programs operation.
     * @param configReader {@link FileReader} of the config file.
     * @param logFileName Name to store log file under (usually same as the input file name).
     */
    public static void storeConfig(FileReader configReader, String logFileName) {
        File logConfigFile = new File(configPath + "\\" + logFileName);
        try {
            logConfigFile.createNewFile();
            BufferedReader configBufferedReader = new BufferedReader(configReader);
            Writer textWriter = new OutputStreamWriter(new FileOutputStream(logConfigFile), StandardCharsets.UTF_8);
            String line = configBufferedReader.readLine();
            while (line != null) {
                textWriter.write(line);
                textWriter.write("\n");
                line = configBufferedReader.readLine();
            }
            textWriter.close();
        } catch (IOException e) {
            Log.error("Could not write config file to Log file.");
        }
    }

}
