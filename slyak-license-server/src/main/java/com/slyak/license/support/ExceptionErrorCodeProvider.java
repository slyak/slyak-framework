package com.slyak.license.support;

import org.springframework.core.ErrorCoded;

/**
 * .
 *
 * @author stormning on 2016/12/15.
 */
public interface ExceptionErrorCodeProvider extends ErrorCoded {
	boolean support(Exception e);
}
