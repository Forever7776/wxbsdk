package com.wxb.sdk.wx.api;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.wxb.sdk.wx.kit.HttpExtKit;
import com.wxb.sdk.wx.kit.ParaMap;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luomhy on 2015/4/24.
 */
public class CardApi {
    private static Map<String,String> ticketMap = new HashMap<String,String>();
    private static Map<String,Long> timeMap = new HashMap<String,Long>();
    private ParaMap paraMap;
    public CardApi(){
        AccessToken accessToken = AccessTokenApi.getAccessToken();
        paraMap = ParaMap.create("access_token",accessToken.getAccessToken());
    }

    /**
     * 上传LOGO
     * @param file
     */
    public ApiResult uploadLogo(File file){
        String url = "https://api.weixin.qq.com/cgi-bin/media/uploadimg";
        try {
            return new ApiResult(HttpExtKit.send(url, file, paraMap.getData()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取颜色
     * @return
     */
    public ApiResult queryColor(){
        String url = "https://api.weixin.qq.com/card/getcolors";
        return new ApiResult(HttpKit.get(url, paraMap.getData()));
    }

    /**
     * 创建卡券
     * @param jsonObject
     * @return
     */
    public ApiResult create(JSONObject jsonObject){
        String url = "https://api.weixin.qq.com/card/create";
        ApiResult apiResult = new ApiResult(HttpKit.post(url,paraMap.getData(),jsonObject.toJSONString()));
        return apiResult;
    }

    /**
     * 创建卡券二维码
     * @param jsonObject
     * @return
     */
    public ApiResult createQrcode(JSONObject jsonObject){
        String url ="https://api.weixin.qq.com/card/qrcode/create";
        return new ApiResult(HttpKit.post(url, paraMap.getData(), jsonObject.toJSONString()));
    }

    /*private ApiResult queryApiTicket(){
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
        ParaMap newP = paraMap;
        newP.put("type","wx_card");
        return new ApiResult(HttpKit.get(url, newP.getData()));
    }*/

    /**
     * 获得ticket
     * @return
     */
    public String queryTicket(){
        ApiConfig apiConfig= ApiConfigKit.getApiConfig();
        String appId = apiConfig.getAppId();

        Long oldtime = timeMap.get(appId);
        if(oldtime !=null && System.currentTimeMillis()-oldtime<7000*1000){
            return ticketMap.get(appId);
        }
        ApiResult result;
        for (int i = 0; i < 3; i++) {
            result = JsTicketApi.getTicket(JsTicketApi.JsApiType.wx_card);
            if (result.isSucceed()) {
                ticketMap.put(appId,result.getStr("ticket"));
                timeMap.put(appId,System.currentTimeMillis());
                return ticketMap.get(appId);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 设置卡券失效
     * @param code
     * @param card_id
     * @return
     */
    public ApiResult cancl(String code,String card_id){
        String url ="https://api.weixin.qq.com/card/code/unavailable";
        JSONObject jo = new JSONObject();
        jo.put("code",code);
        jo.put("card_id",card_id);
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }


    /**
     * 解密CODE
     * @param encrypt_code
     * @return
     */
    public ApiResult decode(String encrypt_code){
        String url ="http://api.weixin.qq.com/card/code/decrypt";
        JSONObject jo = new JSONObject();
        jo.put("encrypt_code",encrypt_code);
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }

    /**
     * 删除卡券
     * @param card_id
     * @return
     */
    public boolean delete(String card_id){
        String url ="https://api.weixin.qq.com/card/delete";
        JSONObject jo = new JSONObject();
        jo.put("card_id",card_id);
        ApiResult apiResult = new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
        return apiResult.isSucceed();
    }

    /**
     * 核销Code
     * @param card_id
     * @param code
     * @return
     */
    public boolean consume(String card_id,String code){
        String url ="https://api.weixin.qq.com/card/code/consume";
        JSONObject jo = new JSONObject();
        jo.put("card_id",card_id);
        jo.put("code",code);
        ApiResult apiResult = new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
        return apiResult.isSucceed();
    }

    /**
     * 查询code
     * @param code
     * @return
     */
    public ApiResult queryinfo(String code){
        String url ="https://api.weixin.qq.com/card/code/get";
        JSONObject jo = new JSONObject();
        jo.put("code",code);
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }

    /**
     * 批量查询卡列表
     * @param offset
     * @param count
     * @return
     */
    public ApiResult queryBatchInfo(int offset,int count){
        String url ="https://api.weixin.qq.com/card/batchget";
        JSONObject jo = new JSONObject();
        jo.put("offset",offset);
        jo.put("count",count);
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }

    /**
     * 查询卡券详情
     * @param card_id
     * @return
     */
    public ApiResult queryDetail(String card_id){
        String url ="https://api.weixin.qq.com/card/get";
        JSONObject jo = new JSONObject();
        jo.put("card_id",card_id);
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }

    /**
     * 设置测试白名单
     * @param openid
     * @param username
     * @return
     */
    public ApiResult setTestUser(String[] openid,String[] username){
        String url ="https://api.weixin.qq.com/card/testwhitelist/set";
        JSONObject jo = new JSONObject();
        jo.put("openid",openid);//openid数组
        jo.put("username",username);//微信号数组
        System.out.println(jo.toJSONString());
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }

    /**
     * 获取用户已领取卡券
     * @param openid
     * @param card_id
     * @return
     */
    public ApiResult getUserCardList(String openid,String card_id){
        String url ="https://api.weixin.qq.com/card/user/getcardlist";
        JSONObject jo = new JSONObject();
        jo.put("openid",openid);//openid
        jo.put("card_id",card_id);//卡券id
        System.out.println(jo.toJSONString());
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }

    /**
     * 更新卡券信息
     * @param jo
     * @return
     */
    public ApiResult update(JSONObject jo){
        String url ="https://api.weixin.qq.com/card/update";
        System.out.println(jo.toJSONString());
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }

    /**
     * 修改库存
     * @param card_id
     * @param increase_stock_value
     * @param reduce_stock_value
     * @return
     */
    public ApiResult modifystock(String card_id,int increase_stock_value,int reduce_stock_value){
        String url ="https://api.weixin.qq.com/card/modifystock";
        JSONObject jo = new JSONObject();
        jo.put("card_id",card_id);
        jo.put("increase_stock_value",increase_stock_value);
        jo.put("reduce_stock_value", reduce_stock_value);
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }

    /**
     * 根据分组群发卡券
     * @param jo
     * @return
     */
    public ApiResult sendCardByGroupId(JSONObject jo){
        String url ="https://api.weixin.qq.com/cgi-bin/message/mass/sendall";
        System.out.println(jo.toJSONString());
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }

    /**
     * 根据OpenID列表群发卡券
     * @param jo
     * @return
     */
    public ApiResult sendCardByOpenId(JSONObject jo){
        String url ="https://api.weixin.qq.com/cgi-bin/message/mass/send";
        System.out.println(jo.toJSONString());
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }
}
