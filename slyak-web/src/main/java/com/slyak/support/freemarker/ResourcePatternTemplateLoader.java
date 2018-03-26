package com.slyak.support.freemarker;

import freemarker.cache.TemplateLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * .
 *
 * @author stormning 2017/12/26
 * @since 1.3.0
 */
@Slf4j
public class ResourcePatternTemplateLoader implements TemplateLoader {

    private Resource[] resources;

    @SneakyThrows
    public ResourcePatternTemplateLoader(ResourceLoader resourceLoader, String locationPattern) {
        this.resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(locationPattern);
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        for (Resource resource : resources) {
            if (resource.exists() && resource.getURI().toString().contains(name)) {
                return resource;
            }
        }
        return null;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        Resource resource = (Resource) templateSource;
        try {
            return new InputStreamReader(resource.getInputStream(), encoding);
        } catch (IOException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Could not find FreeMarker template: " + resource);
            }
            throw ex;
        }
    }

    @Override
    public long getLastModified(Object templateSource) {
        Resource resource = (Resource) templateSource;
        try {
            return resource.lastModified();
        } catch (IOException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Could not obtain last-modified timestamp for FreeMarker template in " +
                        resource + ": " + ex);
            }
            return -1;
        }
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
    }

}
