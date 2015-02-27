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
 * Created by jake（lian_weijian@126.com）
 * Date: 2015-02-23
 * Time: 17:30
 */

public class NetServices {

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
