package com.slyak.core.util;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;

import java.util.List;

/**
 * notice: only use in spring init lifecycle (run main class)
 */
public class DevHelper {

    @SneakyThrows
    public static List<String> getSourceCodeLocations() {
        List<String> locations = Lists.newArrayList();
        String mainCodeLocation = getSourceCodeLocation(Class.forName(getMainClass()));
        if (mainCodeLocation != null) {
            System.out.println("main code location is : " + mainCodeLocation);
            locations.add(mainCodeLocation);
        }
        String coreCodeLocation = getSourceCodeLocation(DevHelper.class);
        if (coreCodeLocation != null) {
            System.out.println("core code location is : " + coreCodeLocation);
            if (!locations.contains(coreCodeLocation)) {
                locations.add(coreCodeLocation);
            }
        }
        return locations;
    }

    @SneakyThrows
    private static String getSourceCodeLocation(Class classToCheck) {
        String classesPath = classToCheck.getProtectionDomain().getCodeSource().getLocation().toString();
        int index = classesPath.indexOf("/target/classes");
        if (index > 0) {
            return classesPath.substring(0, index);
        }
        return null;
    }

    @SneakyThrows
    public static String getSourceCodeLocation() {
        return getSourceCodeLocations().get(0);
    }


    private static String getMainClass() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (int i = trace.length - 1; i >= 0; i--) {
            StackTraceElement ste = trace[i];
            if (ste.getMethodName().equals("main")) {
                return ste.getClassName();
            }
        }
        throw new NoSuchMethodError("main class not found!");
    }

    public static boolean isDev() {
        return getSourceCodeLocation() != null;
    }
}
