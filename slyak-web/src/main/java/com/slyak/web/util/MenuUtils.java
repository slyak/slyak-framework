package com.slyak.web.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.slyak.web.domain.Menu;

import java.util.List;

/**
 * .
 *
 * @author stormning 2018/3/27
 * @since 1.3.0
 */
public class MenuUtils {
    private MenuUtils() {
    }

    public static List<Menu> build(Object menuObjects) {
        return JSON.parseObject(JSON.toJSONString(menuObjects), new TypeReference<List<Menu>>() {
        });
    }
}
