package com.slyak.core.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.LoggerFactory;

/**
 * .
 *
 * @author stormning 2018/5/18
 * @since 1.3.0
 */
public class LoggerUtils {

    private LoggerUtils() {
    }

    private static final String DEFAULT_PATTERN = "${FILE_LOG_PATTERN:-%d{yyyy-MM-dd HH:mm:ss.SSS} ${LOG_LEVEL_PATTERN:-%5p} ${PID:- } --- [%t] %-40.40logger{39} : %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}";

    public static Logger createLogger(String file) {
        return createLogger(file, "%msg%n");
    }

    public static Logger createLogger(String file, String pattern) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder defaultEncoder = new PatternLayoutEncoder();
        defaultEncoder.setPattern(pattern == null ? DEFAULT_PATTERN : pattern);
        defaultEncoder.setContext(loggerContext);
        defaultEncoder.start();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setFile(file);
        fileAppender.setEncoder(defaultEncoder);
        fileAppender.setContext(loggerContext);
        fileAppender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(RandomStringUtils.randomAlphanumeric(6));


        logger.addAppender(fileAppender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);
        return logger;
    }

    public static void main(String[] args) {
        Logger logger = LoggerUtils.createLogger("/Users/stormning/Downloads/test.log");
        logger.info("this is a test log");

    }
}
