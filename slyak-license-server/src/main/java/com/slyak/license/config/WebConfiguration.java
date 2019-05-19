package com.slyak.license.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slyak.license.interceptor.GlobalInteceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author by wolf on 2019-01-01.
 */
@EnableSpringDataWebSupport
@Configuration
@Slf4j
public class WebConfiguration extends WebMvcConfigurerAdapter {

	private ApplicationContext applicationContext;

	public WebConfiguration(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(handlerMethodArgumentResolver());
		super.addArgumentResolvers(argumentResolvers);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(globalInteceptor());
	}


	@Bean
	public GlobalInteceptor globalInteceptor(){
		return new GlobalInteceptor();
	}

	@Bean
	public PageableHandlerMethodArgumentResolver handlerMethodArgumentResolver() {
		return new PageableHandlerMethodArgumentResolver(sortResolver()) {
			@Override
			public Pageable resolveArgument(MethodParameter methodParameter,
                                            ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest,
                                            WebDataBinderFactory binderFactory) {
				Pageable pageable = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
				String limitString = webRequest.getParameter(getParameterNameToUse("limit", methodParameter));
				if (!org.springframework.util.StringUtils.hasText(limitString)) {
					return pageable;
				}
				int limit = Integer.parseInt(limitString);
				//SensorApiUtils.userLimitMap.put(Sec.getUserId(),limit);
				return new PageRequest(pageable.getPageNumber(), limit, pageable.getSort());
				//return new PageRequest(limit / pageable.getPageSize(), pageable.getPageSize(), pageable.getSort());
			}
		};
	}

	@Bean
	public SortArgumentResolver sortResolver() {
		return new SortHandlerMethodArgumentResolver();
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(
				StandardCharsets.UTF_8);
		//optimize AcceptCharset
		stringHttpMessageConverter.setWriteAcceptCharset(false);
		converters.add(stringHttpMessageConverter);
		converters.add(customJackson2HttpMessageConverter());
	}

	/**
	 * add jsonp`s callback
	 *
	 * @return bean.
	 */
	@Bean
	public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().applicationContext(applicationContext).build();
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper) {
			@Override
			protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
				String jsonpFunction =
						(object instanceof MappingJacksonValue ?
								((MappingJacksonValue) object).getJsonpFunction() :
								null);
				if (jsonpFunction != null) {
					generator.writeRaw(jsonpFunction + "(");
				}
			}

		};
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, false);
		jsonConverter.setObjectMapper(objectMapper);
		return jsonConverter;
	}


    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false);
    }
}
