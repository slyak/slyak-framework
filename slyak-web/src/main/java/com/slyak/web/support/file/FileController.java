package com.slyak.web.support.file;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * .
 *
 * @author stormning 2018/5/8
 * @since 1.3.0
 */
public abstract class FileController<T, R, ID extends Serializable> {

    private final FileUploadCallback<T, R, ID> callback;

    public FileController(FileUploadCallback<T, R, ID> callback) {
        this.callback = callback;
    }

    @PostMapping("/upload")
    @SneakyThrows
    public ResponseEntity<R> handleFileUpload(MultipartRequest request) {
        MultiValueMap<String, MultipartFile> fileMap = request.getMultiFileMap();
        Collection<List<MultipartFile>> values = fileMap.values();
        List<T> savedFiles = Lists.newArrayList();
        for (List<MultipartFile> files : values) {
            for (MultipartFile mfile : files) {
                savedFiles.add(callback.saveMFile(mfile));
            }
        }
        return callback.createResponseEntity(savedFiles);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public boolean handleFileDelete(ID id) {
        try {
            callback.deleteFile(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
