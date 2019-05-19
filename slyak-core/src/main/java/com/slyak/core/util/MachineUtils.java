package com.slyak.core.util;


import com.google.common.collect.Sets;
import lombok.SneakyThrows;

import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

public class MachineUtils {

    @SneakyThrows
    public static String getMac() {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            byte[] mac = interfaces.nextElement().getHardwareAddress();
            if (mac == null) {
                continue;
            }
            return Codecs.bytesToHexString(mac);
        }
        return null;
    }

    public static String getMachineCode() {
        Set<String> result = Sets.newHashSet();
        String mac = getMac();
        result.add(mac);
        Properties props = System.getProperties();
        String javaVersion = props.getProperty("java.version");
        result.add(javaVersion);
        String javaVMVersion = props.getProperty("java.vm.version");
        result.add(javaVMVersion);
        String osVersion = props.getProperty("os.version");
        result.add(osVersion);
        return Codecs.md5(result.toString());
    }
}
