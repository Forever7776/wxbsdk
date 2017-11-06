package com.wxb.sdk.wx.api;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.wxb.sdk.wx.kit.HttpExtKit;
import com.wxb.sdk.wx.kit.ParaMap;

import java.io.File;

/**
 * Created by luomhy on 2015/3/18.
 * http://mp.weixin.qq.com/wiki/14/7e6c03263063f4813141c3e17dd4350a.html
 */
public class MaterialApi {
    private static final String ADDURL = "https://api.weixin.qq.com/cgi-bin/material/add_news";
    private static final String ADDFILEURL = "http://api.weixin.qq.com/cgi-bin/material/add_material";

    private static final String QUERYURL = "https://api.weixin.qq.com/cgi-bin/material/get_material";

    private static final String DELURL ="https://api.weixin.qq.com/cgi-bin/material/del_material";
    private static final String EDITURL="https://api.weixin.qq.com/cgi-bin/material/update_news";

    private static final String COUNTURL ="https://api.weixin.qq.com/cgi-bin/material/get_materialcount";
    private static final String LISTTURL ="https://api.weixin.qq.com/cgi-bin/material/batchget_material";
    /**
     * 新增图文
     * @return
     */
    public static ApiResult createGraphicMaterial(String json){

        return new ApiResult(HttpKit.post(ADDURL, ParaMap.createToken().getData(),json));
    }

    /**
     * 新增其他类型的图文
     *  type 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
     * @param
     * @return
     */
    public static ApiResult createFileMaterial(File file,String type){
        try {
            String json =  HttpExtKit.send(ADDFILEURL, file, ParaMap.createToken().put("type", type).getData());
            return new ApiResult(json);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取图文素材
     * @param material_id
     * @return
     */
    public static ApiResult queryGraphicMaterial(String material_id){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("media_id",material_id);
        return new ApiResult(HttpKit.post(QUERYURL,ParaMap.createToken().getData(),jsonObject.toJSONString()));
    }
    public static ApiResult queryFileMaterial(String material_id){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("media_id",material_id);
        HttpExtKit.downFile(QUERYURL + "?access_token=" + AccessTokenApi.token(), jsonObject.toJSONString(), material_id);
        return new ApiResult(HttpKit.post(QUERYURL, ParaMap.createToken().getData(), jsonObject.toJSONString()));
    }

    public static ApiResult delMaterial(String material_id){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("media_id", material_id);
        return new ApiResult(HttpKit.post(DELURL,ParaMap.createToken().getData(),jsonObject.toJSONString()));
    }

    public static ApiResult updateMaterial(String json){
        return new ApiResult(HttpKit.post(EDITURL,ParaMap.createToken().getData(),json));
    }

    /**
     * 获取素材总数
     * http://mp.weixin.qq.com/wiki/16/8cc64f8c189674b421bee3ed403993b8.html
     * @param accountId
     * @return
     */
    public static ApiResult countMaterial(String accountId){
        return new ApiResult(HttpKit.get(COUNTURL, ParaMap.createToken().getData()));
    }
    /**图片（image）、视频（video）、语音 （voice）、图文（news）
     * http://mp.weixin.qq.com/wiki/12/2108cd7aafff7f388f41f37efa710204.html
     * offset 从全部素材的该偏移位置开始返回，0表示从第一个素材 返回
     * count  1~20之间的  获取长度
     */
    public static ApiResult listMaterial(String type,Integer offset,Integer count){
        count = count>20 || count<=0?20:count;
        JSONObject jo = new JSONObject();
        jo.put("type",type);
        jo.put("offset",offset);
        jo.put("count",count);
        return new ApiResult(HttpKit.post(LISTTURL, ParaMap.createToken().getData(), jo.toJSONString()));
    }

}
