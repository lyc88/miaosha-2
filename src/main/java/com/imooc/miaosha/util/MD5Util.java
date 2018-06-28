package com.imooc.miaosha.util;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by 莫文龙 on 2018/6/12.
 */
public class MD5Util {

    private static final String salt = "a1s2d3f4";

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    public static String inputPassToFormPass(String inputPass) {
        inputPass = "" + salt.charAt(1) + salt.charAt(3) + inputPass + salt.charAt(5) + salt.charAt(7);
        return md5(inputPass);
    }

    public static String formPassToDbPass(String formPass, String salt) {
        formPass = formPass + salt;
        return md5(formPass);
    }

    public static String inputPassToDbPass(String inputPass, String salt) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDbPass(formPass, salt);
        return dbPass;
    }
//    f96894b685dc0126cb5181a9a2659589

    public static void main(String[] args) {
        String s = inputPassToFormPass("123456");
        System.out.println(s);
    }
}
