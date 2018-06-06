package com.slyak.web.support.freemarker;

import com.google.common.base.Charsets;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

/**
 * .
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 2015/8/9.
 */
public class FmUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FmUtils.class);

    static BeansWrapper wrapper;

    public static FreemarkerTemplateRender getTemplateRender() {
        return FmContext.getTemplateRender();
    }

    @SuppressWarnings("unchecked")
    public static <T> T unwrap(Object obj) throws TemplateModelException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof TemplateModel) {
            return (T) wrapper.unwrap((TemplateModel) obj);
        }
        return (T) obj;
    }

    public static TemplateModel wrap(Object obj) throws TemplateModelException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof TemplateModel) {
            return (TemplateModel) obj;
        }
        return wrapper.wrap(obj);
    }

    public static TemplateModel wrap(String v) {
        if (v == null) return null;
        return new SimpleScalar(v);
    }

    public static TemplateModel wrap(Boolean v) {
        if (v == null) return null;
        return v ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
    }

    public static TemplateModel wrap(Number v) {
        if (v == null) return null;
        return new SimpleNumber(v);
    }

    public static TemplateModel wrap(Date v) {
        if (v == null) return null;
        return new SimpleDate(v, TemplateDateModel.DATETIME);
    }

    public static <T> T getVar(Environment env, String key) throws TemplateModelException {
        return unwrap(env.getVariable(key));
    }

    public static <T> T getVar(Environment env, String key, T def) throws TemplateModelException {
        T value = getVar(env, key);
        return value == null ? def : value;
    }

    public static void setVar(Environment env, String key, Object value) throws TemplateModelException {
        env.setVariable(key, wrap(value));
    }

    public static void delVar(Environment env, String... keys) throws TemplateModelException {
        for (String key : keys) {
            env.getCurrentNamespace().remove(key);
        }
    }

    public static <T> T getParam(Map params, String key) throws TemplateModelException {
        TemplateModel model = (TemplateModel) params.get(key);
        if (model == null) {
            return null;
        }
        return unwrap(model);
    }

    public static String getString(Map params, String key) throws TemplateModelException {
        TemplateModel model = (TemplateModel) params.get(key);
        if (model == null) {
            return null;
        }
        return model.toString();
    }

    public static Integer getInteger(Map params, String key) throws TemplateModelException {
        TemplateModel model = (TemplateModel) params.get(key);
        if (model == null) {
            return null;
        }
        if ((model instanceof TemplateNumberModel)) {
            return ((TemplateNumberModel) model).getAsNumber().intValue();
        } else {
            try {
                return Integer.valueOf(model.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public static Boolean getBoolean(Map params, String key) throws TemplateModelException {
        TemplateModel model = (TemplateModel) params.get(key);
        if (model == null) {
            return null;
        }
        if (model instanceof TemplateBooleanModel) {
            return ((TemplateBooleanModel) model).getAsBoolean();
        } else if ((model instanceof TemplateNumberModel)) {
            return ((TemplateNumberModel) model).getAsNumber().intValue() != 0;
        } else {
            return BooleanUtils.toBooleanObject(model.toString());
        }
    }


    public static boolean getBoolean(Map params, String key, boolean def) throws TemplateModelException {
        Boolean value = getBoolean(params, key);
        return value == null ? def : value;
    }

    public static String getString(Map params, String key, String def) throws TemplateModelException {
        String value = getString(params, key);
        return StringUtils.isEmpty(value) ? def : value;
    }

    public static void includeTpl(String tplName, Map<String, Object> params, Environment env) throws IOException, TemplateException {
        if (!CollectionUtils.isEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                env.setVariable(entry.getKey(), wrap(entry.getValue()));
            }
        }
        env.include(tplName, Charsets.UTF_8.displayName(), true);
        if (!CollectionUtils.isEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                delVar(env, entry.getKey());
            }
        }
    }

    public static String renderBody(TemplateDirectiveBody body) throws IOException, TemplateException {
        if (body == null) {
            return null;
        }
        StringWriter sw = new StringWriter(64);
        body.render(sw);
        return sw.toString();
    }
}
