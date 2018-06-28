package com.imooc.miaosha.redis;

/**
 * Created by 莫文龙 on 2018/6/14.
 */
public class MiaoshaUserKey extends BasePrefix{

    public static final int TOKEN_EXPIRE = 3600*24*2;

    public MiaoshaUserKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE,"tk");
    //对象缓存，永久有效的
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");
}
