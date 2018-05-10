package com.slyak.file;

import java.io.File;

/**
 * .
 *
 * @author stormning 2018/5/8
 * @since 1.3.0
 */
public interface IdFileStrategy {
    File getFile(String fileId);
    String generateId(String filename);
}
