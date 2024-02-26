package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

    String path;
    LocalDateTime start;
    String timeStamp;
    File createLog;
    FileWriter log;
    DateTimeFormatter plainFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
    DateTimeFormatter richFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

    public Log(String path) {
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

    public void info(String s) {
        try {
            log = new FileWriter(createLog, true);
            log.append("\nINFO: " + s);
            log.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write to log file.");
        }
    }

    public void warn(String s) {
        try {
            log = new FileWriter(createLog, true);
            log.append("\nWARN: " + s);
            log.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write to log file.");
        }
    }

    public void error(String s) {
        try {
            log = new FileWriter(createLog, true);
            log.append("\nERROR: " + s);
            log.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write to log file.");
        }
    }

}
