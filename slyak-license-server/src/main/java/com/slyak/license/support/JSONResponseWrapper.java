package com.slyak.license.support;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.InputStream;

/**
 * @author stormning on 2016/12/13.
 */
@ControllerAdvice
public class JSONResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        ResultMessage.ResultMessageBuilder builder = ResultMessage.builder().success(true).code("0").message("success");
        if (body instanceof MappingJacksonValue) {
            MappingJacksonValue jacksonValue = (MappingJacksonValue) body;
            jacksonValue.setValue(builder.data(jacksonValue.getValue()).build());
            return jacksonValue;
        } else if (body instanceof ResultMessage || body instanceof Resource || body instanceof InputStream) {
            return body;
        } else {
            return builder.data(body).build();
        }
    }

}
