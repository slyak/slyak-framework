package com.slyak.support.crawler.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slyak.concurrent.ExecutorUtils;
import com.slyak.support.crawler.CrawlerService;
import com.slyak.support.crawler.CrawlerSession;
import com.slyak.support.crawler.SessionCallback;
import com.slyak.support.crawler.exception.InvalidSessionException;
import com.slyak.support.crawler.exception.UnreachableException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpMethod;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2017/12/18
 * @since 1.3.0
 */
@Slf4j
public abstract class JsoupCrawlerService implements CrawlerService<Document> {

    @Setter
    @Getter
    private int timeoutMillis = 5000;

    @Setter
    @Getter
    private int retry = 2;

    @Setter
    private SessionCallback sessionCallback = new DefaultSessionCallback();

    //{initUrl:[sessionIds]}
    private final Map<String, List<String>> urlSessionIds = Maps.newConcurrentMap();

    //{sessionId:session}
    private final Map<String, CrawlerSession> sessions = Maps.newConcurrentMap();

    private static final Object SESSION_LOCK = new Object();

    @Override
    public CrawlerSession getSession(String sessionId) {
        synchronized (SESSION_LOCK) {
            CrawlerSession session = sessions.get(sessionId);
            if (session == null) {
                throw new InvalidSessionException(sessionId);
            } else {
                return session;
            }
        }
    }

    @Override
    public List<String> getUrlSessions(String initUrl) {
        synchronized (SESSION_LOCK) {
            List<String> sessionIds = urlSessionIds.get(initUrl);
            if (sessionIds == null) {
                return Collections.emptyList();
            }
            return sessionIds;
        }
    }

    @Override
    public List<String> getUrlLoginSessions(String initUrl) {
        List<String> urlSessions = getUrlSessions(initUrl);
        List<String> filtered = Lists.newArrayList();
        for (String sessionId : urlSessions) {
            try {
                CrawlerSession session = getSession(sessionId);
                if (session.isLogin()) {
                    filtered.add(sessionId);
                }
            } catch (InvalidSessionException e) {
                //ignore
            }
        }
        return filtered;
    }

    @Override
    public String initSession(String initUrl) {
        Connection.Response response = executeWithRetry(get(initUrl));
        if (response != null) {
            return createSession(initUrl, response);
        }
        return initSession(initUrl);
    }

    private String createSession(String initUrl, Connection.Response response) {
        CrawlerSession session = new CrawlerSession();
        session.setInitUrl(initUrl);
        mergeSession(session, response);
        String sessionId = RandomStringUtils.randomAlphabetic(6);
        session.setId(sessionId);
        synchronized (SESSION_LOCK) {
            List<String> sessionIds = urlSessionIds.computeIfAbsent(initUrl, url -> Lists.newArrayList());
            sessionIds.add(sessionId);
            sessions.put(sessionId, session);
        }
        sessionCallback.onSessionCreated(session, response);
        return sessionId;
    }


    private void mergeSession(CrawlerSession session, Connection.Response response) {
        if (response != null) {
            Map<String, String> cookies = response.cookies();
            if (cookies != null) {
                Map<String, String> exist = session.getCookies();
                if (exist == null) {
                    session.setCookies(cookies);
                } else {
                    for (Map.Entry<String, String> entry : cookies.entrySet()) {
                        exist.put(entry.getKey(), entry.getValue());
                    }
                    session.setCookies(exist);
                }
            }
        }
    }

    @Override
    public BufferedInputStream getCaptcha(String sessionId, String captchaUrl) throws UnreachableException {
        CrawlerSession session = getSession(sessionId);
        synchronized (SESSION_LOCK) {
            if (captchaUrl.equals(session.getInitUrl())) {
                BufferedInputStream captcha = session.getCaptcha();
                if (captcha != null) {
                    session.setCaptcha(null);
                    return captcha;
                }
            }
        }
        Connection.Response response = executeWithSession(sessionId, get(captchaUrl));
        if (response != null) {
            return response.bodyStream();
        }
        return null;
    }

