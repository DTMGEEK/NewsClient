package com.news.clent.activity;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.kale.activityoptions.ActivityCompatICS;
import com.kale.activityoptions.ActivityOptionsCompatICS;
import com.kale.activityoptions.transition.TransitionCompat;
import com.news.clent.R;
import com.news.clent.custom.controls.CustomSimpleAdapter;
import com.news.clent.domain.Category;
import com.news.clent.services.NetServices;
import com.news.clent.utils.DisplayUtil;
import com.news.clent.utils.JsonParser;
import com.news.clent.utils.StringUtil;
import org.apache.http.HttpEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主界面Activity
 */
public class MainActivity extends Activity implements View.OnClickListener {

    //以px为单位的标题栏宽度
    private  final int COLUMNWIDTHPX = 155;
    private  int columnwidthdip = 0;

    private  GridView categoryGridView = null;
    private  String[] categoryArray = null;
    private  ListView news_list = null;
    private  RelativeLayout news_list_rl = null;

    private SwipeRefreshLayout news_swipeFrfreshlayout = null;
    private List<HashMap<String, Object>> newsList = null;
    private static final int LOAD = 1;
    private static final int FRESH = 2;
    //新闻分类的id
    private int mCid = 1;

    private SimpleAdapter adapter = null;

    private  List<HashMap<String, Category>> categoryList = null;

    private Button loadMoreBtn = null;

    private int count = 0;

    private String categoryTitleStr = "焦点";

    private  Animator animator = null;

