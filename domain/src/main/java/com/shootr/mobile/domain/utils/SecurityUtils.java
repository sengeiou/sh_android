package com.shootr.mobile.domain.utils;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static String encodePassword(String password) {
        return encodeMd5(encodeSha1(password)).substring(0, 20);
    }

    public static String encodeMd5(String text) {
        return applyHash(text, "md5");
    }

    public static String encodeSha1(String text) {
        return applyHash(text, "SHA-1");
    }

    private static String applyHash(String text, String algorithm) {
        // MD5
        // SHA-1
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance(algorithm);
            byte[] array = md.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return null;
        }

    }
}
