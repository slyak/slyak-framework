package com.slyak.core.ssh2;

import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2018/5/16
 * @since 1.3.0
 */
@Slf4j
public class SimpleStdCallback implements StdCallback {
    public static SimpleStdCallback INSTANCE = new SimpleStdCallback();

    public SimpleStdCallback() {
    }

    @Override
    public void processOut(String out) {
        log.info(out);
    }

    @Override
    public void processError(String error) {
        log.info(error);
    }

    @Override
    public void setExistStatus(Integer exitStatus) {
        log.info("Exist status {}", exitStatus);
    }

}
