package com.wxb.sdk.wx.api;

import com.jfinal.kit.HttpKit;
import com.wxb.sdk.wx.kit.MapKit;
import com.wxb.sdk.wx.kit.ParaMap;
import com.wxb.utils.UUIDGenerator;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by luomhy on 2015/3/20.
 */
public class JsApi {
    private static final String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
    private static Map<String,String> jsMap = new HashMap<String,String>();
    private static Map<String,Long> jsTimeMap = new HashMap<String,Long>();

    private static  String jsTicket(){
        ApiConfig config = ApiConfigKit.getApiConfig();
        String appId = config.getAppId();
        Long oldtime = jsTimeMap.get(appId);
        if(oldtime !=null && System.currentTimeMillis()-oldtime<7000*1000){
            return jsMap.get(appId);
        }
        try {
            ApiResult result;
            for (int i = 0; i < 3; i++) {
                result = JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
                if (result.isSucceed()) {
                    jsMap.put(appId,result.getStr("ticket"));
                    jsTimeMap.put(appId,System.currentTimeMillis());
                    return jsMap.get(appId);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    private static String apiTicket(){
        ApiConfig config = ApiConfigKit.getApiConfig();
        String appId = config.getAppId();
        Long oldtime = jsTimeMap.get(appId);
        if(oldtime !=null && System.currentTimeMillis()-oldtime<7000*1000){
            return jsMap.get(appId);
        }
        try {
            ApiResult result;
            for (int i = 0; i < 3; i++) {
                result = JsTicketApi.getTicket(JsTicketApi.JsApiType.wx_card);
                if (result.isSucceed()) {
                    jsMap.put(appId,result.getStr("ticket"));
                    jsTimeMap.put(appId,System.currentTimeMillis());
                    return jsMap.get(appId);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    private static String generateSign(Map<String, String> map){
        Map<String, String> tmap = MapKit.order(map);
        String str = MapKit.mapJoin(tmap, true, false);
        return DigestUtils.shaHex(str);
    }

    private static String generateCardSign(Map<String, String> map){
        Map<String, String> tmap = MapKit.orderByValue(map);
        StringBuilder sb = new StringBuilder();
        for(String key : map.keySet()){
            if(StringUtils.isNotBlank(map.get(key))){
                sb.append(map.get(key));
            }
        }
        String str = sb.toString();
        return DigestUtils.shaHex(str);
    }

    public static Map<String,Object>  jsapi(String url){
        String ticket = jsTicket();
        Map<String,String> map = new TreeMap<String, String>();
        Long time = System.currentTimeMillis()/1000;
        String nocestr = UUIDGenerator.getUUID();
        map.put("jsapi_ticket",ticket);
        map.put("noncestr", nocestr);
        map.put("timestamp",time.toString());
        map.put("url",url);
        String sign = generateSign(map);
        Map<String,Object> resultMap = new HashMap<String, Object>();
        resultMap.put("timestamp",time);
        resultMap.put("nonceStr",nocestr);
        resultMap.put("signature",sign);
        return resultMap;
    }

    public static Map<String,Object> wxcard(){
        ApiConfig config = ApiConfigKit.getApiConfig();
        String ticket = apiTicket();

        Map<String,String> map = new TreeMap<String, String>();
        Long time = System.currentTimeMillis()/1000;
        String nocestr = UUIDGenerator.getUUID();

        map.put("api_ticket",ticket);
        map.put("noncestr", nocestr);
        map.put("timestamp",time.toString());
        map.put("app_id",config.getAppId());

        String cardSign = generateCardSign(map);
        Map<String,Object> resultMap = new HashMap<String, Object>();
        resultMap.put("timestamp",time);
        resultMap.put("nonceStr",nocestr);
        resultMap.put("cardSign",cardSign);

        return resultMap;
    }

}