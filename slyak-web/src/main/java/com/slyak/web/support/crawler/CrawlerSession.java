package com.slyak.web.support.crawler;

import lombok.Data;
import lombok.ToString;

import java.io.BufferedInputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2017/12/18
 * @since 1.3.0
 */
@Data
@ToString
public class CrawlerSession implements Serializable {
    private String id;
    private String initUrl;
    private Map<String, String> cookies;
    private boolean login;
    private BufferedInputStream bodyStream;
}
