package com.slyak.license.support.dict;

import com.google.common.collect.Lists;
import com.slyak.spring.jpa.GenericJpaRepository;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class DbDictProvider<T, ID extends Serializable> implements DictProvider, ApplicationContextAware, InitializingBean {


    private ApplicationContext context;
    private GenericJpaRepository<T, ID> repository;
    private EntityInformation<T, ID> ei;

    private Class<T> javaType;

    private String[] ignorePaths;

    protected abstract ID toId(String key);

    @Override
    @SneakyThrows
    public List<DictItem> search(String key, int limit) {
        PageRequest pageRequest = new PageRequest(0, limit);
        List<T> objs;
        if (StringUtils.isNotBlank(key)) {
            T obj = BeanUtils.instantiate(javaType);
            org.apache.commons.beanutils.BeanUtils.setProperty(obj, getSearchField(), key);


            Example<T> example = Example.of(
                    obj,
                    ExampleMatcher
                            .matching()
                            .withMatcher(getSearchField(), ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                            .withIgnorePaths(ignorePaths)
            );
            objs = repository.findAll(example, pageRequest).getContent();
        } else {
            objs = repository.findAll();
        }
        List<DictItem> items = Lists.newArrayList();
        for (T obj : objs) {
            items.add(new DictItem(ObjectUtils.getDisplayString(ei.getId(obj)), getDisplayValue(obj)));
        }
        return items;
    }

    protected abstract String getSearchField();

    @Override
    public List<DictItem> searchByCodes(List<String> itemCodes) {
        if (CollectionUtils.isEmpty(itemCodes)) {
            return Collections.emptyList();
        }
        Map<ID, T> objMap = repository.mget(itemCodes.stream().map(this::toId).collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(objMap)) {
            return Collections.emptyList();
        }
        List<DictItem> result = Lists.newArrayList();
        for (Map.Entry<ID, T> oe : objMap.entrySet()) {
            result.add(new DictItem(ObjectUtils.getDisplayString(oe.getKey()), getDisplayValue(oe.getValue())));
        }
        return result;
    }

    protected abstract String getDisplayValue(T value);

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }


    @Override
    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void afterPropertiesSet() {
        Class<T> aClass = (Class<T>) (GenericTypeResolver.resolveTypeArguments(this.getClass(), DbDictProvider.class)[0]);
        Repositories repos = new Repositories(context);
        this.repository = (GenericJpaRepository<T, ID>) repos.getRepositoryFor(aClass);
        this.ei = repos.getEntityInformationFor(aClass);
        this.javaType = ei.getJavaType();
        BeanInfo beanInfo = Introspector.getBeanInfo(javaType);
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        List<String> igps = Lists.newArrayList();
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getWriteMethod() != null) {
                String name = descriptor.getName();
                if (!name.equals(getSearchField())) {
                    igps.add(name);
                }
            }
        }
        this.ignorePaths = igps.toArray(new String[0]);
    }
}
