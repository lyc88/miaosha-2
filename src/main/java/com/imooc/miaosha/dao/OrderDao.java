package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

/**
 * Created by 莫文龙 on 2018/6/19.
 */

@Mapper
public interface OrderDao {

    @Select("select * from order_info where user_id = #{userId} and goods_id = #{goodsId};")
    OrderInfo getMiaoshaOrderByUserIdGoodsId(@Param("userId") Long UserId, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long insert(OrderInfo orderInfo);

}