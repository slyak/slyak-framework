package com.slyak.core.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class P {

    private P() {
    }

    public static Pageable fix(Pageable pageable) {
        int pageNumber = Math.max(pageable.getPageNumber() - 1, 0);
        return new PageRequest(pageNumber, pageable.getPageSize(), pageable.getSort());
    }
}
