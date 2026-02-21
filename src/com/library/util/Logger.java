package com.library.util;

import com.library.constants.AppConstants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class Logger {
    private static final Path LOG_PATH = Path.of(AppConstants.LOG_FILE);

    public static void info(String message) {
        write("INFO", message);
    }

    public static void error(String message) {
        write("ERROR", message);
    }

    private static void write(String level, String message) {
        String line = LocalDateTime.now() + " [" + level + "] " + message + System.lineSeparator();
        System.out.print(line);
        try {
            Files.writeString(LOG_PATH, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
        }
    }
}

