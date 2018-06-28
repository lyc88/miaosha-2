package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.OrderDao;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by 莫文龙 on 2018/6/19.
 */

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    MiaoshaOrderService miaoshaOrderService;

    public OrderInfo getMiaoshaOrderByUserIdGoodsId(Long userIdd, long goodsId) {
        return orderDao.getMiaoshaOrderByUserIdGoodsId(userIdd,goodsId);
    }


    //事务是原子操作
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        //order_info  miaosha_order都需要插进去
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setUserId(user.getId());
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);

        orderDao.insert(orderInfo);

        //向秒杀表插入
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        //插入成功后mybait会将订单号插进到OrderInfo中
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());

        miaoshaOrderService.insert(miaoshaOrder);

        return orderInfo;

    }
}
