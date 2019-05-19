package com.slyak.web.util;

import javax.servlet.http.HttpServletRequest;

/**
 * .
 *
 * @author stormning 2017/12/21
 * @since 1.3.0
 */
public class WebUtils extends org.springframework.web.util.WebUtils {

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWithHeader);
    }

}
