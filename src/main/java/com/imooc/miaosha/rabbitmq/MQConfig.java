package com.imooc.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 莫文龙 on 2018/6/22.
 */

@Configuration
public class MQConfig {

//    public static final String QUEUE = "miaosha_queue";



    //队列名称->创建一些消息队列,每个队列的唯一标识就是名字
    public static final String QUEUE = "queue";
//    public static final String QUEUE2 = "queue2";
//    public static final String QUEUE3 = "queue3";
//    public static final String QUEUE4 = "queue4";

    //交换机名称
//    public static final String Topic_Exchange = "topicExchange";
//    public static final String Fanout_Exchange = "fanoutExchange";
//    public static final String Header_Exchange = "headerExchange";


    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }



    /**
     * 第一种: Direct模式 --> 最简单的模式，直接向某个队列发送消息，不需要交换机
     *           没有交换机，所以不需要绑定交换机
     */

    //每个队列都有一个名字
/*
    @Bean
    public Queue queue1() {
        return new Queue(QUEUE1, true);
    }
    @Bean
    public Queue queue2() {
        return new Queue(QUEUE2, true);
    }
    @Bean
    public Queue queue3() {
        return new Queue(QUEUE3, true);
    }
    @Bean
    public Queue queue4() {return new Queue(QUEUE4,true);}
*/


    /**
     *
     *   交换机: -->每个交换机都有一个名字
     *      发送者向外发送的消息时，并不是直接投递到队列中去，
     *      而是将消息先发送到交换机中，再由交换机再把数据发送到队列中
     *
     *      下面将介绍的三种模式都是需要队列绑定到交换机上
     */

    /**
     * 第二种: topic模式，TopicExchange交换机，可以通配队列 使用通配符
     *
     *  将消息发送到TopicExchange交换机，该消息将转发到绑定在该交换机下面的队列(是符合通配的队列，而不是发到全部的队列)
     */
//    @Bean
//    public TopicExchange topicExchange() {
//        return new TopicExchange(Topic_Exchange);
//    }
    /**
     *  这两个队列绑定在一个TopicExchange交换机下
     *
     *  当消息定义规则为key.1时，queue2和queue3都能收到信息，
     *  但是当定义规则为key.2是，只有queue3能收到信息，# 表示任意值任意数量
     */
//    @Bean
//    public Binding topicBinding1() {return BindingBuilder.bind(queue2()).to(topicExchange()).with("key.1");}
//    @Bean
//    public Binding topicBinding2() {return BindingBuilder.bind(queue3()).to(topicExchange()).with("key.#");}


    /**
     *  第三种:  Fanout模式，交换机是FanoutExchange ,是广播的形式,不需要配置队列的规则
     *          当有消息来的时候，就将消息发送给绑定在该交换机下的所有队列
     */
 /*   @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(Fanout_Exchange);
    }
    //将两个队列绑定到该Fanout交换机下
    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(queue2()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(queue3()).to(fanoutExchange());
    }
*/

    /**
     * 第四种: Header模式，HeaderExchange交换机
     */
   /* @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(Header_Exchange);
    }
    //将queue绑定到交换机下
    @Bean
    public Binding headerBinding() {
        //以上面的不同，这里需要创建一个map对象
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("h1", "v1");
        map.put("h2", "v2");
        return BindingBuilder.bind(queue4()).to(headersExchange()).whereAll(map).match();
    }
*/




}