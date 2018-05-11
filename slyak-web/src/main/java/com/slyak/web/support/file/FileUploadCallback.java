package com.slyak.web.support.file;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * .
 *
 * @author stormning 2018/5/8
 * @since 1.3.0
 */
public interface FileUploadCallback<T, R, ID extends Serializable> {
    T saveMFile(MultipartFile mfile);

    boolean deleteFile(ID fileId);

    ResponseEntity<R> createResponseEntity(List<T> savedFiles);
}
