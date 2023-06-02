package com.study.board;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Encoder {

    /**
     * 파라미터로 들어온 String 값을 암호화하여 리턴한다.
     *
     * @param string
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String encrypt(String string) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(string.getBytes());
        return bytesToHex(md.digest());
    }

    //    TODO 이해 필요
    private String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }
}
