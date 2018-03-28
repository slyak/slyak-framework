package com.slyak.web.support.freemarker;

import com.google.common.collect.Maps;
import com.slyak.core.util.IOUtils;
import com.slyak.core.util.StringUtils;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;


/**
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 2015/8/2.
 */
public class FreeMarkerConfigurer extends org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer implements ApplicationContextAware {

    private static final String FTLROOT_REGEX = "\\[#--\\s{1,}@ftlroot\\s{1,}\"(.*)\"\\s{1,}--\\]";
    private static final String IMPORT_REGEX = "\\[#import\\s{1,}['\"](.*)['\"]\\s{1,}as\\s{1,}(.*)\\]";
    private static final String VARIABLE_REGEX = "\\[#--\\s{1,}@ftlvariable\\s{1,}name=\"(.*)\"\\s{1,}type=\"(.*)\"\\s{1,}--]";

    private String implicitFile = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "/**/freemarker_implicit.ftl";

    private Map<String, String> imports = Maps.newHashMap();
    private Map<String, String> variables = Maps.newHashMap();

    private BeansWrapper beansWrapper = new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build();

    private ApplicationContext wac;

    @Override
    protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
        super.postProcessTemplateLoaders(templateLoaders);
        try {
            ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(getResourceLoader());
            Resource[] implicitFiles = resourcePatternResolver.getResources(implicitFile);

            for (Resource resource : implicitFiles) {
                if (resource.exists()) {
                    List<String> lines = IOUtils.readLines(resource.getInputStream(), Charset.forName("UTF-8"));
                    for (String line : lines) {
                        //find root
//                    List<String> rootVars = StringUtils.findGroupsIfMatch(FTLROOT_REGEX, line);
//                    if (CollectionUtils.isNotEmpty(rootVars)) {
//                        ftlroot = rootVars.get(0);
//                        continue;
//                    }

                        List<String> imps = StringUtils.findGroupsIfMatch(IMPORT_REGEX, line);
                        if (CollectionUtils.isNotEmpty(imps)) {
                            imports.put(imps.get(1), imps.get(0));
                            continue;
                        }

                        List<String> vals = StringUtils.findGroupsIfMatch(VARIABLE_REGEX, line);
                        if (CollectionUtils.isNotEmpty(vals)) {
                            variables.put(vals.get(0), vals.get(1));
                        }
                    }
                }
            }

            //classpath
            templateLoaders.add(
                    new ResourcePatternTemplateLoader(
                            getResourceLoader(),
                            ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                                    + ClassUtils.classPackageAsResourcePath(this.getClass())
                                    + "/**/*.ftl"
                    )
            );
        } catch (Exception e) {
            //ignore?
        }
    }

    @Override
    protected void postProcessConfiguration(Configuration config) throws IOException, TemplateException {
        ConfigurableObjectWrapper wrapper = new ConfigurableObjectWrapper();
        config.setObjectWrapper(wrapper);
        config.setAPIBuiltinEnabled(true);
        FmUtils.wrapper = wrapper;
        if (!imports.isEmpty()) {
            for (Map.Entry<String, String> iptEntry : imports.entrySet()) {
                config.addAutoImport(iptEntry.getKey(), iptEntry.getValue());
            }
        }
        if (!variables.isEmpty()) {
            for (Map.Entry<String, String> valEntry : variables.entrySet()) {
                String vname = valEntry.getKey();
                String val = valEntry.getValue();
                if (vname.equalsIgnoreCase("null")) {
                    config.setSharedVariable(vname, TemplateModel.NOTHING);
                    continue;
                }
                if (config.getSharedVariableNames().contains(vname) || val.contains("freemarker.ext.servlet.")) {
                    continue;
                }
                try {
                    Class<?> aClass = ClassUtils.forName(val, ClassUtils.getDefaultClassLoader());
                    Ignore annotation = AnnotationUtils.findAnnotation(aClass, Ignore.class);
                    if (annotation == null) {
                        if (TemplateModel.class.isAssignableFrom(aClass) || aClass.getAnnotation(Fm.class) != null) {
                            Object instantiate = BeanUtils.instantiate(aClass);
                            AutowireCapableBeanFactory beanFactory = wac.getAutowireCapableBeanFactory();
                            beanFactory.autowireBean(instantiate);
                            beanFactory.initializeBean(instantiate, "freemarker" + aClass.getName());
                            config.setSharedVariable(vname, instantiate);
                        } else if (aClass.isInterface()) {
                            config.setSharedVariable(vname, wac.getBean(aClass));
                        } else {
                            config.setSharedVariable(vname, beansWrapper.getStaticModels().get(val));
                        }
                    }

                } catch (Exception e) {
                    //ignore
                }
            }
        }
        FmContext.cfg = config;
    }

    public void setImplicitFile(String implicitFile) {
        this.implicitFile = implicitFile;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.wac = applicationContext;
    }
}