package com.wxb.sdk.shunfeng;

import com.jfinal.kit.HttpKit;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by luomhy on 2015/8/3.
 */
public class SFApi {
    private static final String URL = "https://bsp-oisp.test.sf-express.com/bsp-oisp/sfexpressService";
    private String code;
    private String checkword;
    private static final String XML_HEAD = "<?xml version='1.0' encoding='UTF-8'?>";
    private static final String REQUEST_TPL = "<Request service=\"%s\" lang=\"zh-CN\"><Head>%s</Head><Body>%s</Body></Request> ";
    private static final String PARAM_TPL = "xml=%s&verifyCode=%s";

    public SFApi(String code, String checkword){
        this.code = code;
        this.checkword = checkword;
    }

    /**
     * 下订单接口
     * @param map
     * @return
     */
    public String goOrder(Map<String,Object> map){
        String str = run("OrderService", "Order", map);
        System.out.println(str);
        return str;
    }

    /**
     * 路由查询
     * @param map
     * @return
     */
    public String routeQuery(Map<String,Object> map){
        String str = run("RouteService", "RouteRequest", map);
        return str;
    }

    private String run(String service_name,String attr_name,Map<String,Object> map){
        String xml = xmlMain(service_name, attr_name, map);
        String verifyCode = verifyCode(xml);

        System.out.println("xml-->" + xml);
        System.out.println("code-->" + verifyCode);

        return HttpKit.post(URL, String.format(PARAM_TPL, xml, verifyCode));
    }
    private String xmlParam(String attr_name,Map<String,Object> map){
        StringBuffer sb = new StringBuffer("<");
        sb.append(attr_name);
        Iterator records = map.entrySet().iterator();
        List<String> childList = new ArrayList<>();
        while (records.hasNext()) {
            Map.Entry entry = (Map.Entry) records.next();
            String key = entry.getKey().toString();
            Object obj = entry.getValue();
            if(obj instanceof String){
                sb.append(StringUtils.SPACE).append(key).append("='").append(entry.getValue()).append("'");
            }else if(obj instanceof Map){
                childList.add(xmlParam(key,(Map<String, Object>)obj));
            }
        }
        sb.append(">");
        sb.append(StringUtils.join(childList,""));
        sb.append("</").append(attr_name).append(">");
        return sb.toString();
    }
    /**
     * 返回数据
     * @param service_name
     * @param map
     * @return
     */
    private String xmlMain(String service_name,String attr_name,Map<String,Object> map){
        return String.format(REQUEST_TPL, service_name, code, xmlParam(attr_name, map));
    }

    /**
     * 得到校验码
     * @param str
     * @return
     */
    private String verifyCode(String str){
        return encodeBase64(md5Encrypt(str+checkword));
    }


    private static byte[] md5Encrypt(String encryptStr) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(encryptStr.getBytes("utf8"));
            return md5.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String encodeBase64(byte[] b) {
        BASE64Encoder base64Encode = new BASE64Encoder();
        return base64Encode.encode(b);
    }
}
