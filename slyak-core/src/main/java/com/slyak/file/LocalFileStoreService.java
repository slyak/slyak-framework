package com.slyak.file;

import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * .
 *
 * @author stormning 2018/5/8
 * @since 1.3.0
 */
public class LocalFileStoreService implements FileStoreService<String> {

    @Setter
    private IdFileStrategy<String> idPathStrategy = new DefaultIdFileStrategy();

    @Setter
    private String pathPrefix = "file:~/filestore";

    @Override
    @SneakyThrows
    public String store(InputStream is, String filename) {
        String fileId = idPathStrategy.generateId(filename);
        IOUtils.copy(is, new FileOutputStream(idPathStrategy.getFile(fileId)));
        return fileId;
    }

    @Override
    public File lookup(String id) {
        return idPathStrategy.getFile(id);
    }

    @Override
    @SneakyThrows
    public void removeFile(String id) {
        FileUtils.forceDelete(idPathStrategy.getFile(id));
    }

    public class DefaultIdFileStrategy implements IdFileStrategy<String> {
        private char separatorChar = '-';

        @Override
        @SneakyThrows
        public File getFile(String fileId) {
            String[] split = StringUtils.split(fileId, separatorChar);
            StringBuilder builder = new StringBuilder(pathPrefix);
            for (String path : split) {
                builder.append(File.separator).append(path);
            }
            File file = new File(builder.toString());
            FileUtils.forceMkdirParent(file);
            return file;
        }

        @Override
        public String generateId(String filename) {
            String prefix = DateFormatUtils.format(System.currentTimeMillis(), "yyyy" + separatorChar + "MM" + separatorChar + "dd");
            return prefix + separatorChar + RandomStringUtils.randomAlphanumeric(4);
        }

    }
}
