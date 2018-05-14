package com.slyak.web.support.data;

import java.lang.annotation.*;

/**
 * .
 *
 * @author stormning 2018/5/12
 * @since 1.3.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParamBind {

    String value();
}