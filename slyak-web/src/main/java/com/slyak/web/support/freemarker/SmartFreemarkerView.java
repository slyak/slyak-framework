package com.slyak.web.support.freemarker;

import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2017/12/25
 * @since 1.3.0
 */
public class SmartFreemarkerView extends FreeMarkerView {

    private static final String SLYAK_REQUEST_CONTEXT = "slyakRequestContext";

    @Override
    protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
        model.put(SLYAK_REQUEST_CONTEXT, new SlyakRequestContext(request));
    }
}
