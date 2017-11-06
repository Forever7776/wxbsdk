package com.wxb.utils;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestDeviceUtils {

    /**
     * android : 所有android设备
     * mac os : iphone ipad
     * windows phone:Nokia等windows系统的手机
     */
    public static boolean  isMobileDevice(HttpServletRequest request){
        String requestHeader = request.getHeader("user-agent");
        System.out.println(requestHeader);
        String[] deviceArray = new String[]{"android","iphone","ipad","windows phone"};
        if(requestHeader == null)
            return false;
        requestHeader = requestHeader.toLowerCase();
        for(int i=0;i<deviceArray.length;i++){
            if(requestHeader.indexOf(deviceArray[i])>0){
                return true;
            }
        }
        return false;
    }
}