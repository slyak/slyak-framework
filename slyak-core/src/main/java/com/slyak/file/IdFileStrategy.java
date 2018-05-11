package com.slyak.file;

import java.io.File;
import java.io.Serializable;

/**
 * .
 *
 * @author stormning 2018/5/8
 * @since 1.3.0
 */
public interface IdFileStrategy<ID extends Serializable> {
    File getFile(ID fileId);

    ID generateId(ID filename);
}
