package com.slyak.support.crawler;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2017/12/18
 * @since 1.3.0
 */
@Data
public class CrawlerSession implements Serializable {
    private String id;
    private String siteId;
    private Map<String, String> cookies;
}
