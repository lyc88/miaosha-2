package com.imooc.miaosha.redis;

/**
 * Created by 莫文龙 on 2018/6/12.
 */
public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();

}
