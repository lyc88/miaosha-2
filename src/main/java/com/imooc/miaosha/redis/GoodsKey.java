package com.imooc.miaosha.redis;

/**
 * Created by 莫文龙 on 2018/6/21.
 */
public class GoodsKey extends BasePrefix {

    public GoodsKey(String prefix) {
        super(prefix);
    }

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //有60s的过期时间
    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60,"gd");
    public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0, "gs");

}
