package com.slyak.support.freemarker;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2017/12/25
 * @since 1.3.0
 */
public class SlyakRequestContext extends RequestContext {
    private static final UrlPathHelper urlPathHelper = new UrlPathHelper();
    private HttpServletRequest request;

    public SlyakRequestContext(HttpServletRequest request) {
        this.request = request;
    }

    public String query(String url, Map<String, ?> params) {
        if (StringUtils.isEmpty(url)) {
            //placeholder
            //foo/{bar}?spam={spam}
            return getContextUrl(url, params);
        } else {
            return replaceCurrentQuery(params);
        }
    }

    private String replaceCurrentQuery(Map<String, ?> params) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromRequest(request);
        if (!CollectionUtils.isEmpty(params)) {
            for (Map.Entry<String, ?> entry : params.entrySet()) {
                builder.replaceQueryParam(entry.getKey(), entry.getValue());
            }
        }
        UriComponents components = builder.build();
        UriComponents encodedComponents = components.encode();
        return encodedComponents.toUri().toASCIIString();
    }

    //same as baidu google

    /**
     * @param pageCount   page count
     * @param currentPage start from 0
     * @param showNum     button num
     */
    public Pagination pagination(int pageCount, int currentPage, int showNum) {
        int realShow = 1;
        int start = currentPage;
        int end = currentPage;
        for (int i = 0; i < showNum; i++) {
            if (start - 1 >= 0 && realShow < showNum) {
                ++realShow;
                --start;
            }
            if (end + 1 < pageCount && realShow < showNum) {
                ++realShow;
                ++end;
            }
        }
        boolean hasNext = currentPage + 1 < pageCount;
        boolean hasPrevious = currentPage > 0;
        return Pagination
                .builder()
                .start(start)
                .end(end)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }

    public String getContextPath() {
        return this.urlPathHelper.getOriginatingContextPath(this.request);
    }

    public static void main(String[] args) {
        System.out.println(new SlyakRequestContext(null).pagination(3, 2, 2));
    }
}
