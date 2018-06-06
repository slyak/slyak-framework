package com.slyak.core.concurrent;

/**
 * .
 *
 * @author stormning 2018/4/4
 * @since 1.3.0
 */
public interface Competition<R> {
    R start(int index);
}
