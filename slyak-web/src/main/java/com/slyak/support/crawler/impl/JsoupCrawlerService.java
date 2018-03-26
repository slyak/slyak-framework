package com.slyak.support.crawler.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slyak.support.crawler.CrawlerService;
import com.slyak.support.crawler.CrawlerSession;
import com.slyak.support.crawler.UrlExchange;
import com.slyak.support.crawler.exception.InvalidSessionException;
import com.slyak.support.crawler.exception.UnreachableException;
import com.slyak.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpMethod;

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
public class JsoupCrawlerService implements CrawlerService<Document> {

    @Setter
    private int timeoutMillis;

    @Setter
    @Getter
    private int retry = 2;

    private Map<String, CrawlerSession> sessionCache = Maps.newConcurrentMap();

    //initUrl->siteId
    private Map<String, UrlExchange> exchangeMap = Maps.newConcurrentMap();

    @Override
    public CrawlerSession getSession(String sessionId) {
        CrawlerSession session = sessionCache.get(sessionId);
        if (session == null) {
            throw new InvalidSessionException();
        }
        return session;
    }

    @Override
    public List<String> getInitUrlSessions(String initUrl) {
        UrlExchange urlExchange = exchangeMap.get(initUrl);
        if (urlExchange == null) {
            return Collections.emptyList();
        } else {
            List<String> ids = Lists.newArrayList();
            String siteId = urlExchange.getSiteId();
            for (CrawlerSession session : sessionCache.values()) {
                if (siteId.equals(session.getSiteId())) {
                    ids.add(session.getId());
                }
            }
            return ids;
        }
    }

    @Override
    public String initSession(String initUrl) {
        Connection.Response response = executeWithRetry(get(initUrl));
        if (response != null) {
            return createSession(exchange(initUrl), response);
        }
        return null;
    }

    @Override
    public boolean isLogin(String initUrl) {
        UrlExchange urlExchange = exchangeMap.get(initUrl);
        return urlExchange != null && urlExchange.isLogin();
    }

    private String exchange(String initUrl) {
        UrlExchange urlExchange = exchangeMap.get(initUrl);
        if (urlExchange == null) {
            urlExchange = new UrlExchange();
            urlExchange.setSiteId(RandomStringUtils.randomAlphabetic(4));
            exchangeMap.put(initUrl, urlExchange);
        }
        return urlExchange.getSiteId();
    }

    private String getInitUrl(String siteId) {
        for (Map.Entry<String, UrlExchange> entry : exchangeMap.entrySet()) {
            if (siteId.equals(entry.getValue().getSiteId())) {
                return entry.getKey();
            }
        }
        return null;
    }


    private String createSession(String siteId, Connection.Response response) {
        CrawlerSession session = new CrawlerSession();
        session.setSiteId(siteId);
        mergeSession(session, response);
        String sessionId = RandomStringUtils.randomAlphabetic(6);
        session.setId(sessionId);
        sessionCache.put(sessionId, session);
        log.info("Session created :{}", session);
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
    public byte[] getCaptcha(String sessionId, String captchaUrl) throws UnreachableException {
        Connection.Response response = executeWithSession(sessionId, get(captchaUrl));
        if (response != null) {
            return response.bodyAsBytes();
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
        String initUrl = getInitUrl(session.getSiteId());
        UrlExchange urlExchange = exchangeMap.get(initUrl);
        urlExchange.setLogin(true);
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
        return safeParse(executeWithSession(sessionId, prepareFetch(method, url, headers, data)));
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
        String initUrl = getInitUrl(session.getSiteId());
        if (!isSessionValid(initUrl, response)) {
            log.info("Session is invalid, start to evict session objects.");
            //evict session
            sessionCache.remove(sessionId);
            exchangeMap.get(initUrl).setLogin(false);
            throw new InvalidSessionException();
        }
        mergeSession(session, response);
        return response;
    }

    protected boolean isSessionValid(String initUrl, Connection.Response response) {
        log.info("Response url is : {}", response.url());
        return !StringUtils.equals(initUrl, response.url().toString());
    }

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
            return connection.execute();
        } catch (IOException e) {
            throw new UnreachableException(connection.request().url().toString());
        }
    }
}
