package com.imooc.miaosha.redis;

/**
 * Created by 莫文龙 on 2018/6/12.
 */
public class UserKey extends BasePrefix {

    private UserKey(String prefix) {
        super(prefix);
    }

    private UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");

    public static void main(String[] args) {
        String prefix = UserKey.getById.getPrefix();
        System.out.println(prefix);
        String prefix1 = UserKey.getByName.getPrefix();
        System.out.println(prefix1);
    }
}
