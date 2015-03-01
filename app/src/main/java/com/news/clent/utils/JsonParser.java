package com.news.clent.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * json解析类
 * Created by jake（lian_weijian@126.com）
 * Date: 2015-02-24
 * Time: 01:10
 */

public class JsonParser {

    //byte数组转换为字符串
    public static String byte2String(byte[] bytes) {
        String str = new String(bytes);
        return str;
    }


    //json解析
    public static List<HashMap<String, Object>> parserJson(String str) {
        List<HashMap<String, Object>> newsList = new ArrayList<HashMap<String, Object>>();
        try {
            if (null != str) {
                JSONObject jsonObject = new JSONObject(str);
                JSONObject dataObject = jsonObject.optJSONObject("data");
                int totalnum = dataObject.getInt("totalnum");
                if (totalnum > 0) {
                    JSONArray newslist = dataObject.optJSONArray("newslist");
                    for (int i = 0; i < newslist.length(); i++) {
                        JSONObject newsObject = (JSONObject) newslist.opt(i);
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("nid", newsObject.getInt("nid"));
                        hashMap.put("newslist_item_title", newsObject.getString("title"));
                        hashMap.put("newslist_item_digest", newsObject.getString("digest"));
                        hashMap.put("newslist_item_source", newsObject.getString("source"));
                        hashMap.put("newslist_item_ptime", newsObject.getString("ptime"));
                        hashMap.put("newslist_item_comments", newsObject.getString("commentcount"));
                        newsList.add(hashMap);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }


    /**
     * 解析json获得 新闻详细内容
     *
     * @param byteData
     * @return
     */
    public static String byte2StringNewsDetail(byte[] byteData) {
        String str = null;
        String strData = new String(byteData);
        try {
            JSONObject jsonObject = new JSONObject(strData);
            JSONObject dataObject = jsonObject.optJSONObject("data");
            JSONObject newsObject = dataObject.optJSONObject("news");
            str = newsObject.getString("body");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }


    /**
     * 提交评论后，返回的标识码
     *
     * @return
     */
    public static int btye2StringCommand(String str) {
        int resCode = 0;
        try {
            JSONObject jsonObject = new JSONObject(str);
            resCode = jsonObject.optInt("ret");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resCode;
    }


    /**
     * 解析返回的评论json数据
     * @param bytes
     * @return
     * @throws JSONException
     */
    public static List<HashMap<String, Object>> btye2StringAllCommend(byte[] bytes) throws JSONException {
        String str = new String(bytes);
        List<HashMap<String, Object>> mCommsData = new ArrayList<HashMap<String, Object>>();

        JSONObject jsonObject = new JSONObject(str);
        int retCode = jsonObject.getInt("ret");
        if (0 == retCode) {
            JSONObject dataObject = jsonObject.optJSONObject("data");
            // 获取返回数目
            int totalnum = dataObject.optInt("totalnum");
            if (totalnum > 0) {
                // 获取返回新闻集合
                JSONArray commsList = dataObject.optJSONArray("commentslist");
                for(int i = 0; i < commsList.length(); i++){
                    HashMap<String, Object> map = new HashMap<String,Object>();
                    JSONObject commsObject = commsList.optJSONObject(i);
                    map.put("cid", commsObject.getInt("cid"));
                    map.put("commentator_from", commsObject.getString("region"));
                    map.put("comment_content", commsObject.getString("content"));
                    map.put("comment_ptime", commsObject.getString("ptime"));
                    mCommsData.add(map);
                }
            }
        }
        return mCommsData;
    }




}
