package com.slyak.web.support.data;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletRequest;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2018/5/14
 * @since 1.3.0
 */
public class RequestParamBindArgumentResolver implements HandlerMethodArgumentResolver, ApplicationContextAware {

    private Repositories repositories;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestParamBind.class) && repositories.hasRepositoryFor(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String paramName = paramName(parameter.getParameterAnnotation(RequestParamBind.class));
        String paramValue = webRequest.getParameter(paramName);
        if (StringUtils.isEmpty(paramValue)) {
            return resolveArgumentWhenParamValueIsEmpty(parameter, mavContainer, webRequest, binderFactory);
        } else {
            return resolveArgumentWhenParamValueIsNotEmpty(paramName, paramValue, parameter, mavContainer, webRequest, binderFactory);
        }
    }

    private Object resolveArgumentWhenParamValueIsNotEmpty(String resolvedName, Object paramValue, MethodParameter parameter,
                                                           ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object arg = paramValue;

        if (binderFactory != null) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, null, resolvedName);
            try {
                arg = binder.convertIfNecessary(arg, parameter.getParameterType(), parameter);
            } catch (ConversionNotSupportedException ex) {
                throw new MethodArgumentConversionNotSupportedException(arg, ex.getRequiredType(),
                        resolvedName, parameter, ex.getCause());
            } catch (TypeMismatchException ex) {
                throw new MethodArgumentTypeMismatchException(arg, ex.getRequiredType(),
                        resolvedName, parameter, ex.getCause());

            }

            if (arg != null) {
                //bind again
                //this action will directly modify entity loadedState , it will cause loadedState equals currentState, so update will have no effect
                //to avoid this, need a copy of complex property (simple property will has no problem), add bind param to it
                arg = deepCopy(arg);
                String name = Conventions.getVariableNameForParameter(parameter);
                bindToTarget(parameter, mavContainer, webRequest, binderFactory, name, arg);
            }
        }
        return arg;
    }

    private Object deepCopy(Object arg) {
        return SerializationUtils.deserialize(SerializationUtils.serialize(arg));
    }

    private String paramName(RequestParamBind ann) {
        String name = ann.value();
        Assert.notNull(name, "@RequestParamBind has no value");
        return name;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.repositories = new Repositories(context);
    }

    private Object resolveArgumentWhenParamValueIsEmpty(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String name = Conventions.getVariableNameForParameter(parameter);
        Object attribute = (mavContainer.containsAttribute(name) ? mavContainer.getModel().get(name) :
                createAttribute(name, parameter, binderFactory, webRequest));

        return bindToTarget(parameter, mavContainer, webRequest, binderFactory, name, attribute);
    }

    private Object bindToTarget(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory, String name, Object attribute) throws Exception {
        WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
        if (binder.getTarget() != null) {
            if (!mavContainer.isBindingDisabled(name)) {
                bindRequestParameters(binder, webRequest);
            }
            validateIfApplicable(binder, parameter);
            if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                throw new BindException(binder.getBindingResult());
            }
        }

        // Add resolved attribute and BindingResult at the end of the model
        Map<String, Object> bindingResultModel = binder.getBindingResult().getModel();
        mavContainer.removeAttributes(bindingResultModel);
        mavContainer.addAllAttributes(bindingResultModel);

        return binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
    }

    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation ann : annotations) {
            Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
            if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
                Object hints = (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann));
                Object[] validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[]{hints});
                binder.validate(validationHints);
                break;
            }
        }
    }

    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
        ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;
        servletBinder.bind(servletRequest);
    }

    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getMethod().getParameterTypes();
        boolean hasBindingResult = (paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1]));
        return !hasBindingResult;
    }

    protected final Object createAttribute(String attributeName, MethodParameter parameter,
                                           WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {
        String value = getRequestValueForAttribute(attributeName, request);
        if (value != null) {
            Object attribute = createAttributeFromRequestValue(
                    value, attributeName, parameter, binderFactory, request);
            if (attribute != null) {
                return attribute;
            }
        }
        return BeanUtils.instantiateClass(parameter.getParameterType());
    }

    protected Object createAttributeFromRequestValue(String sourceValue, String attributeName,
                                                     MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest request)
            throws Exception {

        DataBinder binder = binderFactory.createBinder(request, null, attributeName);
        ConversionService conversionService = binder.getConversionService();
        if (conversionService != null) {
            TypeDescriptor source = TypeDescriptor.valueOf(String.class);
            TypeDescriptor target = new TypeDescriptor(parameter);
            if (conversionService.canConvert(source, target)) {
                return binder.convertIfNecessary(sourceValue, parameter.getParameterType(), parameter);
            }
        }
        return null;
    }

    protected String getRequestValueForAttribute(String attributeName, NativeWebRequest request) {
        Map<String, String> variables = getUriTemplateVariables(request);
        String variableValue = variables.get(attributeName);
        if (StringUtils.hasText(variableValue)) {
            return variableValue;
        }
        String parameterValue = request.getParameter(attributeName);
        if (StringUtils.hasText(parameterValue)) {
            return parameterValue;
        }
        return null;
    }

    protected final Map<String, String> getUriTemplateVariables(NativeWebRequest request) {
        Map<String, String> variables = (Map<String, String>) request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        return (variables != null ? variables : Collections.<String, String>emptyMap());
    }

    public static void main(String[] args) {
        Long a = 1L;
        System.out.println(a.getClass().isPrimitive());
    }
}
