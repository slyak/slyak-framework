package com.slyak.core.ssh2;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * .
 *
 * @author stormning 2018/5/18
 * @since 1.3.0
 */
@Slf4j
public class CustomStdLogger implements StdCallback {

    private Logger logger;

    private int lineCount = 0;

    private Integer exitStatus = -1;

    private int lastErrorLine = -1;

    public CustomStdLogger(Logger logger) {
        this.logger = logger;
    }

    public void info(String info) {
        logger.info(info);
    }

    @Override
    public void processOut(String out) {
        logger.info(out);
        log.info(out);
//        lineCount++;
    }

    @Override
    public void processError(String error) {
        logger.error(error);
        log.error(error);
//        this.lastErrorLine = lineCount;
//        lineCount++;
    }

    @Override
    public void setExistStatus(Integer exitStatus) {
        this.exitStatus = exitStatus;
    }

    public boolean hasError() {
//        return lastErrorLine >= 0 && lastErrorLine == lineCount - 1;
        return exitStatus != 0;
    }
}
