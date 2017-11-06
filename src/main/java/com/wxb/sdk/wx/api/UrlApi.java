package com.wxb.sdk.wx.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 长链接转短链接
 * Created by luomhy on 2015/3/18.
 */
public class UrlApi {
    public static String longToShort(String url){
        String access_token = AccessTokenApi.token();
        StringBuffer urlSb = new StringBuffer("https://api.weixin.qq.com/cgi-bin/shorturl?access_token=").append(access_token);
        JSONObject jo = new JSONObject();
        jo.put("action","long2short");
        jo.put("long_url", url);
        String result = HttpKit.post(urlSb.toString(),jo.toJSONString());
        JSONObject rj = JSON.parseObject(result);
        if(rj.getInteger("errcode")==0){
            return rj.getString("short_url");
        }
        return url;
    }
    public static String longToShort(String url,Map<String,String> param){
        if (MapUtils.isEmpty(param)) return url;

        StringBuilder sb = new StringBuilder(url);
        boolean isFirst =false;
        if (url.indexOf("?") == -1) {
            isFirst = true;
            sb.append("?");
        }

        for (Map.Entry<String, String> entry : param.entrySet()) {
            if (isFirst) isFirst = false;
            else sb.append("&");

            String key = entry.getKey();
            String value = entry.getValue();
            //如果URL中有当前参数了
            if(StringUtils.indexOf(url,key)>-1) continue;

            if (StrKit.notBlank(value))
                try {value = URLEncoder.encode(value, "UTF-8");} catch (UnsupportedEncodingException e) {throw new RuntimeException(e);}

            sb.append(key).append("=").append(value);
        }
        return sb.toString();
    }

    /**
     * 获得授权链接
     * @param appid
     * @param url
     * @param isneedinfo
     * @param param
     * @return
     */
    public static String oauth(String appid,String url,boolean isneedinfo,String param){
        try {
            url = URLEncoder.encode(url, "UTF-8");
            String oauthUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=$APPID$&redirect_uri=$URL$&response_type=code&scope=$SCOPE$&state=STATE#wechat_redirect";
            oauthUrl = StringUtils.replace(oauthUrl, "$APPID$", appid);
            oauthUrl = StringUtils.replace(oauthUrl, "$URL$", url);
            oauthUrl = StringUtils.replace(oauthUrl, "$SCOPE$", isneedinfo?"snsapi_userinfo":"snsapi_base");
            if(StringUtils.isNotBlank(param)){
                oauthUrl = StringUtils.replace(oauthUrl, "STATE", param);
            }
            return oauthUrl;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }
    public static String oauth(String appid,String url){
        return oauth(appid,url,false,null);
    }

    public static JSONObject oauthCode(String appid,String appsecret,String code){
        Map<String,String> map = new HashMap<>();
        map.put("appid",appid);
        map.put("secret",appsecret);
        map.put("code",code);
        map.put("grant_type","authorization_code");
        return JSON.parseObject(HttpKit.get("https://api.weixin.qq.com/sns/oauth2/access_token",map));
    }
}
