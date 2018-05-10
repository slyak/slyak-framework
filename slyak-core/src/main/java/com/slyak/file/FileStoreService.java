package com.slyak.file;

import java.io.File;
import java.io.InputStream;

/**
 * .
 *
 * @author stormning 2018/5/8
 * @since 1.3.0
 */
public interface FileStoreService {

    String store(InputStream is, String filename);

    File lookup(String id);

    void removeFile(String id);
}
