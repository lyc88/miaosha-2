package com.imooc.miaosha.redis;

/**
 * Created by 莫文龙 on 2018/6/12.
 */
public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;

    private String prefix;

    public BasePrefix(String prefix) {
        this.expireSeconds = 0;
        this.prefix = prefix;
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public int expireSeconds() {
        //0表示永不过期
        return expireSeconds;
    }

    public String getPrefix() {
        String simpleName = getClass().getSimpleName();
        return simpleName + ":" + prefix;
    }
}
