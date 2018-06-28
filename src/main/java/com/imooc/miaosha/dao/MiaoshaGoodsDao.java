package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoshaGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * Created by 莫文龙 on 2018/6/21.
 */

@Mapper
public interface MiaoshaGoodsDao {

    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0;")
    public int reduceStock(MiaoshaGoods miaoshaGoods);



}
