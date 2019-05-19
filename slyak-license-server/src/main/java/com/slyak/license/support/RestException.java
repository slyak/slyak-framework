package com.slyak.license.support;

import org.springframework.core.ErrorCoded;

/**
 * RestFull api service exception.
 *
 * @author stormning on 2016/10/20.
 */
public abstract class RestException extends RuntimeException implements ErrorCoded {

	public static final String DEFAULT_ERROR_CODE = "serverEx";

	private Object[] args;

	public RestException(Object... args) {
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

	@Override
	public String getMessage() {
		String customMessage = getCustomMessage();
		return customMessage == null ? super.getMessage() : customMessage;
	}

	protected String getCustomMessage() {
		return null;
	}
}
