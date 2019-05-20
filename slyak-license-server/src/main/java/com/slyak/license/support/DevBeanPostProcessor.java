package com.slyak.license.support;

import com.google.common.collect.Lists;
import com.slyak.core.config.livereload.LiveReloadServer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.List;

/**
 * @author stormning
 * @since 1.0
 */

@Component
@Slf4j
public class DevBeanPostProcessor implements BeanPostProcessor, ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

    private List<String> watchDirectory = Lists.newArrayList();

    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ThymeleafProperties) {
            String codeLocation = DevHelper.getSourceCodeLocation();
            String templateDir = codeLocation + "/src/main/resources/templates/";
            ThymeleafProperties props = (ThymeleafProperties) bean;
            props.setPrefix(templateDir);
            watchIt(templateDir);
        } else if (bean instanceof ResourceProperties) {
            String codeLocation = DevHelper.getSourceCodeLocation();
            ResourceProperties props = (ResourceProperties) bean;
            String[] exist = props.getStaticLocations();
            List<String> devLocs = Lists.newArrayList();
            for (String loc : exist) {
                if (loc.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
                    String dev_loc = loc.replace(ResourceUtils.CLASSPATH_URL_PREFIX, codeLocation + "/src/main/resources");
                    devLocs.add(dev_loc);
                    watchIt(dev_loc);
                } else {
                    devLocs.add(loc);
                }
            }
            props.setStaticLocations(devLocs.toArray(new String[0]));
            props.setCachePeriod(0);
        }
        return bean;
    }

    private void watchIt(String dir) {
        watchDirectory.add(dir.startsWith("file:") ? dir.replace("file:", "") : dir);
    }

    @Override
    @SneakyThrows
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if ("test".equals(environment.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME))) {
            LiveReloadServer liveReloadServer = applicationContext.getBean(LiveReloadServer.class);
            if (liveReloadServer != null && watchDirectory.size() > 0) {
                FileAlterationMonitor monitor = new FileAlterationMonitor(600);
                for (String dir : watchDirectory) {
                    FileAlterationObserver observer = new FileAlterationObserver(dir);
                    observer.addListener(new FileAlterationListenerAdaptor() {
                        @Override
                        public void onFileCreate(File file) {
                            log.info("onFileCreate {}", file);
                            liveReloadServer.triggerReload();
                        }

                        @Override
                        public void onFileChange(File file) {
                            log.info("onFileChange {}", file);
                            liveReloadServer.triggerReload();
                        }

                        @Override
                        public void onFileDelete(File file) {
                            log.info("onFileDelete {}", file);
                            liveReloadServer.triggerReload();
                        }
                    });
                    monitor.addObserver(observer);
                }
                monitor.start();
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
