package com.slyak.support.crawler;

import com.slyak.web.support.crawler.CrawlerSession;
import org.jsoup.Connection;

/**
 * .
 *
 * @author stormning 2018/4/3
 * @since 1.3.0
 */
public interface SessionCallback {
    void onSessionCreated(CrawlerSession session, Connection.Response response);
    void onSessionDestroyed(String sessionId);
}