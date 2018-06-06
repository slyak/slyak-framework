package com.slyak.core.ssh2;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2018/5/24
 * @since 1.3.0
 */
public class StdEvent implements Serializable {

    private StdEventType type;

    private String line;

    private int number;

    private Map<String, Object> props = Maps.newHashMap();

    public StdEvent(StdEventType type, String line, int number) {
        this.type = type;
        this.line = line;
        this.number = number;
    }

    public StdEventType getType() {
        return type;
    }

    public String getLine() {
        return line;
    }

    public int getNumber() {
        return number;
    }

    public <T> void setProperty(String name, T value) {
        props.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name) {
        return (T) props.get(name);
    }
}