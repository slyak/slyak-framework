package com.slyak.web.support.freemarker;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.Properties;

/**
 * .
 *
 * @author stormning 2017/12/20
 * @since 1.3.0
 */
@Configuration
@ConditionalOnClass({freemarker.template.Configuration.class, FreeMarkerConfigurationFactory.class})
@AutoConfigureBefore(FreeMarkerAutoConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(FreeMarkerProperties.class)
public class FreemarkerConfig {

    private FreeMarkerProperties properties;

    public FreemarkerConfig(FreeMarkerProperties properties) {
        this.properties = properties;
    }

    protected void applyProperties(FreeMarkerConfigurationFactory factory) {
        factory.setTemplateLoaderPaths(this.properties.getTemplateLoaderPath());
        factory.setPreferFileSystemAccess(this.properties.isPreferFileSystemAccess());
        factory.setDefaultEncoding(this.properties.getCharsetName());
        Properties settings = new Properties();
        settings.putAll(this.properties.getSettings());
        factory.setFreemarkerSettings(settings);
    }

    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        applyProperties(configurer);
        return configurer;
    }

    @Bean(name = "freeMarkerViewResolver")
    @ConditionalOnProperty(name = "spring.freemarker.enabled", matchIfMissing = true)
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setViewClass(SmartFreemarkerView.class);
        this.properties.applyToViewResolver(resolver);
        return resolver;
    }
}
