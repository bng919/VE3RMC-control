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
    private static String path;
    private LocalDateTime start;
    private static String timeStamp;
    private static File createLog;
    private static FileWriter log;
    private static DateTimeFormatter plainFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
    private static DateTimeFormatter richFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
    private static int storeDecodedCount = 0;

    public Log(String path, Verbosity verbose) {
        this.path = path;
        this.verbose = verbose;

        start = LocalDateTime.now();
        timeStamp = start.format(plainFormatter);
        if(! new File(path + timeStamp).mkdirs()) {
            throw new RuntimeException("Error creating log folder.");
        }
        createLog = new File(path + timeStamp + "\\log.txt");
        try {
            if(!createLog.createNewFile()) {
                throw new RuntimeException("Error creating log file.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating log file.");
        }
        printToConsoleAndFile("VE3RMC-control started " + start.format(richFormatter));
    }

    private static void printToConsoleAndFile(String out) {
        String timeStamp = LocalDateTime.now().format(richFormatter);
        String formattedOut = timeStamp + " " + out + "\n";
        System.out.print(formattedOut);
        try {
            log = new FileWriter(createLog, true);
            log.append(formattedOut);
            log.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write to log file.");
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
