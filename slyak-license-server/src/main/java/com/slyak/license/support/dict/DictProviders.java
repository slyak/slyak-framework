package com.slyak.license.support.dict;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.slyak.license.support.Titleable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class DictProviders implements InitializingBean {
    private ObjectProvider<List<DictProvider>> dictProviders;

    @Setter
    private String packageToScan = "com.lotimg";

    private Set<Class<? extends Enum>> enums = Sets.newHashSet();

    private Map<String, DictProvider> providerCache = Maps.newHashMap();

    @Autowired
    public DictProviders(ObjectProvider<List<DictProvider>> dictProviders) {
        this.dictProviders = dictProviders;
    }

    public DictProvider getDictProvider(String name) {
        String lowerName = name.toLowerCase();
        DictProvider provider = providerCache.get(lowerName);
        if (provider != null) {
            return provider;
        }
        for (Class<? extends Enum> anEnum : enums) {
            if (anEnum.getSimpleName().equalsIgnoreCase(lowerName)) {
                provider = new DictProvider() {
                    @Override
                    public String getName() {
                        return lowerName;
                    }

                    @Override
                    public List<DictItem> search(String key, int limit) {
                        List<DictItem> items = Lists.newArrayList();
                        for (Enum e : anEnum.getEnumConstants()) {
                            String title = ((Titleable) e).getTitle();
                            if (StringUtils.isBlank(key) || title.contains(key)) {
                                items.add(new DictItem(e.name(), title));
                            }
                        }
                        return items;
                    }

                    @Override
                    public List<DictItem> searchByCodes(List<String> itemCodes) {
                        List<DictItem> items = Lists.newArrayList();
                        for (Enum e : anEnum.getEnumConstants()) {
                            String title = ((Titleable) e).getTitle();
                            if (itemCodes.contains(e.name())) {
                                items.add(new DictItem(e.name(), title));
                            }
                        }
                        return items;
                    }

                };
            }
        }

        if (provider == null) {
            List<DictProvider> providers = dictProviders.getIfAvailable();
            if (providers != null) {
                for (DictProvider dictProvider : providers) {
                    if (dictProvider.getName().equalsIgnoreCase(lowerName)) {
                        provider = dictProvider;
                        break;
                    }
                }
            }
        }
        providerCache.put(lowerName, provider);
        return provider;
    }

    @SuppressWarnings("unchecked")
    private void findAllEnums() {
        ClassLoader loader = getClass().getClassLoader();
        MetadataReaderFactory mrf = new CachingMetadataReaderFactory(loader);
        try {
            Resource[] resources = scan(loader, packageToScan);
            for (Resource resource : resources) {
                Class<?> clazz = loadClass(loader, mrf, resource);
                if (clazz != null && clazz.isEnum() && Titleable.class.isAssignableFrom(clazz)) {
                    enums.add((Class<? extends Enum>) clazz);
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Resource[] scan(ClassLoader loader, String packageName) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                loader);
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(packageName) + "/**/*.class";
        return resolver.getResources(pattern);
    }

    private Class<?> loadClass(ClassLoader loader, MetadataReaderFactory readerFactory,
                               Resource resource) {
        try {
            MetadataReader reader = readerFactory.getMetadataReader(resource);
            return ClassUtils.forName(reader.getClassMetadata().getClassName(), loader);
        } catch (Throwable ex) {
            log.error("{}", ex);
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() {
        findAllEnums();
    }
}
