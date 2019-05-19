package com.slyak.core.util;

public class DevHelper {
    public static String getSourceCodeLocation() {
        String classesPath = DevHelper.class.getProtectionDomain().getCodeSource().getLocation().toString();
        int index = classesPath.indexOf("/target/classes");
        if (index > 0) {
            return classesPath.substring(0, index);
        }
        return null;
    }

    public static boolean isDev() {
        return getSourceCodeLocation() != null;
    }
}
