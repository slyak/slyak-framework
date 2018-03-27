package com.slyak.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * .
 *
 * @author stormning on 2015/8/2.
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static List<String> findGroupsIfMatch(String regex, String input) {
        List<String> groups = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            int c = matcher.groupCount();
            for (int i = 1; i <= c; i++) {
                String val = trimToNull(matcher.group(i));
                if (val != null) {
                    groups.add(val);
                }
            }
        }
        return groups;
    }

}
