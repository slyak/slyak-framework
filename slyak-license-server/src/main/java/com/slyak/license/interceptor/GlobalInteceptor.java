package com.slyak.license.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.slyak.license.support.DevHelper;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GlobalInteceptor extends HandlerInterceptorAdapter implements InitializingBean {

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private List<Menu> menus;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        initMenu(request);
        return super.preHandle(request, response, handler);
    }

    private void initMenu(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String requestUri = urlPathHelper.getRequestUri(request);
        List<Menu> cloned = cloneMenus(getMenus());
        Menu current = walkMenu(cloned, null, requestUri);
        session.setAttribute("menus", cloned);
        if (current != null) {
            session.setAttribute("current_menu", current);
        }
    }

    private List<Menu> getMenus() {
        if (DevHelper.isDev()) {
            return getMenusFromConfig(DevHelper.getSourceCodeLocation() + "/src/main/resources/menu.json");
        }
        return menus;
    }

    private List<Menu> cloneMenus(List<Menu> menus) {
        List<Menu> cloned = Lists.newArrayList();
        for (Menu menu : menus) {
            Menu clone = menu.clone();
            List<Menu> children = menu.getChildren();
            if (!CollectionUtils.isEmpty(children)) {
                clone.setChildren(cloneMenus(children));
            }
            cloned.add(clone);
        }
        return cloned;
    }

    private Menu walkMenu(List<Menu> menus, Menu parent, String requestUri) {
        for (Menu menu : menus) {
            if (CollectionUtils.isEmpty(menu.getChildren())) {
                if (menu.getUrl().equals(requestUri) || matchAny(requestUri, menu.getPatterns())) {
                    menu.setChecked(true);
                    if (parent != null) {
                        parent.setChecked(true);
                    }
                    return menu;
                }
            } else {
                Menu current = walkMenu(menu.getChildren(), menu, requestUri);
                if (current != null) {
                    return current;
                }
            }
        }
        return null;
    }

    private boolean matchAny(String requestUri, String... patterns) {
        if (patterns == null) {
            return false;
        }
        for (String pattern : patterns) {
            if (antPathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    private List<Menu> getMenusFromConfig(String jsonFile) {
        Resource resource = resourceLoader.getResource(jsonFile);
        String json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        return JSON.parseObject(json, new TypeReference<List<Menu>>() {
        });
    }

    @Override
    public void afterPropertiesSet() {
        this.menus = getMenusFromConfig(ResourceUtils.CLASSPATH_URL_PREFIX + "menu.json");
    }
}