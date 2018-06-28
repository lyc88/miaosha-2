package com.imooc.miaosha.controller;

import com.imooc.miaosha.rabbitmq.MQSender;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 莫文龙 on 2018/6/22.
 */

@Controller
@RequestMapping("/rabbit")
public class DemoController {
/*

    @Autowired
    private MQSender sender;

    @RequestMapping("/direct")
    @ResponseBody
    public void direct() {
        sender.sendDirect("hello direct");
    }

    @RequestMapping("/topic")
    @ResponseBody
    public void topic() {
        sender.sendTopic("hello topic");
    }

    @RequestMapping("/fanout")
    @ResponseBody
    public void fanout() {
        sender.sendFanout("hello fanout");
    }

    @RequestMapping("/header")
    @ResponseBody
    public void header() {
        sender.sendHeader(" hello header");
    }

*/

}
