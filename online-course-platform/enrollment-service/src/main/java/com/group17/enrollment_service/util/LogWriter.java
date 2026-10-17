package com.group17.enrollment_service.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LogWriter {

    private static final String LOG_PATH = "logs/enrollment_log.txt";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void append(String message) {
        try {
            Path path = Paths.get(LOG_PATH);
            Files.createDirectories(path.getParent());
            String line = "[" + LocalDateTime.now().format(FMT) + "] " + message + System.lineSeparator();
            Files.writeString(path, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Write log failed: " + e.getMessage(), e);
        }
    }
}
