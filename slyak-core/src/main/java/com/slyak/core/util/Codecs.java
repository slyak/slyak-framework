package com.slyak.core.util;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;

public class Codecs {
    private static HashFunction HF = Hashing.murmur3_128(31);
    private static BaseEncoding BE = BaseEncoding.base64Url().omitPadding();


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

    public static byte[] hash(byte[] bytes) {
        return HF.hashBytes(bytes).asBytes();
    }

    public static String base64(byte[] bytes) {
        return BE.encode(bytes);
    }

    public static String hash(String str) {
        return base64(hash(toBytes(str)));
    }

    @SneakyThrows
    public static byte[] toBytes(String s) {
        if (s == null) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return s.getBytes(Charsets.UTF_8);
    }


    public static String md5(byte[] input) {
        return Hashing.md5().hashBytes(input).toString();
    }

    public static String md5(String input) {
        return md5(input.getBytes(StandardCharsets.UTF_8));
    }
}
