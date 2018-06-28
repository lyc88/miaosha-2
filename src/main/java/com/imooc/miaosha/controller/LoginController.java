package com.imooc.miaosha.controller;

import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Created by 莫文龙 on 2018/6/12.
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoggerFactory.class);

    @Autowired
    MiaoshaUserService userService;

    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }


    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        //用户调用登录接口，进行验证登陆，登录成功后将session写入到redis中
        userService.login(response,loginVo);
        return Result.success(true);
    }

}
