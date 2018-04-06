package com.slyak.support.crawler.impl;

import com.slyak.support.crawler.CrawlerSession;
import com.slyak.support.crawler.SessionCallback;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;

/**
 * .
 *
 * @author stormning 2018/4/3
 * @since 1.3.0
 */
@Slf4j
public class DefaultSessionCallback implements SessionCallback {
    @Override
    public void onSessionCreated(CrawlerSession session, Connection.Response response) {
        log.info("Session created {}", session);
    }

    @Override
    public void onSessionDestroyed(String sessionId) {
        log.info("Session {} destroyed {}", sessionId);
    }
}
