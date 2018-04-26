package com.slyak.web.support.socket;

import lombok.Data;

import java.io.Serializable;
import java.security.Principal;

/**
 * .
 *
 * @author stormning 2018/4/25
 * @since 1.3.0
 */
@Data
public class Session implements Serializable {
    private String id;
    private Principal user;
}
