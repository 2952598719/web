package top.orosirian.blog.utils;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.lang.Snowflake;

public class UidGenerator {

    public static Long getSnowUid() {
        Snowflake sf = new Snowflake();
        Long id = sf.nextId();
        return id;
    }

    public static String pw_hash(String plaintext) {
        return BCrypt.hashpw(plaintext); 
    }

    public static void main(String[] args) {
        System.out.println(getSnowUid());
        System.out.println(pw_hash("aa111111"));
    }
   
    
}
