package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.rabbitmq.MiaoshaMessage;
import com.imooc.miaosha.redis.AccessKey;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 莫文龙 on 2018/6/16.
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    private static Logger log = LoggerFactory.getLogger(MiaoshaController.class);

    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    MQSender sender;

    private Map<Long, Boolean> map = new HashMap<Long, Boolean>();


    @RequestMapping(value = "/{path}/do_miaosha")
    @ResponseBody
    public Result list(Model model, MiaoshaUser user,
                       @RequestParam("goodsId") long goodsId,
                       @PathVariable("path")String path) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证加密的url地址path
        boolean check = miaoshaService.checkPath(user.getId(),goodsId,path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLGAL);
        }

        //内存标记，减少redis的访问
        Boolean isOver = map.get(goodsId);
        if (isOver) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //在redis中预减库存
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            //没有仓库标志位ture，这样再有请求来就不要访问redis了
            map.put(goodsId, true);
            //已经秒杀完毕
            miaoshaService.setGoodsOver(goodsId);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        OrderInfo orderInfo = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (orderInfo != null) {
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }
        //入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        //发布消息
        sender.sendMiaoshaMessage(mm);
        log.info("订单已经进入消息队列了");
        return Result.success(0);
    }



    @RequestMapping(value = "/do_miaosha")
    @ResponseBody
    public Result list2(Model model, MiaoshaUser user,
                       @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //内存标记，减少redis的访问
        Boolean isOver = map.get(goodsId);
        if (isOver) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //在redis中预减库存
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            //没有仓库标志位ture，这样再有请求来就不要访问redis了
            map.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        //发布消息
        sender.sendMiaoshaMessage(mm);
        log.info("用户名为: " + user.getId() + "的订单已经进入消息队列了");
        return Result.success(0);
    }




    //获取加密url
    @RequestMapping(value = "/path")
    @ResponseBody
    public Result<String> path(HttpServletRequest request,Model model, MiaoshaUser user,
                               @RequestParam("goodsId") long goodsId,
                               @RequestParam(value = "verifyCode",defaultValue = "0")int verifyCode) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //查询访问的次数
        String uri = request.getRequestURI();
        String key = uri + "_" + user.getId();
        Integer count = redisService.get(AccessKey.access, key, Integer.class);
        //不能再5秒内访问5次
        if (count == null) {
            redisService.set(AccessKey.access, key, 1);
        } else if (count < 5) {
            redisService.incr(AccessKey.access, key);
        } else {
            log.info("用户:" + user.getId() + "访问太频繁");
            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }

        //生成加密地址
        String path = miaoshaService.createMiaoshaPath(user.getId(),goodsId);
        return Result.success(path);
    }



    @RequestMapping(value = "/verifyCode")
    @ResponseBody
    public Result<String> verifyCode(HttpServletResponse response,Model model, MiaoshaUser user,
                                     @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        BufferedImage image = miaoshaService.createVerifyCode(user,goodsId);
        try {
            //用流将图片信息写到客户端
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }







/*

    @RequestMapping(value = "/do_miaosha1")
    public String list1(Model model, MiaoshaUser user,
                       @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return "login";
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stock = goods.getStockCount();
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀到了
        OrderInfo order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEAT_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        //减库存，写入秒杀订单
        OrderInfo orderInfo =  miaoshaService.miaosha(user,goods);
        //将订单信息和商品信息直接返回给用户
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods",goods);
        return "order_detail";
    }

*/

    /**
     * 返回orderId:成功
     * -1: 秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaosha(Model model, MiaoshaUser user,
                                   @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //获取秒杀结果
        Long  result = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return Result.success(result);
    }



    //Spring初始化之后会进行初始化
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.listGoodsVo();
        if (list == null) return;
        for (GoodsVo g : list) {
            //在数据库的数量读取到redis中进行预减
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + g.getId(),g.getStockCount() );
            //内存标记，当设为false的时候不要访问redis
            map.put(g.getId(), false);
        }
    }
}
