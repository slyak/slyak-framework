package com.slyak.core.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class Codecs {

    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (byte b : bArray) {
            sTemp = Integer.toHexString(0xFF & b);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static String md5(byte[] input) {
        return Hashing.md5().hashBytes(input).toString();
    }

    public static String md5(String input) {
        return md5(input.getBytes(StandardCharsets.UTF_8));
    }
}
