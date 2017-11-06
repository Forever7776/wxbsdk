package com.wxb.sdk.wx.kit;

import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by luomhy on 2015/3/18.
 */
public class HttpExtKit {
    private static String readResponseString(HttpURLConnection conn) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;

        try {
            inputStream = conn.getInputStream();
            BufferedReader e = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = null;

            while((line = e.readLine()) != null) {
                sb.append(line).append("\n");
            }

            String var5 = sb.toString();
            return var5;
        } catch (Exception var14) {
            throw new RuntimeException(var14);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }
        }
    }
    /**
     * 模拟form表单的形式 ，上传文件 以输出流的形式把文件写入到url中，然后用输入流来获取url的响应
     *
     * @param url 请求地址 form表单url地址
     * @param file 文件在服务器保存路径
     * @return String url的响应信息返回值
     * @throws IOException
     */
    public static String send(String url, File file,Map<String, String> queryParas) throws IOException {
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }

        /**
         * 第一部分
         */
        URL urlObj = new URL(buildUrlWithQueryString(url,queryParas));
        // 连接
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
        /**
         * 设置关键值
         */
        con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false); // post方式不能使用缓存
        // 设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        // 设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        String name = file.getName();
        String upName = name.toUpperCase();
        String contentType = "";
        if(upName.endsWith(".JPG") ||upName.endsWith(".JPEG")){
            contentType = "image/jpeg";
        }else if(upName.endsWith(".GIF")){
            contentType = "image/gif";
        }else if(upName.endsWith(".PNG")){
            contentType = "image/png";
        }
        // 请求正文信息
        // 第一部分：
        StringBuilder sb = new StringBuilder();
        sb.append("--"); // 必须多两道线
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"media\";filename=\"");
        sb.append(name).append("\"\n");
        sb.append("Content-Type:").append(contentType).append("\r\n\r\n");
        //sb.append("Content-Type:application/octet-stream\r\n\r\n");
        byte[] head = sb.toString().getBytes("utf-8");
        // 获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        // 输出表头
        out.write(head);
        // 文件正文部分
        // 把文件已流文件的方式 推入到url中
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();
        // 结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
        out.write(foot);
        out.flush();
        out.close();
        return readResponseString(con);
    }
    public static boolean downFile(String urlstr,String data,String filename){
        try {
            int bytesum = 0;
            int byteread = 0;
            URL url = new URL(urlstr);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(19000);
            conn.setReadTimeout(19000);
            //写数据过去
            OutputStream e = conn.getOutputStream();
            e.write(data.getBytes("UTF-8"));
            e.flush();
            e.close();
            InputStream inputStream = conn.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            byte[] buffer = new byte[2048];
            while ((byteread = inputStream.read(buffer)) != -1) {
                bytesum += byteread;
                fileOutputStream.write(buffer, 0, byteread);
            }
            fileOutputStream.close();
            inputStream.close();
            conn.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    private static String buildUrlWithQueryString(String url, Map<String, String> queryParas) {
        if(queryParas != null && !queryParas.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            boolean isFirst;
            if(url.indexOf("?") == -1) {
                isFirst = true;
                sb.append("?");
            } else {
                isFirst = false;
            }

            String key;
            String value;
            for(Iterator i$ = queryParas.entrySet().iterator(); i$.hasNext(); sb.append(key).append("=").append(value)) {
                Map.Entry entry = (Map.Entry)i$.next();
                if(isFirst) {
                    isFirst = false;
                } else {
                    sb.append("&");
                }

                key = (String)entry.getKey();
                value = (String)entry.getValue();
                if(StrKit.notBlank(value)) {
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException var9) {
                        throw new RuntimeException(var9);
                    }
                }
            }

            return sb.toString();
        } else {
            return url;
        }
    }

    /**
     *
     * @param url 退款地址
     * @param cate 证书地址
     * @param pwd 密码
     * @param data 数据
     * @return
     * @throws KeyStoreException
     * @throws FileNotFoundException
     */
    public static String HttpCateP12(String url,String cate,String pwd,String data) throws KeyStoreException, FileNotFoundException {
        String result = StringUtils.EMPTY;
        try{
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream instream = new FileInputStream(new File(cate));
            try {
                keyStore.load(instream, pwd.toCharArray());
            } finally {
                instream.close();
            }
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, pwd.toCharArray()).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext, new String[]{"TLSv1"}, null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
            try {
                HttpPost httpPost = new HttpPost(url);
                StringEntity stringEntity = new StringEntity(data);
                httpPost.setEntity(stringEntity);
                System.out.println("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();

                    System.out.println("----------------------------------------");
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        System.out.println("Response content length: " + entity.getContentLength());
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
                        String text;
                        while ((text = bufferedReader.readLine()) != null) {
                            result +=text;
                        }

                    }
                    EntityUtils.consume(entity);
                } finally {
                    response.close();
                }
            } finally {
                httpclient.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String... args){
    }
    /**
     * byte数组转换成16进制字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    /**
     * 根据文件流读取图片文件真实类型
     * @param is
     * @return
     */
    public static String getTypeByStream(FileInputStream is){
        byte[] b = new byte[4];
        try {
            is.read(b, 0, b.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str = bytesToHexString(b);
        if(str==null){
            return StringUtils.EMPTY;
        }
        String type = str .toUpperCase();
        if(type.contains("FFD8FF")){
            return "jpg";
        }else if(type.contains("89504E47")){
            return "png";
        }else if(type.contains("47494638")){
            return "gif";
        }else if(type.contains("49492A00")){
            return "tif";
        }else if(type.contains("424D")){
            return "bmp";
        }
        return type;
    }

}
