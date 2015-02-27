package com.news.clent.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.news.clent.R;
import com.news.clent.services.NetServices;
import com.news.clent.utils.JsonParser;

import org.apache.http.HttpEntity;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class CommentsActivity extends Activity {


    private List<HashMap<String,Object>> commentsList = null;
    private TextView commentator_from = null;

    private TextView comment_ptime = null;

    private TextView comment_content = null;

    private ListView comments_list = null;

    private String nid = null;

    private int startnid = 0;

    private static final int LOAD = 1;

    private static final int FRESH = 2;

    private  SimpleAdapter adapter = null;

    private  SwipeRefreshLayout comments_swipeFrfreshlayout;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case LOAD:
                    commentsList = (List<HashMap<String, Object>>) msg.obj;
                     adapter = new SimpleAdapter(CommentsActivity.this,commentsList,R.layout.comments_list_item
                            ,new String[]{"commentator_from","comment_ptime","comment_content"}
                            ,new int[]{R.id.commentator_from,R.id.comment_ptime,R.id.comment_content});

                    comments_list.setAdapter(adapter);
                break;

                case FRESH:
                    comments_swipeFrfreshlayout.setRefreshing(false);
                    List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) msg.obj;
                    commentsList.addAll(list);
                    adapter.notifyDataSetChanged();
                break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.comments);
        commentsList = new ArrayList<HashMap<String,Object>>();
        comments_list = (ListView) this.findViewById(R.id.comments_list);
        init();
    }

    public void init(){
        this.commentator_from = (TextView) this.findViewById(R.id.commentator_from);
        this.comment_ptime = (TextView) this.findViewById(R.id.comment_ptime);
        this.comment_content = (TextView) this.findViewById(R.id.comment_content);

        Intent intent = this.getIntent();
        this.nid = intent.getStringExtra("nid");

        comments_swipeFrfreshlayout = (SwipeRefreshLayout) this.findViewById(R.id.comments_swipeFrfreshlayout);
        comments_swipeFrfreshlayout.setOnRefreshListener(new SwipeRefreshLayoutLinstener()); //设置刷新监听
        getCommentThrad();
      /*  for(int i = 0; i < 10 ; i++){
            HashMap<String,String> hashMap = new HashMap<String,String>();
            hashMap.put("commentator_from","连云港朋友");
            hashMap.put("comment_ptime","2012-03-22 20:21:22");
            hashMap.put("comment_content","这样视频真的太好的！");
            commentsList.add(hashMap);
        }*/


    }



    public List<HashMap<String, Object>> getComment(String nid, int startnid, int count) throws IOException, JSONException {
        String url = this.getResources().getString(R.string.getcomment_url);
        Map<String,String> params = new HashMap<String,String>();
        params.put("nid",nid);
        params.put("startnid",startnid+"");
        params.put("count",count+"");
        String trueUrl = NetServices.getURLString(url, params);
        HttpEntity entity = NetServices.sendGetRequest(trueUrl);
        byte[] bytes = NetServices.getByteDate(entity.getContent());
        List<HashMap<String,Object>> commentList = JsonParser.btye2StringAllCommend(bytes);
        return commentList;
    }


    public void getCommentThrad(){

        new Thread(){
            @Override
            public void run() {
                try {
                    Message msg = handler.obtainMessage();
                    msg.obj = getComment(nid,0,5);
                    msg.what = LOAD;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }



    public void gtCommentThreadFresh(){
        startnid += 5;
        new Thread(){
            @Override
            public void run() {
                try {
                    Message msg = handler.obtainMessage();
                    msg.obj = getComment(nid,startnid,5);
                    msg.what = FRESH;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }



    private class SwipeRefreshLayoutLinstener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            gtCommentThreadFresh();
        }
    }





}
