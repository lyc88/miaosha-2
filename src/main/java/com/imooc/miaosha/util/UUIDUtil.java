package com.imooc.miaosha.util;

import java.util.UUID;

/**
 * Created by 莫文龙 on 2018/6/14.
 */
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
