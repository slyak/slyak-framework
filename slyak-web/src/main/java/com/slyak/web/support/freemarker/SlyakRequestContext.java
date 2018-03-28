package com.slyak.web.support.freemarker;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * .
 *
 * @author stormning 2017/12/25
 * @since 1.3.0
 */
public class SlyakRequestContext extends RequestContext {

    private static final String ATTR_RESOURCE_HOLDER = "_a_r_holder";
    private static Map<String, String> version;
    private static final String CSS_FORMAT = "<link href=\"%s\" rel=\"stylesheet\">";
    private static final String JS_FORMAT = "<script src=\"%s\"></script>";

    static {
        version = Maps.newHashMap();
        version.put("version", RandomStringUtils.randomAlphabetic(4));
    }

    public SlyakRequestContext(HttpServletRequest request) {
        super(request);
    }

    public String query(String url, Map<String, ?> params) {
        if (StringUtils.isEmpty(url)) {
            return replaceCurrentQuery(params);
        } else {
            //placeholder
            //foo/{bar}?spam={spam}
            return getContextUrl(url, params);
        }
    }

    private String replaceQuery(String url, Map<String, ?> params) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromUriString(url);
        return resetParams(builder,params);
    }

    private String replaceCurrentQuery(Map<String, ?> params) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromRequest(getRequest());
        return resetParams(builder, params);
    }

    private String resetParams(UriComponentsBuilder builder, Map<String, ?> params) {
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

    public String css(Object url) {
        return determineResource(url, CSS_FORMAT);
    }


    public String js(Object url) {
        return determineResource(url, JS_FORMAT);
    }

    @SuppressWarnings("unchecked")
    private String determineResource(Object url, String format) {
        if (url instanceof Collection) {
            return singleResources((Collection<String>) url, format);
        } else {
            return singleResource((String) url, format);
        }
    }

    private String singleResources(Collection<String> urls, String format) {
        StringBuilder builder = new StringBuilder();
        for (String u : urls) {
            builder.append(singleResource(u, format)).append(StringUtils.LF);
        }
        return builder.toString();
    }

    private String singleResource(String url, String format) {
        String realUrl = getResource(url);
        if (addResource(realUrl)) {
            return format == null ? realUrl : String.format(format, realUrl);
        }
        return StringUtils.EMPTY;
    }

    public String getResource(String url) {
        if (url.startsWith("http")) {
            return url;
        } else {
            return replaceQuery(url, version);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean addResource(String url) {
        Set<String> resources = (Set<String>) getRequest().getAttribute(ATTR_RESOURCE_HOLDER);
        if (resources == null) {
            resources = Sets.newHashSet();
        }
        return resources.add(url);
    }

    public static void main(String[] args) {
        Set<String> test = Sets.newHashSet("test");
        System.out.println(test.add("test1"));
    }
}
