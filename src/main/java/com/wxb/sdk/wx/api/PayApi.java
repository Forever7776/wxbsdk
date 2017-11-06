package com.wxb.sdk.wx.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.wxb.sdk.wx.kit.HttpExtKit;
import com.wxb.sdk.wx.kit.MapKit;
import com.wxb.utils.UUIDGenerator;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by luomhy on 2015/3/27.
 */
public class PayApi {
    private static final String MCH_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    private static final String QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    private static final String REFUND_URL="https://api.mch.weixin.qq.com/secapi/pay/refund";
    private static final String REFUND_QUERY_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
    private static HashMap<String,String> refundMap = new HashMap<>();
    static {
        refundMap.put("SUCCESS","退款成功");
        refundMap.put("FAIL","退款失败");
        refundMap.put("PROCESSING","退款处理中");
        refundMap.put("NOTSURE","未确定，需要商户原退款单号重新发起");
        refundMap.put("CHANGE","转入代发，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，资金回流到商户的现金帐号，需要商户人工干预，通过线下或者财付通转账的方式进行退款");
    }


    public static String getSign(Map<String,String> param,String key) throws UnsupportedEncodingException {
        String str1 = createSign(param,false);
        String stringSignTemp = str1.concat("&key=").concat(key);
        return DigestUtils.md5Hex(stringSignTemp).toUpperCase();
    }
    /**
     * 构造签名
     * @param params
     * @param encode
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String createSign(Map<String, String> params, boolean encode) throws UnsupportedEncodingException {
        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuffer temp = new StringBuffer();
        boolean first = true;
        for (Object key : keys) {
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = params.get(key);
            String valueString = "";
            if (null != value) {
                valueString = value.toString();
            }
            if (encode) {
                temp.append(URLEncoder.encode(valueString, "UTF-8"));
            } else {
                temp.append(valueString);
            }
        }
        return temp.toString();
    }


    public static String postXml(Map<String,String> map){
        String xml = MapKit.mapToXml(map);
        System.out.println(xml);
        String xmlResult = HttpKit.post(MCH_URL, xml);
        return MapKit.xmlToJsonStr(xmlResult);
    }

    /**
     * 查询订单数据
     * @param appId
     * @param mchId
     * @param paykey
     * @param code
     * @return
     */
    public static JSONObject queryOrder(String code){
        try {
            ApiConfig config = ApiConfigKit.getApiConfig();
            Map<String, String> map = new LinkedHashMap<>();
            map.put("appid", config.getAppId());
            map.put("mch_id", config.getPayMchId());
            map.put("nonce_str", UUIDGenerator.getUUID());
            map.put("out_trade_no", code);
            map.put("sign", getSign(map, config.getPayKey()));
            return JSON.parseObject(MapKit.xmlToJsonStr(postXml(map)));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 退款接口
     * @return
     * {"refund_id":"2006750435201508280033571690","refund_fee":"1","coupon_refund_fee":"0","refund_channel":"","return_msg":"OK","appid":"wx61080f4c9c842212","nonce_str":"4EQVTJkvdlVLlB4O","out_trade_no":"1440581877906","out_refund_no":"TK-1440581877906","transaction_id":"1006750435201508260714484528","coupon_refund_count":"0","sign":"CC3507DA4EFA2A8E7B323D56DD8DACAA","result_code":"SUCCESS","mch_id":"1234708802","total_fee":"1","return_code":"SUCCESS","cash_refund_fee":"1","cash_fee":"1"}
     */
    public static JSONObject refund(String out_trade_no,Integer total_fee){
        try {
            ApiConfig config = ApiConfigKit.getApiConfig();
            String nonce_str = UUIDGenerator.getUUID();
            if(StringUtils.isBlank(config.getPayCred())){
                return new JSONObject();
            }else{
                File file = new File(config.getPayCred());
                if(!file.exists()){
                    JSONObject result = new JSONObject();
                    result.put("return_code","ERROR");
                    return result;
                }
            }

            Map<String, String> param = new LinkedHashMap<String, String>();
            param.put("appid",config.getAppId());
            param.put("mch_id",config.getPayMchId());
            param.put("nonce_str",nonce_str);
            param.put("op_user_id",config.getPayMchId());
            param.put("out_refund_no","TK-"+out_trade_no);
            param.put("out_trade_no",out_trade_no);
            param.put("refund_fee",total_fee.toString());
            param.put("total_fee",total_fee.toString());
            String sign = getSign(param, config.getPayKey());
            param.put("sign",sign);
            String xml = MapKit.mapToXml(param);
            String str = HttpExtKit.HttpCateP12(REFUND_URL, config.getPayCred(), config.getPayMchId(),xml);
            String json = MapKit.xmlToJsonStr(str);
            return JSONObject.parseObject(json);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 退款单查询
     * @param code
     * @return
     */
    public static JSONObject refundQuery(String code) {
        try {
            ApiConfig config = ApiConfigKit.getApiConfig();
            String nonce_str = UUIDGenerator.getUUID();
            Map<String, String> param = new LinkedHashMap<String, String>();
            param.put("appid", config.getAppId());
            param.put("mch_id", config.getPayMchId());
            param.put("nonce_str", nonce_str);
            param.put("out_trade_no", code);
            String sign = PayApi.getSign(param, config.getPayKey());
            param.put("sign", sign);

            String xml = MapKit.mapToXml(param);
            System.out.println(xml);
            String xmlResult = HttpKit.post(REFUND_QUERY_URL, xml);
            return JSON.parseObject(MapKit.xmlToJsonStr(xmlResult));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getRefundValue(String key){
        return refundMap.get(key);
    }

}
