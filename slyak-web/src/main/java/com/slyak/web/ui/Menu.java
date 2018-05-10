package com.slyak.web.ui;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * .
 *
 * @author stormning 2018/3/27
 * @since 1.3.0
 */
@Data
public class Menu implements Serializable {
    private String title;
    private String url;
    private List<Menu> children;

    public boolean isActive(String requestUri) {
        if (hasChildren()) {
            for (Menu child : children) {
                if (child.isActive(requestUri)) {
                    return true;
                }
            }
            return false;
        } else {
            String[] path = StringUtils.split(requestUri.replaceFirst("(.*)(\\?.*)", "$1"), '/');
            if (path == null || path.length == 0) {
                return false;
            }
            return url.startsWith("/" + path[0]);
        }
    }

    public boolean hasChildren() {
        return !CollectionUtils.isEmpty(children);
    }

    public static void main(String[] args) {
        String[] split = StringUtils.split("/project/test", '/');
        System.out.println(split[0]);
    }
}