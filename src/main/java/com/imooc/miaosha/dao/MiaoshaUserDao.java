package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.*;

/**
 * Created by 莫文龙 on 2018/6/12.
 */
@Mapper
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id = #{id}")
    MiaoshaUser getById(@Param("id") long id);


    @Insert("insert into miaosha_user(id,nickname,password,salt,head,register_date,last_login_date,login_count) " +
            "values(#{id},#{nickname},#{password},#{salt},#{head},#{registerDate},#{lastLoginDate},#{loginCount})")
    void insert(MiaoshaUser user);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    void update(MiaoshaUser u);
}
