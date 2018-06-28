package com.imooc.miaosha.service;

import com.imooc.miaosha.controller.MiaoshaController;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by 莫文龙 on 2018/6/19.
 */
@Service
public class MiaoshaService {

    private static Logger log = LoggerFactory.getLogger(MiaoshaService.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaOrderService miaoshaOrderService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存，写订单
        boolean f = goodsService.reduceStock(goods);
        if (f) {
            //减仓库成功才生成订单
            return orderService.createOrder(user,goods);
        }else {
            //减库存失败，证明买完了
            setGoodsOver(goods.getId());
            return null;
        }
     }


    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = miaoshaOrderService.getByUserIdGoodsId(userId, goodsId);
        if (order != null) {
            //秒杀成功
            return order.getOrderId();
        }else {
            //不存在为false
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                //已经买完了，没有秒杀成功
                return -1;
            } else {
                //还没有买完，正在处理
                return 0;
            }
        }

    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
    }


    public void setGoodsOver(Long goodsId) {
        log.info("商品id为:" + goodsId + "已经被秒杀完毕!!!!!!!!!!!");
        redisService.set(MiaoshaKey.isGoodsOver,"" + goodsId,true);
    }

    /**
     *
     *  验证两个path是不是相等
     *
     */
    public boolean checkPath(Long userId, long goodsId, String path) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(goodsId) || StringUtils.isEmpty(path)) {
            return false;
        }
        String p = redisService.get(MiaoshaKey.getMiaoshaPath, "" + userId + "_" + goodsId, String.class);
        return path.equals(p);
    }

    public String createMiaoshaPath(Long usrId, long goodsId) {

        //生成加密的地址
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        //用redis保存加密的秒杀地址
        redisService.set(MiaoshaKey.getMiaoshaPath, "" + usrId + "_"+ goodsId, str);
        return str;
    }

    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        //生成计算公式
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //计算表达式的结果
        int rnd = calc(verifyCode);
        //把验证码的计算结果保存到redis中
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    //使用JavaScript引擎，计算表达式的结果
    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    private char[] ops = new char[]{'+','-','*'};
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;
    }
}
