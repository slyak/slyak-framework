package com.slyak.web.support.freemarker.bootstrap;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * .
 *
 * @author stormning 2018/5/9
 * @since 1.3.0
 */
public abstract class InitialPreviewConfigConverter<S> implements Converter<List<S>, List<Fileinput>> {
    @Override
    public List<Fileinput> convert(List<S> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        return source.stream().map(this::convertOne).collect(Collectors.toList());
    }

    protected abstract Fileinput convertOne(S s);
}
