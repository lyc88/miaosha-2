package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoshaOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by 莫文龙 on 2018/6/19.
 */
@Mapper
public interface MiaoshaOrderDao {

    @Insert("insert into miaosha_order (user_id,order_id,goods_id) " +
            "values(#{userId},#{orderId},#{goodsId})")
    public int insert(MiaoshaOrder miaoshaOrder);


    @Select("select * from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
    public MiaoshaOrder getByUserIdGoodsId(@Param("userId")long userId, @Param("goodsId")long goodsId);
}
