package com.imooc.miaosha.redis;

/**
 * Created by 莫文龙 on 2018/6/24.
 */
public class AccessKey extends BasePrefix {

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey access = new AccessKey(5, "access");

}
