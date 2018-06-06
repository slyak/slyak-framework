package com.slyak.web.support.freemarker;

import freemarker.template.Configuration;

/**
 * .
 * <p>
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 2015/8/2.
 */
public class FmContext {
    private static Configuration cfg;
    private static FreemarkerTemplateRender templateRender;

    static void setConfiguration(Configuration cfg) {
        FmContext.cfg = cfg;
        FmContext.templateRender = new FreemarkerTemplateRender(cfg);
    }

    public static Configuration getConfiguration() {
        return cfg;
    }

    public static FreemarkerTemplateRender getTemplateRender() {
        return templateRender;
    }
}
