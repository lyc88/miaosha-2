package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 莫文龙 on 2018/6/22.
 */

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQConfig.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String str = RedisService.beanToString(mm);
        log.info("send message to " + MQConfig.QUEUE + " :" + str);
        //发送名称为QUEUE的队列
        amqpTemplate.convertAndSend(MQConfig.QUEUE,str);
    }


/*
    //这个对象可以操作queue。或者交换机，通过交换机操作消息队列
    @Autowired
    AmqpTemplate amqpTemplate;


    //第一种: Direct模式-->直接向指定的队列发送消息
    public void sendDirect(Object object) {
        String str = RedisService.beanToString(object);
        log.info("direct send :" + str);
        //指定向MQConfig.QUEUE1的队列发布消息
        amqpTemplate.convertAndSend(MQConfig.QUEUE1,str);
    }

    //第二种: Topic模式-->匹配到指定队列
    public void sendTopic(Object object) {
        String str = RedisService.beanToString(object);
        log.info("topic send :" + str + 1);
        log.info("topic send :" + str + 2);
        *//**
         * 第一个参数是发送给那个交换机，它不需要指定发送到哪一个队列，交换机会发送到匹配的队列的
         * 第二个参数是告诉交换机匹配的规则，发送给什么队列
         * 第三个参数是发送什么内容
         *//*
        amqpTemplate.convertAndSend(MQConfig.Topic_Exchange, "key.1", str + 1);
        amqpTemplate.convertAndSend(MQConfig.Topic_Exchange, "key.2", str + 2);
    }

    //第三种: Fanout模式，广播模式
    public void sendFanout(Object object) {
        String str = RedisService.beanToString(object);
        log.info("fanout send :" + str);
        //广播模式不需要匹配像topic模式那么有匹配规则的，所以第二个参数填空串就行
        amqpTemplate.convertAndSend(MQConfig.Fanout_Exchange,"",str);
    }

    //第四种: Header模式
    public void sendHeader(Object object) {
        String str = RedisService.beanToString(object);
        log.info("header send " + str);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("h1","v1");
        properties.setHeader("h2","v2");
        //以字节发送
        Message message = new Message(str.getBytes(), properties);
        //生产消息
        amqpTemplate.convertAndSend(MQConfig.Header_Exchange,"",message);
    }*/


}
