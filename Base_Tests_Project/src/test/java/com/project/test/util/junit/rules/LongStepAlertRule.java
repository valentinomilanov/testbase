package com.project.test.util.junit.rules;

import com.project.test.util.junit.TestBase;
import com.project.test.util.junit.TestBaseStaticConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.project.test.util.junit.TestBase;
import com.project.test.util.junit.TestBaseStaticConfig;

import ch.qos.logback.classic.Level;

public class LongStepAlertRule implements AfterEachCallback {

	public static PrintStream printer;
    
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (!TestBaseStaticConfig.isCheckForLongSteps()) {
            return;
        }
        if (!context.getExecutionException().isPresent()) {
            // only apply log on passed tests
            LogLine prevousLine = null;
            File warningFile = getWarningLogFile(context);
            warningFile.createNewFile();
            PrintStream printStream = new PrintStream(warningFile);
            try {
                for (String line: FileUtils.readLines(LoggingRule.getLogFile(context), Charset.defaultCharset())) {
                    LogLine logLine = parseLogLine(line);
                    if (prevousLine != null) {
                        if (prevousLine.getDate().plusSeconds(TestBaseStaticConfig.getLimitInSeconds()).isBefore(logLine.getDate())) {
                            //exceptions
                            
                            //launching the browser
                            if (prevousLine.getMessage().contains("Download path") || logLine.getMessage().contains("Go to the login page of your server")) {
                                prevousLine = logLine;
                                continue;
                            }
                            
                            //navigating to a page
                            if (prevousLine.getMessage().contains("url") || logLine.getMessage().contains("Currently on page")) {
                                prevousLine = logLine;
                                continue;
                            }                            
                                                                                    
                            printStream.println("Too much time between steps");
                            printStream.println("Previous line:");
                            printStream.println(prevousLine);
                            printStream.println("Current line:");
                            printStream.println(logLine);
                            printStream.println("____________________________________________________");
                        }
                    }
                    prevousLine = logLine;
                }
            } finally {
                printStream.close();
            }
        }
        
    }
    
    private LogLine parseLogLine(String line) {
        String[] parts = line.split("\\ ");
        String dateString = parts[0] + " " + parts[1];
        String levelString = parts[2];
        int separationIndex = 2;
        while (!parts[separationIndex].equals("-") && separationIndex < parts.length) {
            separationIndex++;
        }
        if (separationIndex >= parts.length) {
            throw new Error("Cound not parse line: " + line);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = ++separationIndex; i < parts.length; i++) {
            stringBuilder.append(" " + parts[i]);
        }
        String message = stringBuilder.toString().trim();
        LocalDateTime date = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss,SSS").parseLocalDateTime(dateString);
        Level leggerLevel = Level.toLevel(levelString);
        return new LogLine(date, leggerLevel, message);
    }
    
    public static File getWarningLogFile(ExtensionContext context) {
        String fileName = TestBase.getFileName(context);
        String path = "logs" + File.separator + DateTime.now().toString("yyyy-MM-dd") + File.separator + "Warning-" + fileName + ".log";
        return new File(path);      
    }
}
