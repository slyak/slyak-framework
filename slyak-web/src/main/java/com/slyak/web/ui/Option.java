package com.slyak.web.ui;

import lombok.Data;

import java.io.Serializable;

/**
 * .
 *
 * @author stormning 2018/5/14
 * @since 1.3.0
 */
@Data
public class Option implements Serializable {

    private String title;

    private String value;

    private boolean selected;
}
