package com.wxb.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by luomhy on 2015/10/9.
 */
public class HttpUtils {
    /**
     * 判断是否微信内置浏览器
     * @param request
     * @return
     */
    public static boolean isWeixin(HttpServletRequest request){
        String ua = request.getHeader("user-agent").toLowerCase();
        return StringUtils.indexOf(ua, "micromessenger")>-1;
    }

    /**
     * 判断是否Ajax 请求
     * @param request
     * @return
     */
    public static boolean isAjax(HttpServletRequest request){
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(header);
    }

    /**
     * 得到URL
     * @param request
     * @return
     */
    public static String url(HttpServletRequest request){
        StringBuffer sb = request.getRequestURL();
        if(StringUtils.isNoneBlank(request.getQueryString())) {
            sb.append("?").append(request.getQueryString());
        }
        return sb.toString();
    }
}