    private RelativeLayout titlebar_layout = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {

            news_swipeFrfreshlayout.setRefreshing(false);
                    switch (msg.what) {
                        //加载新闻
                        case LOAD:
                            MainActivity.this.newsList = (List<HashMap<String, Object>>) msg.obj;
                            adapter = new SimpleAdapter(MainActivity.this, MainActivity.this.newsList, R.layout.newlist_item
                                    , new String[]{"newslist_item_title", "newslist_item_digest", "newslist_item_source", "newslist_item_ptime"}
                                    , new int[]{R.id.newslist_item_title, R.id.newslist_item_digest, R.id.newslist_item_source, R.id.newslist_item_ptime});


                            news_list.setAdapter(adapter);
                        break;

                        //刷新新闻
                        case FRESH:
                            List<HashMap<String, Object>> coll = (List<HashMap<String, Object>>) msg.obj;
                            MainActivity.this.newsList.addAll(coll);
                            /*   adapter = new SimpleAdapter(MainActivity.this, MainActivity.this.newsList, R.layout.newlist_item
                                    , new String[]{"newslist_item_title", "newslist_item_digest", "newslist_item_source", "newslist_item_ptime"}
                                    , new int[]{R.id.newslist_item_title, R.id.newslist_item_digest, R.id.newslist_item_source, R.id.newslist_item_ptime});
                            */

                            adapter.notifyDataSetChanged();
                            //news_list.setAdapter(adapter);

                        break;
                    }
                            news_list_rl.setVisibility(View.GONE);
                            news_swipeFrfreshlayout.setVisibility(View.VISIBLE);

                        //为没有listview的item设置监听
                        news_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                view.setTranslationZ(30.0f);
                                HashMap<String, Object> hashMap =  MainActivity.this.newsList.get(position);
                                Intent intent = new Intent(MainActivity.this, NewDetailActivity.class);
                                intent.putExtra("nid",newsList.get(position).get("nid").toString());
                                intent.putExtra("newsList",(ArrayList)MainActivity.this.newsList);
                                intent.putExtra("categoryTitleStr",categoryTitleStr);
                                intent.putExtra("position",position);

                                /*ActivityOptionsCompatICS options = ActivityOptionsCompatICS.makeCustomAnimation(MainActivity.this,
                                        R.anim.slide_right_in, R.anim.slide_bottom_out);
                                ActivityCompatICS.startActivity(MainActivity.this, intent, options.toBundle());*/

                                //使用android5.0的转场动画 使用了开源框架可以兼容4.0
                                TransitionCompat.setAnimDuration(1000);
                                TransitionCompat.setAnimStartDelay(500);
                                ActivityOptionsCompatICS options = ActivityOptionsCompatICS.
                                        makeSceneTransitionAnimation(MainActivity.this, view, R.id.news_body_flipper);
                                ActivityCompatICS.startActivity(MainActivity.this, intent, options.toBundle());
                            }
                        });


        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //设置全屏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);


        titlebar_layout = (RelativeLayout) this.findViewById(R.id.titlebar_layout);


        init();
        gridViewOnItemClick();
        initListView();

    }


    //取出标题栏的编号和字符项目
    private List<HashMap<String,Category>> getCategoryList(){
        categoryArray = this.getResources().getStringArray(R.array.categories);
        List<HashMap<String,Category>> categoryList = new ArrayList<HashMap<String,Category>>();
        for(int i = 0 ; i < categoryArray.length ;i++) {
            String[] temp = categoryArray[i].split("[|]");
            if(temp.length == 2) {
                HashMap<String,Category> map = new HashMap<String,Category>();
                Category category = new Category(StringUtil.String2int(temp[0]), temp[1]);
                map.put("category_title",category);
                categoryList.add(map);
            }
        }
        return categoryList;
    }


    //初始化方法
    public void init(){
        news_list_rl = (RelativeLayout) this.findViewById(R.id.news_list_rl);
        news_list_rl.setVisibility(View.VISIBLE);

        news_list = (ListView) findViewById(R.id.news_list);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.load_more,null);
        news_list.addFooterView(view);
        loadMoreBtn = (Button)view.findViewById(R.id.loadmore_btn);
        loadMoreBtn.setOnClickListener(this);
        categoryList = getCategoryList();

        //下拉刷新控件
        news_swipeFrfreshlayout = (SwipeRefreshLayout) this.findViewById(R.id.news_swipeFrfreshlayout);
        news_swipeFrfreshlayout.setOnRefreshListener(new SwipeRefreshLayoutLinstener()); //设置刷新监听
        /*news_swipeFrfreshlayout.setColorSchemeColors(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
                );*/
        //下拉刷新控件

        //自定义SimpleAdapter 使返回TextView的时候第一个item（0号）已经是显示白色被选中状态
        CustomSimpleAdapter adapter = new CustomSimpleAdapter(this,categoryList,R.layout.category_title
                ,new String[]{"category_title"},new int[]{R.id.category_title});

        //把px单位转换为dip单位（在代码中定义的都是px）
        this.columnwidthdip = DisplayUtil.px2dip(this,COLUMNWIDTHPX);
        //计算标题栏的长度
        int width = columnwidthdip * categoryArray.length;

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        categoryGridView = new GridView(this);
        categoryGridView.setColumnWidth(columnwidthdip);
        categoryGridView.setNumColumns(GridView.AUTO_FIT);
        categoryGridView.setGravity(Gravity.CENTER);
        categoryGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        categoryGridView.setLayoutParams(params);
        categoryGridView.setAdapter(adapter);

        LinearLayout category_content = (LinearLayout) this.findViewById(R.id.category_content);
        category_content.addView(categoryGridView);

    }


    //设置List动画
    public void setListAnimation(){
         animator = ViewAnimationUtils.createCircularReveal(
                news_list
                ,0
                ,0
                ,0
                ,(float)Math.hypot(news_list.getWidth(), news_list.getHeight()));
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(2000);

    }


    /**
     * 设置标题栏的监听
     */
    public void gridViewOnItemClick(){
        categoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            TextView categoryTitle = null;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i < categoryArray.length; i++){
                    if(i != position){
                        TextView tv = (TextView) parent.getChildAt(i);
                        tv.setTextColor(0XFFADB2AD);
                    }
                }
                //通过parent参数可以取出每个item
                categoryTitle = (TextView) parent.getChildAt(position);
                categoryTitleStr = categoryTitle.getText().toString();
                categoryTitle.setTextColor(Color.WHITE);
                //启动线程，取得新闻
                getTitleNews(position);
                news_list_rl.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,categoryTitle.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 得到特定标题下的新闻
     * @param position
     */
    public void getTitleNews(int position){

        final Category category = categoryList.get(position).get("category_title");
        this.mCid = category.getCid();
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                       // MainActivity.this.newsList = getSpeCateNews(category.getCid(),0,newsList,true);
                        Message msg = handler.obtainMessage();
                        msg.obj =  getSpeCateNews(mCid,0);
                        msg.what = LOAD;
                        handler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    /**
     * 初始化
     */
    public void initListView(){
        count += 5;
        new Thread(){
            @Override
            public void run() {
                try {
                    Message msg = handler.obtainMessage();
                    MainActivity.this.newsList = getSpeCateNews(mCid,0);
                    msg.obj =  MainActivity.this.newsList;
                    msg.what = LOAD;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();


    }


    /**
     * 从网络获取新闻
     * @param cid
     * @param startnid
     * @throws IOException
     */
    public List<HashMap<String, Object>> getSpeCateNews(int cid,int startnid) throws IOException {
       // news_list_rl.setVisibility(View.VISIBLE);
        String url = MainActivity.this.getResources().getString(R.string.newlist_url);
        Map<String, String> params = new HashMap<String, String>();

        params.put("startnid", String.valueOf(startnid));
        params.put("count", "5");
        params.put("cid", String.valueOf(cid));

        String urlWithParams = NetServices.getURLString(url, params);
        HttpEntity entity = NetServices.sendGetRequest(urlWithParams);
        byte[] bytes = NetServices.getByteDate(entity.getContent());
        String jsonString = JsonParser.byte2String(bytes);
        return  JsonParser.parserJson(jsonString);
    }


    @Override
    public void onClick(View v) {
        count += 5;
        switch (v.getId()){
            case R.id.loadmore_btn:
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Message msg = handler.obtainMessage();
                            msg.obj = getSpeCateNews(mCid,count-5);
                            msg.what = FRESH;
                            handler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            break;
        }
    }


    /**
     * 下拉刷新监听
     */
    private class SwipeRefreshLayoutLinstener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
                new Thread(){
                    @Override
                    public void run() {

                        try {
                            Message msg = handler.obtainMessage();
                            msg.obj = getSpeCateNews(mCid,0);
                            msg.what = LOAD;
                            handler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

        }
    }




}
