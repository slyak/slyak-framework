package com.slyak.file;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

/**
 * .
 *
 * @author stormning 2018/5/8
 * @since 1.3.0
 */
public interface FileStoreService<ID extends Serializable> {

    String store(InputStream is, String filename);

    File lookup(String ID);

    void removeFile(ID id);
}
