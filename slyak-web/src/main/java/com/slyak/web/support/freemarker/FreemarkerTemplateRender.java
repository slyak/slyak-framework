package com.slyak.web.support.freemarker;

import com.google.common.hash.Hashing;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;

/**
 * .
 *
 * @author stormning 2018/5/28
 * @since 1.3.0
 */
public class FreemarkerTemplateRender {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreemarkerTemplateRender.class);

    private final Configuration configuration;

    private StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();

    public FreemarkerTemplateRender(Configuration configuration) {
        TemplateLoader templateLoader = configuration.getTemplateLoader();
        if (templateLoader == null) {
            configuration.setTemplateLoader(stringTemplateLoader);
        } else {
            configuration.setTemplateLoader(new MultiTemplateLoader(new TemplateLoader[]{templateLoader, stringTemplateLoader}));
        }
        this.configuration = configuration;
    }


    public String renderNamedTpl(String tplName, Object model) {
        return renderTpl(getTemplate(tplName), model);
    }

    public String renderStringTpl(String content, Object model) {
        return renderTpl(createIfAbsent(content), model);
    }

    private String renderTpl(Template template, Object model) {
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException e) {
            LOGGER.error("Template {0} not found", template.getName());
        } catch (TemplateException e) {
            LOGGER.error("Render template error", e);
        }
        return StringUtils.EMPTY;
    }


    public Template getTemplate(String tpl) {
        try {
            return configuration.getTemplate(tpl);
        } catch (IOException e) {
            LOGGER.error("Template {0} not found", tpl);
            return null;
        }
    }

    private Template createIfAbsent(String content) {
        String key = Hashing.md5().hashBytes(content.getBytes()).toString();
        if (stringTemplateLoader.findTemplateSource(key) == null) {
            stringTemplateLoader.putTemplate(key, content);
        }
        return getTemplate(key);
    }
}
