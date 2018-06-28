package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.GoodsDao;
import com.imooc.miaosha.dao.MiaoshaGoodsDao;
import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 莫文龙 on 2018/6/15.
 */

@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private MiaoshaGoodsDao miaoshaGoodsDao;

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        //将goods表减库存
        Goods g = new Goods();
        g.setId(goods.getId());
        int i = goodsDao.reduceStock(g);
        //将秒杀表减库存
        MiaoshaGoods mg = new MiaoshaGoods();
        mg.setGoodsId(goods.getId());
        int i1 = miaoshaGoodsDao.reduceStock(mg);
        if (i == 0 || i1 == 0) {
            return false;
        }
        return true;
    }
}
