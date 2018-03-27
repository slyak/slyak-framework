package com.slyak.web.support.freemarker;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * .
 *
 * @author stormning 2017/12/26
 * @since 1.3.0
 */
@Builder
@ToString
@Getter
public class Pagination {
    //start from 0
    private int start;
    //start from 0
    private int end;
    private boolean hasNext;
    private boolean hasPrevious;
}
