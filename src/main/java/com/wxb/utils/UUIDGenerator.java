package com.wxb.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * UUID生成器
 * @author 刘英俊
 * @date 2015-01-14
 */
public class UUIDGenerator { 
	
    public UUIDGenerator() { 
    
    } 
    
    /** 
     * 获得一个UUID 
     * @return String UUID 
     */ 
    public static String getUUID(){ 
        String s = UUID.randomUUID().toString();
        StringBuffer sb = new StringBuffer();
        //去掉“-”符号
        return StringUtils.replace(s, "-", StringUtils.EMPTY);
    }
    
    /** 
     * 获得指定数目的UUID 
     * @param number int 需要获得的UUID数量 
     * @return String[] UUID数组 
     */ 
    public static String[] getUUID(int number){ 
        if(number < 1){ 
            return null; 
        } 
        String[] ss = new String[number]; 
        for(int i=0;i<number;i++){ 
            ss[i] = getUUID(); 
        } 
        return ss; 
    }
}   
