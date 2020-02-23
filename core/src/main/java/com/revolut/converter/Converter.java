package com.revolut.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

public interface Converter<I, R> {

    R convert(I input);

    default List<R> convertCollection(final Collection<I> entities) {
        return CollectionUtils.isNotEmpty(entities) ?
                entities.stream()
                        .map(this::convert)
                        .collect(Collectors.toList())
                : Collections.emptyList();
    }

}
