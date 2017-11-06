package com.wxb.sdk.wx.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.wxb.sdk.wx.kit.HttpExtKit;
import com.wxb.sdk.wx.kit.ParaMap;

import java.io.File;

/**
 * Created by luomhy on 2015/4/24.
 * 门店管理API
 */
public class StoreApi {


    /**
     * buffer数据
     * @param file
     */
    public static JSONObject uploadImg(File file){
        String url = "https://file.api.weixin.qq.com/cgi-bin/media/uploadimg";
        try {
            ParaMap paraMap = ParaMap.createToken();
            ApiResult apiResult = new ApiResult(HttpExtKit.send(url, file, paraMap.getData()));
            return JSON.parseObject(apiResult.getJson());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建门店
     * @param jsonObject
     * @return
     */
    public static boolean create(JSONObject jsonObject){
        String url = "http://api.weixin.qq.com/cgi-bin/poi/addpoi";
        ParaMap paraMap = ParaMap.createToken();
        ApiResult apiResult = new ApiResult(HttpKit.post(url,paraMap.getData(),jsonObject.toJSONString()));
        return apiResult.isSucceed();
    }

    /**
     * 查询门店
     * @param poi_id
     * @return
     */
    public static ApiResult query(String poi_id){
        String url = "http://api.weixin.qq.com/cgi-bin/poi/getpoi";
        JSONObject jo = new JSONObject();
        jo.put("poi_id",poi_id);
        ParaMap paraMap = ParaMap.createToken();
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }

    /**
     * 查询门店列表
     * @param begin
     * @param limit
     * @return
     */
    public static ApiResult queryList(int begin,int limit){
        String url ="http://api.weixin.qq.com/cgi-bin/poi/getpoilist";
        JSONObject jo = new JSONObject();
        jo.put("begin",begin);
        jo.put("limit",limit);
        ParaMap paraMap = ParaMap.createToken();
        return new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
    }

    /**
     * 删除门店
     * @param poi_id
     * @return
     */
    public static boolean delete(String poi_id){
        String url ="http://api.weixin.qq.com/cgi-bin/poi/delpoi";
        JSONObject jo = new JSONObject();
        jo.put("poi_id",poi_id);
        ParaMap paraMap = ParaMap.createToken();
        ApiResult apiResult =new ApiResult(HttpKit.post(url,paraMap.getData(),jo.toJSONString()));
        return apiResult.isSucceed();
    }

    /**
     * 修改门店
     * @param jsonObject
     * @return
     */
    public static boolean update(JSONObject jsonObject){
        String url ="http://api.weixin.qq.com/cgi-bin/poi/updatepoi";
        ParaMap paraMap = ParaMap.createToken();
        ApiResult apiResult =new ApiResult(HttpKit.post(url,paraMap.getData(),jsonObject.toJSONString()));
        return apiResult.isSucceed();
    }

}
