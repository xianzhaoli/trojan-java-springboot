package com.kosoft.trojanjava.utils;

import cn.hutool.core.util.HexUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encipher {

    public static String jdkSHA224(String src) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-224");
            md.update(src.getBytes());
            byte[] bytes = md.digest();
            return HexUtil.encodeHexStr(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(jdkSHA224("cfd174e6-82ec-4724-8773-4f3dfa711017"));
    }

}
