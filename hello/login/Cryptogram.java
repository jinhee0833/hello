package com.helloants.mm.helloants1.login;

/**
 * Created by park on 2016-01-21.
 */
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by park on 2016-01-14.
 */
public enum Cryptogram {
    INSTANCE;

    public static String Decrypt(String text) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] keyBytes = new byte[16];
            byte[] b = "helloants".getBytes("UTF-8");
            int len = b.length;
            if (len > keyBytes.length) len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] results = cipher.doFinal(Base64.decode(text, 0));

            return new String(results, "UTF-8");
        } catch (IllegalBlockSizeException e) {
            return "";
        }
    }

    public static String Encrypt(String text) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] keyBytes = new byte[16];
            byte[] b = "helloants".getBytes("UTF-8");
            int len = b.length;
            if (len > keyBytes.length) len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] results = cipher.doFinal(text.getBytes("UTF-8"));

            return Base64.encodeToString(results, 0);
        } catch (IllegalBlockSizeException e) {
            return "";
        }
    }
}