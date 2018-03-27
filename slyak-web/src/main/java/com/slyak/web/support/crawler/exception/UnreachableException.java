package com.slyak.web.support.crawler.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * .
 *
 * @author stormning 2017/12/19
 * @since 1.3.0
 */
public class UnreachableException extends NestedRuntimeException {
    public UnreachableException(String url) {
        super(url + " unreachable!");
    }
}
