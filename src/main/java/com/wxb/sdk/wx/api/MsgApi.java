package com.wxb.sdk.wx.api;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.wxb.sdk.wx.kit.ParaMap;

/**
 * Created by luomhy on 2015/4/21.
 * 消息API
 */
public class MsgApi {
    //?access_token=ACCESS_TOKEN
    private static String send_url = "https://api.weixin.qq.com/cgi-bin/message/custom/send";
    public static boolean sendTextMsg(String openid,String msg){
        String str = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}";
        return sendMsg(String.format(str,openid,msg));
    }
    public static boolean sendNewsMsg(String openid,String media_id){
        String str ="{\"touser\":\"%s\",\"msgtype\":\"mpnews\",\"mpnews\":{\"media_id\":\"%s\"}";
        return sendMsg(String.format(str,openid,media_id));
    }
    private static boolean sendMsg(String str){
        ParaMap pm = ParaMap.createToken();
        ApiResult apiResult =  new ApiResult(HttpKit.post(send_url, pm.getData(), str));
        return apiResult.isSucceed();
    }

    //上传图文消息素材
    public ApiResult uploadNews(JSONObject jo){
        String url = "https://api.weixin.qq.com/cgi-bin/media/uploadnews";
        ParaMap pm = ParaMap.createToken();
        return new ApiResult(HttpKit.post(url,pm.getData(),jo.toJSONString()));
    }

    //群发图文消息
    public ApiResult sendNewsMsg(JSONObject jo){
        String url = "https://api.weixin.qq.com/cgi-bin/message/mass/send";
        ParaMap pm = ParaMap.createToken();
        return new ApiResult(HttpKit.post(url,pm.getData(),jo.toJSONString()));
    }
}
