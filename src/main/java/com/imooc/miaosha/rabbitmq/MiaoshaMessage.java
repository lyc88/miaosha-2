package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.domain.MiaoshaUser;

/**
 * Created by 莫文龙 on 2018/6/23.
 */
public class MiaoshaMessage {

    private MiaoshaUser user;
    private long goodsId;

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