    @Override
    public boolean login(String sessionId, String loginUrl, Map<String, String> data) throws UnreachableException, InvalidSessionException {
        try {
            executeWithSession(sessionId, prepareFetch(HttpMethod.POST, loginUrl, null, data));
        } catch (UnreachableException | InvalidSessionException e) {
            return false;
        }
        CrawlerSession session = getSession(sessionId);
        log.info("Session with id {} logged in");
        session.setLogin(true);
        return true;
    }

    private Connection prepareFetch(HttpMethod method, String url, Map<String, String> headers, Map<String, String> data) {
        Connection connection = method == HttpMethod.POST ? post(url) : get(url);
        if (headers != null) {
            connection.headers(headers);
        }
        if (data != null) {
            connection.data(data);
        }
        return connection;
    }

    @Override
    public Document fetchDocument(
            String sessionId,
            String url,
            HttpMethod method,
            Map<String, String> headers,
            Map<String, String> data
    ) throws UnreachableException, InvalidSessionException {
        log.info("start fetch url {}, session id is {}", url, sessionId);
        return safeParse(executeWithSession(sessionId, prepareFetch(method, url, headers, data)));
    }

    @Override
    public Document fetchDocument(List<String> sessionIds, String url, HttpMethod method, Map<String, String> headers, Map<String, String> data) throws UnreachableException, InvalidSessionException {
        return ExecutorUtils.startCompetition(index -> fetchDocument(sessionIds.get(index), url, method, headers, data), sessionIds.size(), timeoutMillis);
    }

    @Override
    public Document fetchDocument(
            String url,
            HttpMethod method,
            Map<String, String> headers,
            Map<String, String> data
    ) throws UnreachableException {
        return safeParse(executeWithRetry(prepareFetch(method, url, headers, data)));
    }

    private Document safeParse(Connection.Response response) {
        if (response != null) {
            try {
                return response.parse();
            } catch (IOException e) {
                log.error("Doc parse exception.", e);
            }
        }
        return null;
    }

    private Connection get(String url) {
        return config(url).method(Connection.Method.GET);
    }

    private Connection post(String url) {
        return config(url).method(Connection.Method.POST);
    }

    private Connection config(String url) {
        log.info("Destination url is {}", url);
        Connection connection = Jsoup.connect(url).ignoreContentType(true).followRedirects(true);
        if (timeoutMillis > 0) {
            connection.timeout(timeoutMillis);
        }
        return connection;
    }

    private Connection.Response executeWithRetry(Connection connection) {
        return executeWithRetry(connection, 0);
    }

    private Connection.Response executeWithSession(String sessionId, Connection connection) {
        assert sessionId != null;
        CrawlerSession session = getSession(sessionId);
        if (session.getCookies() != null) {
            //session cookies
            connection.cookies(session.getCookies());
        }
        Connection.Response response = executeWithRetry(connection);
        //is session valid
        if (!isSessionValid(response)) {
            log.info("Session with id {} is invalid, start to evict session objects.", sessionId);
            //evict session
            destroySession(sessionId);
            throw new InvalidSessionException(sessionId);
        }
        mergeSession(session, response);
        return response;
    }

    private void destroySession(String sessionId) {
        synchronized (SESSION_LOCK) {
            CrawlerSession session = sessions.get(sessionId);
            if (session != null) {
                String initUrl = session.getInitUrl();
                urlSessionIds.get(initUrl).removeIf(sessionId::equals);
                sessions.remove(sessionId);
            }
        }
        sessionCallback.onSessionDestroyed(sessionId);
    }

    protected abstract boolean isSessionValid(Connection.Response response);

    private Connection.Response executeWithRetry(Connection connection, int count) {
        try {
            return execute(connection);
        } catch (UnreachableException ue) {
            int retry = getRetry();
            if (retry > 0 && count < this.retry) {
                log.debug("Connection :{} retrying {} times...", connection, count + 1);
                return executeWithRetry(connection, count + 1);
            }
            throw ue;
        }
    }

    private Connection.Response execute(Connection connection) {
        try {
            Connection.Response response = connection.execute();
            log.info("Response url is {}", response.url());
            return response;
        } catch (IOException e) {
            throw new UnreachableException(connection.request().url().toString());
        }
    }
}
