package com.imooc.miaosha.redis;

/**
 * Created by 莫文龙 on 2018/6/12.
 */
public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(prefix);
    }

    @Override
    public int expireSeconds() {
        return super.expireSeconds();
    }

    @Override
    public String getPrefix() {
        return super.getPrefix();
    }
}
