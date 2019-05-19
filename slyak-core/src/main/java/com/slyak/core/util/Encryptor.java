package com.slyak.core.util;

import lombok.SneakyThrows;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

public class Encryptor {

    private BufferedBlockCipher cipher;
    private KeyParameter key;

    public Encryptor(byte[] key) {
        this.cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new BlowfishEngine()));
        this.key = new KeyParameter(key);
    }

    public Encryptor(String key) {
        this(key.getBytes());
    }

    @SneakyThrows
    private byte[] callCipher(byte[] data) {
        int size =
                cipher.getOutputSize(data.length);
        byte[] result = new byte[size];
        int olen = cipher.processBytes(data, 0,
                data.length, result, 0);
        olen += cipher.doFinal(result, olen);

        if (olen < size) {
            byte[] tmp = new byte[olen];
            System.arraycopy(
                    result, 0, tmp, 0, olen);
            result = tmp;
        }

        return result;
    }

    @SneakyThrows
    public synchronized byte[] encrypt(byte[] data) {
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        cipher.init(true, key);
        return callCipher(data);
    }

    @SneakyThrows
    public byte[] encryptString(String data) {
        if (data == null || data.length() == 0) {
            return new byte[0];
        }

        return encrypt(data.getBytes());
    }

    @SneakyThrows
    public synchronized byte[] decrypt(byte[] data) {
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        cipher.init(false, key);
        return callCipher(data);
    }

    @SneakyThrows
    public String decryptString(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }

        return new String(decrypt(data));
    }
}