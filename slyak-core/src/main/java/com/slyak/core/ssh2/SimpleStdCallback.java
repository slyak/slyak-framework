package com.slyak.core.ssh2;

import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2018/5/16
 * @since 1.3.0
 */
@Slf4j
public enum SimpleStdCallback implements StdCallback {
    INSTANCE;

    @Override
    public void processOut(String out) {
        log.info(out);
    }

    @Override
    public void processError(String error) {
        log.info(error);
    }
}
