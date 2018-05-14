package com.slyak.web.support.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * .
 *
 * @author stormning 2018/5/13
 * @since 1.3.0
 */
@Configuration
public class RequestParamBindConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(bindArgumentResolver());
    }

    @Bean
    public RequestParamBindArgumentResolver bindArgumentResolver(){
        return new RequestParamBindArgumentResolver();
    }
}
