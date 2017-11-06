package com.wxb.sdk.qiniu;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.File;

/**
 * Created by luomhy on 2015/8/26.
 */
public class QiNiuApi {
    private static final Log logger = Log.getLog(QiNiuApi.class);
    private static String uploadToken;
    private static UploadManager uploadManager;
    public static QiNiuApi qiNiu;
    public static Auth auth;
    private static String bucket;

    public QiNiuApi(String access_key, String secret_key, String bucket){
        auth = Auth.create(access_key, secret_key);
        this.bucket = bucket;
        reload();
        uploadManager = new UploadManager();
        qiNiu = this;
    }

    public static void  reload(){
        uploadToken = auth.uploadToken(bucket);
    }

    public static JSONObject upload(File file) throws QiniuException {
        Response resp = uploadManager.put(file,file.getName(),uploadToken);
        JSONObject jo = new JSONObject();
        if(resp.isOK()) {
            String result = resp.bodyString();
            jo = JSONObject.parseObject(result);
            jo.put("result_code","SUCCESS");
        }else{
            jo.put("result_code","ERROR");
        }
        return jo;
    }
}
