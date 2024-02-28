package utils;

import utils.enums.Verbosity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    public static Verbosity verbose = Verbosity.INFO;
    static String path;
    LocalDateTime start;
    static String timeStamp;
    static File createLog;
    static FileWriter log;
    DateTimeFormatter plainFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
    DateTimeFormatter richFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
    static int storeDecodedCount = 0;

    public Log(String path, Verbosity verbose) {
        this.path = path;

        start = LocalDateTime.now();
        timeStamp = start.format(plainFormatter);
        if(! new File(path + timeStamp).mkdirs()) {
            throw new RuntimeException("Error creating log folder.");
        }
        createLog = new File(path + timeStamp + "\\log.txt");
        try {
            if(! createLog.createNewFile()) {
                throw new RuntimeException("Error creating log file.");
            }
            log = new FileWriter(createLog);
            log.write("VE3RMC-control started " + start.format(richFormatter));
            log.close();
        } catch (IOException e) {
            throw new RuntimeException("Error creating log file.");
        }

    }

    public static void debug(String s) {
        if (!verbose.isEqOrHigher(Verbosity.DEBUG)) {
            return;
        }
        try {
            log = new FileWriter(createLog, true);
            log.append("\nDEBUG: " + s);
            log.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write to log file.");
        }
    }


    public static void info(String s) {
        if (!verbose.isEqOrHigher(Verbosity.INFO)) {
            return;
        }
        try {
            log = new FileWriter(createLog, true);
            log.append("\nINFO: " + s);
            log.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write to log file.");
        }
    }

    public static void warn(String s) {
        if (!verbose.isEqOrHigher(Verbosity.WARN)) {
            return;
        }
        try {
            log = new FileWriter(createLog, true);
            log.append("\nWARN: " + s);
            log.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write to log file.");
        }
    }

    public static void error(String s) {
        if (!verbose.isEqOrHigher(Verbosity.ERROR)) {
            return;
        }
        try {
            log = new FileWriter(createLog, true);
            log.append("\nERROR: " + s);
            log.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write to log file.");
        }
    }

    public static void storeDecodedData(byte[] d) {
        String decodedPath = path + timeStamp + "\\DecodedData\\Packet" + storeDecodedCount;
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
            FileWriter textWriter = new FileWriter(textFile);
            textWriter.write(HexFormat.hexDump(d));
            textWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        storeDecodedCount++;
    }

}
