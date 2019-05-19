package com.slyak.license.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * .
 *
 * @author stormning on 2016/11/10.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultMessage {
	boolean success = true;
	String code;
	String message;
	Object data;
}
