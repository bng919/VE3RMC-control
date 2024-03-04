package utils;

import utils.enums.Verbosity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    public static Verbosity verbose = Verbosity.INFO;
    private static String basePath;
    private static String dataPath;
    private ZonedDateTime start;
    private static String timeStamp;
    private static File createLog;
    private static FileWriter log;
    private static DateTimeFormatter plainFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
    private static DateTimeFormatter richFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
    private static int storeDecodedCount = 0;
    private static int storeAudioCount = 0;

    public Log(String path, Verbosity verbose) {
        start = ZonedDateTime.now(ZoneId.of("UTC"));
        timeStamp = start.format(plainFormatter);
        Log.basePath = path + timeStamp;
        Log.verbose = verbose;

        dataPath = basePath + "\\PassData";
        if(! new File(dataPath).mkdirs()) {
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
        printToConsoleAndFile("VE3RMC-control started " + start.format(richFormatter));
        String hostname = null;
        try {
            hostname = new BufferedReader(
                    new InputStreamReader(Runtime.getRuntime().exec("hostname").getInputStream()))
                    .readLine();
        } catch (IOException e) {
            hostname = "Unknown";
        }
        printToConsoleAndFile("Log saved to: " + createLog.getAbsolutePath());
        printToConsoleAndFile("Run on: " + hostname);
    }

    public static File getNextAudioFile() {
        String audioPath = dataPath + "\\audio";
        if(! new File(audioPath).mkdirs()) {
            throw new RuntimeException("Error creating audio folder.");
        }
        File nextAudioFile = new File(dataPath + "\\audio\\recording" + storeAudioCount + ".wav");
        storeAudioCount++;
        return nextAudioFile;
    }

    private static void printToConsoleAndFile(String out) {
        String timeStamp = ZonedDateTime.now(ZoneId.of("UTC")).format(richFormatter);
        String formattedOut = timeStamp + " " + out + "\n";
        System.out.print(formattedOut);
        try {
            log = new FileWriter(createLog, true);
            log.append(formattedOut);
            log.close();
        } catch (IOException e) { //TODO: Get rid of this catastrophe.... log4j
            try {
                log = new FileWriter(createLog, true);
                log.append(formattedOut);
                log.close();
            } catch (IOException e2) {
                throw new RuntimeException("Could not write to log file.");
            }
        }
    }

    public static void debug(String s) {
        if (verbose.isEqOrHigher(Verbosity.DEBUG)) {
            printToConsoleAndFile("DEBUG: " + s);
        }
    }

    public static void info(String s) {
        if (verbose.isEqOrHigher(Verbosity.INFO)) {
            printToConsoleAndFile("INFO: " + s);
        }
    }
    
    public static void warn(String s) {
        if (verbose.isEqOrHigher(Verbosity.WARN)) {
            printToConsoleAndFile("WARN: " + s);
        }
    }

    public static void error(String s) {
        if (verbose.isEqOrHigher(Verbosity.ERROR)) {
            printToConsoleAndFile("ERROR: " + s);
        }
    }

    public static void storeDecodedData(byte[] d) {
        String decodedPath = dataPath + "\\DecodedData\\Packet" + storeDecodedCount;
        if(! new File(decodedPath).mkdirs()) {
            throw new RuntimeException("Error creating log folder.");
        }
        File dataFile = new File(decodedPath + "\\data.bin");
        try {
            dataFile.createNewFile();
            FileOutputStream outStream = new FileOutputStream(dataFile, false);
            outStream.write(d, 0, d.length);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File textFile = new File(decodedPath + "\\data.txt");
        try {
            textFile.createNewFile();
            Writer textWriter = new OutputStreamWriter(new FileOutputStream(textFile), StandardCharsets.UTF_8);
            textWriter.write(HexFormat.hexDump(d));
            textWriter.close();
            //TODO: Which is better?
            /*FileWriter textWriter = new FileWriter(textFile);
            textWriter.write(HexFormat.hexDump(d));
            textWriter.close();*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        storeDecodedCount++;
    }

}
