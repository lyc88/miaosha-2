package com.imooc.miaosha.rabbitmq;

import java.lang.String;

import com.imooc.miaosha.dao.MiaoshaOrderDao;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaOrderService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 莫文龙 on 2018/6/22.
 */

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MiaoshaService miaoshaService;

    @Autowired
    private MiaoshaOrderDao miaoshaOrderDao;


    //开始队列消费消息
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {

        log.info("receive message :" + MQConfig.QUEUE + " :" + message);
        MiaoshaMessage mm = RedisService.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        //查看是不是已经秒杀到了
        MiaoshaOrder order = miaoshaOrderDao.getByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            log.info("重复秒杀");
            return;
        }
        //减库下单到数据库
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        if (orderInfo == null) {
            log.info("下单失败");
            return;
        }
        log.info("下单成功，订单号号:" + orderInfo.getId());
    }

}
