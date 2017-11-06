package com.wxb.sdk.location.baidu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luomhy on 2015/9/17.
 * Geocoding API 是一类简单的HTTP接口，用于提供从地址到经纬度坐标或者从经纬度坐标到地址的转换服务
 * API 见 --> http://developer.baidu.com/map/index.php?title=webapi/guide/webservice-geocoding
 */
public class GeocodingAPI {
    private static final String url = "http://api.map.baidu.com/geocoder/v2/";
    private String ak;
    private final static String OUTPUT = "json";
    public GeocodingAPI(String ak){
        this.ak = ak;
    }

    /**
     *
     * 根据经纬度得到详细地址
     * @param lat 纬度
     * @param lng 经度
     * @return
     * {"status":0,"result":{"location":{"lng":114.1589579965,"lat":22.650414033007},"formatted_address":"广东省深圳市龙岗区东深路","business":"康桥","addressComponent":{"city":"深圳市","country":"中国","direction":"","distance":"","district":"龙岗区","province":"广东省","street":"东深路","street_number":"","country_code":0},"poiRegions":[],"sematic_description":"深圳市龙岗区文华学校东南255米","cityCode":340}}
     */
    public  JSONObject query(String lat,String lng){
        Map<String,String> map = new HashMap<>();
        map.put("output",OUTPUT);
        map.put("ak", ak);
        map.put("location",lat+","+lng);
        return JSON.parseObject(HttpKit.get(url,map));
    }

    /**
     * 根据地址得到经纬度
     * @param address
     * @return
     */
    public JSONObject query(String address){
        Map<String,String> map = new HashMap<>();
        map.put("output",OUTPUT);
        map.put("ak", ak);
        map.put("address",address);
        return JSON.parseObject(HttpKit.get(url,map));
    }
}
