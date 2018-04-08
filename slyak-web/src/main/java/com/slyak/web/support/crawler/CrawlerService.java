package com.slyak.web.support.crawler;

import com.slyak.web.support.crawler.exception.InvalidSessionException;
import com.slyak.web.support.crawler.exception.UnreachableException;
import org.springframework.http.HttpMethod;

import java.io.BufferedInputStream;
import java.util.List;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2017/12/18
 * @since 1.3.0
 */
public interface CrawlerService<D> {

    CrawlerSession getSession(String sessionId);

    List<String> getUrlSessions(String initUrl);

    List<String> getUrlLoginSessions(String initUrl);

    String initSession(String initUrl) throws UnreachableException;

    BufferedInputStream getCaptcha(String sessionId, String captchaUrl) throws UnreachableException, InvalidSessionException;

    boolean login(String sessionId, String loginUrl, Map<String, String> data) throws UnreachableException, InvalidSessionException;

    D fetchDocument(String url, HttpMethod method, Map<String, String> headers, Map<String, String> data) throws UnreachableException;

    D fetchDocument(String sessionId, String url, HttpMethod method, Map<String, String> headers, Map<String, String> data) throws UnreachableException, InvalidSessionException;

    D fetchDocument(List<String> sessionIds, String url, HttpMethod method, Map<String, String> headers, Map<String, String> data) throws UnreachableException, InvalidSessionException;
}
