package com.news.clent.services;

import com.news.clent.domain.Parameter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * 网络服务类 包括发送get请求的 拼出get的url带参数字符串  get方法发送， post方法发送请求
 * 把返回的HttpEntity对象得到的inputStream 通过ByteArrayOutputStream转换为byte数组 在转换为String
 * Created by jake（lian_weijian@126.com）
 * Date: 2015-02-23
 * Time: 17:30
 */

public class NetServices {

    /**
     * 拼出get请求带参数的url
     * @param strUrl  没有带参数的url
     * @param params  用一个map对象保存参数的key和value
     * @return  拼好的字符串
     */
    public static String getURLString(String strUrl,Map<String,String> params) {
        StringBuilder builder = null;
        if (null != strUrl && params.size() != 0){
            builder = new StringBuilder(strUrl);
            builder.append("?");
            Set<Map.Entry<String, String>> sets = params.entrySet();
            for (Map.Entry<String, String> set : sets) {
                builder.append(set.getKey());
                builder.append("=");
                builder.append(set.getValue());
                builder.append("&");
            }
            builder.deleteCharAt(builder.length() - 1);
      }
        return builder.toString();
    }


    /**
     * 得到字节数组
     * @param is  输入流
     * @return  byte数组
     */
    public static byte[] getByteDate(InputStream is){
        ByteArrayOutputStream baos = null;
        if(null != is) {
            baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024];
            try {
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       return baos.toByteArray();
    }


    /**
     * 发送get请求
     * @param url  拼好的字符串url
     * @return
     */
    public static HttpEntity sendGetRequest(String url){
        HttpEntity entity = null;
        if(null != url) {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = null;
            try {
                response = client.execute(httpGet);
                if (200 == response.getStatusLine().getStatusCode()) {
                    entity = response.getEntity();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }


    /**
     * 发送Post请求
     * @param url  请求的url地址
     * @param params  参数list（NameValuePair）
     * @return
     */

    public static HttpEntity sendPostRequest(String url,List<NameValuePair> params){
        HttpEntity entity = null;
        if(null != url && params.size() > 0) {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = null;

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                response = client.execute(httpPost);
                if (200 == response.getStatusLine().getStatusCode()) {
                    entity = response.getEntity();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    /**
     * 把Parameter类型集合转换成NameValuePair类型集合
     * @param params 参数集合
     * @return
     */
    private List<BasicNameValuePair> buildNameValuePair(List<Parameter> params)
    {
        List<BasicNameValuePair> result = new ArrayList<BasicNameValuePair>();
        for (Parameter param : params)
        {
            BasicNameValuePair pair = new BasicNameValuePair(param.getName(), param.getValue());
            result.add(pair);
        }
        return result;
    }


}
