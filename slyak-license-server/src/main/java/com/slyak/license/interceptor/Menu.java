package com.slyak.license.interceptor;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Menu implements Serializable, Cloneable {
    private List<Menu> children;
    private String title;
    private String url;
    private boolean checked;
    private String[] patterns;
    private String icon;

    @Override
    public Menu clone() {
        try {
            return (Menu) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
