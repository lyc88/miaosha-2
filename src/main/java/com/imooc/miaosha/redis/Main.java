package com.imooc.miaosha.redis;

import java.net.ServerSocket;

/**
 * Created by 莫文龙 on 2018/6/21.
 */
public class Main {

    public static void main(String[] args) {

    }

    public static boolean isEqu() {
        String str1 = null;
        while (true) {
            //第一次获取String保存在str1中，str1不再为null
            if (str1 == null) {
                str1 = recv();
            } else {
                //第二次的String与第一次的比较
                return str1 == recv() ? true : false;
            }
        }
    }

    public static String recv() {
        //.........
        return "str";
    }
}

