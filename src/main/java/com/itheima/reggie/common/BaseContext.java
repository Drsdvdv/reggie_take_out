package com.itheima.reggie.common;

public class BaseContext {
    //用来存储用户是id
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
    //设置值
    public static void setCurrentId(long id){
        threadLocal.set(id);
    }
    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
