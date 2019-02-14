package com.slyak.web.support.socket;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 * .
 *
 * @author stormning 2018/10/21
 * @since 1.3.0
 */
public class CleanImg {

    public static void main(String[] args) {
        String base = "/Users/stormning/Downloads/1019_1022";

        Set<String> dirs = Sets.newHashSet("20181019", "20181020", "20181021", "20181022");

        int invalid = 0;
        int total = 0;
        for (String dir : dirs) {
            Iterator<File> iterator = FileUtils.iterateFiles(new File(base + "/" + dir), null, false);
            while (iterator.hasNext()) {
                total++;
                File next = iterator.next();
                String label = FilenameUtils.getBaseName(next.getName()).split("_")[0];
                if (StringUtils.isEmpty(label) || label.length() != 4) {
                    System.out.println(label);
                    invalid++;
                    FileUtils.deleteQuietly(next);
                }
            }
        }
        System.out.println("total : " + total + " invalid : " + invalid);

    }
}
