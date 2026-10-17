package com.group17.course_service.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CourseLogWriter {

    private static final String LOG_FILE = "logs/course_log.txt";

    private Path path() {
        Path p = Paths.get(LOG_FILE);
        try {
            Files.createDirectories(p.getParent());
            if (!Files.exists(p)) Files.createFile(p);
        } catch (IOException e) {
            throw new RuntimeException("Init course log failed", e);
        }
        return p;
    }

    private String now() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public synchronized void log(String message) {
        try {
            String line = "[" + now() + "] " + message + System.lineSeparator();
            Files.write(path(), line.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Write course log failed", e);
        }
    }
}
