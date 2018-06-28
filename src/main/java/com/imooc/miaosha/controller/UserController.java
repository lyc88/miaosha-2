package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.*;
import java.util.Date;

/**
 * Created by 莫文龙 on 2018/6/20.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MiaoshaUserService miaoshaUserService;

    @Autowired
    private RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model,MiaoshaUser miaoshaUser) {
        return Result.success(miaoshaUser);
    }

    @RequestMapping("/insert")
    @ResponseBody
    public int insert() {
        long a = 13727007916L;
        //生成用户向数据库里面插进去
        MiaoshaUser miaoshaUser = new MiaoshaUser();
        miaoshaUser.setNickname("wenlong");
        miaoshaUser.setLastLoginDate(new Date());
        miaoshaUser.setLoginCount(1);
        miaoshaUser.setPassword("e9788a73269bfd7ecdbc515f111ebc91");
        miaoshaUser.setSalt("abc");
        int i = 1;
        for (; i <= 5000; i ++) {
            a = a + 1;
            miaoshaUser.setId(a);
            System.out.println(i);
            //生成cookie
            String token = UUIDUtil.uuid();
            //向redis更新
            redisService.set(MiaoshaUserKey.token, token, MiaoshaUser.class);
            //插入数据库
            miaoshaUserService.insert(miaoshaUser);
        }
        return i;
    }


    //将用户信息生成token并写入到redis,txt文件
    @RequestMapping("/output")
    @ResponseBody
    public int outPutTxt() throws IOException {
        File file = new File("D:\\b.txt");
        FileWriter fileWriter = new FileWriter(file);
        long a = 13727007916L;
        int i  = 1;
        for (;i <= 5000; i ++) {
            a = a + 1;
            MiaoshaUser user = miaoshaUserService.getById(a);
            String uuid = UUIDUtil.uuid();
            redisService.set(MiaoshaUserKey.token,uuid,user);
            fileWriter.write(String.valueOf(a));
            fileWriter.write(",");
            fileWriter.write(uuid);
            fileWriter.write("\r\n");
            fileWriter.flush();
        }
        return i;
    }


    public static void main(String[] args) throws IOException {

    }

}