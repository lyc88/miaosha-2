package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoshaUserDao;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 莫文龙 on 2018/6/12.
 */

@Service
public class MiaoshaUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    private MiaoshaUserDao miaoshaUserDao;

    @Autowired
    private RedisService redisService;

    public void insert(MiaoshaUser miaoshaUser) {
        miaoshaUserDao.insert(miaoshaUser);
    }

    public MiaoshaUser getById(long id) {
        //取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (user != null) {
            return user;
        }
        //取数据库
        MiaoshaUser u = miaoshaUserDao.getById(id);
        if (u != null) {
            redisService.set(MiaoshaUserKey.getById, "" + id, u);
        }
        return u;
    }

    public boolean updatePassword(String token,long id, String password) {
        //取user
        MiaoshaUser user = miaoshaUserDao.getById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoshaUser u = new MiaoshaUser();
        u.setId(id);
        u.setPassword(MD5Util.formPassToDbPass(password,user.getSalt()));
        miaoshaUserDao.update(u);
        //处理缓存
        redisService.delete(MiaoshaUserKey.getById,"" + id);
        user.setPassword(u.getPassword());
        redisService.set(MiaoshaUserKey.token,token,user);
        return true;
    }

    public boolean login(HttpServletResponse response,LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if (miaoshaUser == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //数据库中的2次MD5
        String dbPass = miaoshaUser.getPassword();
        String reallyDbPass = MD5Util.formPassToDbPass(password, miaoshaUser.getSalt());
        if (!dbPass.equals(reallyDbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        MiaoshaUser user = getById(Long.valueOf(mobile));
        //生成cookie
        String token = UUIDUtil.uuid();
        //向redis更新和向浏览器发送
        addCookie(response,token,user);
        return true;
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        //从redis中查询该seesion信息出来
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        if (user != null) {
            //从新更新redis与cookie的信息
            addCookie(response,token,user);
        }
        //从redis里面取出数据
        return user;
    }


    //有一个功能 1.向redis里新增或者是更新过期时间  2.设置cookie发送给客户端
    private void addCookie(HttpServletResponse response,String token,MiaoshaUser user) {

        //更新redis的过期时间
        redisService.set(MiaoshaUserKey.token,token,user);
        //更新Cookie的过期时间
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
