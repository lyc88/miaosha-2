package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoshaOrderDao;
import com.imooc.miaosha.domain.MiaoshaOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 莫文龙 on 2018/6/19.
 */
@Service
public class MiaoshaOrderService {

    @Autowired
    private MiaoshaOrderDao miaoshaOrderDao;


    public void insert(MiaoshaOrder miaoshaOrder) {
        miaoshaOrderDao.insert(miaoshaOrder);
    }

    public MiaoshaOrder getByUserIdGoodsId(Long userId, long goodsId) {
        return miaoshaOrderDao.getByUserIdGoodsId(userId,goodsId);
    }
}
