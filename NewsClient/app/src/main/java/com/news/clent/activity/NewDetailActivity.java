package com.news.clent.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.kale.activityoptions.ActivityCompatICS;
import com.kale.activityoptions.ActivityOptionsCompatICS;
import com.kale.activityoptions.transition.TransitionCompat;
import com.news.clent.R;
import com.news.clent.services.NetServices;
import com.news.clent.utils.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class NewDetailActivity extends Activity implements View.OnClickListener,View.OnTouchListener,GestureDetector.OnGestureListener {


    private ViewFlipper news_body_flipper = null;
    private Button newsdetails_titlebar_previous = null;
    private Button newsdetails_titlebar_next = null;
    private Button newsdetails_titlebar_comments = null;
    private Button news_share_btn = null;
    private TextView newsdetails_titlebar_title = null;
    private float mStratX = 0;
    private int count = 0;
    private String nid = null;


    TextView news_body_title = null;
    TextView news_body_ptime_source = null;
    TextView news_body_details = null;

    private CardView news_body_cardview = null;

    private LayoutInflater inflater = null;

    private List<HashMap<String, Object>> newsList;

    private String categoryTitleStr = null;

    private int position = 0;

    private int initPostion = 0;

    private int newsLen = 0;

    private RelativeLayout newsdetail_list_rl = null;

    private LinearLayout news_reply_edit_layout = null;

    private LinearLayout news_reply_img_layout = null;

    private EditText news_reply_edittext = null;

    private Button news_reply_post = null;


    private GestureDetector detector; //手势检测

    Animation leftInAnimation;
    Animation leftOutAnimation;
    Animation rightInAnimation;
    Animation rightOutAnimation;

    private static final int SENDCOMMAND = 2;
    private static final int LOADNEWS = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADNEWS:
//                    newsdetail_list_rl.setVisibility(View.GONE);
                    String newDetail = (String) msg.obj;
                    news_body_details.setText(Html.fromHtml(newDetail));

                    break;

                case SENDCOMMAND:
                    int code = (int) msg.obj;
                    if (0 == code) {
                        Toast.makeText(NewDetailActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewDetailActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        detector = new GestureDetector(this);
        setContentView(R.layout.newdetails);

        this.news_body_cardview = (CardView) this.findViewById(R.id.news_body_cardview);

        this.news_reply_edit_layout = (LinearLayout) this.findViewById(R.id.news_reply_edit_layout);
        this.news_reply_img_layout = (LinearLayout) this.findViewById(R.id.news_reply_img_layout);
        this.news_reply_edittext = (EditText) this.findViewById(R.id.news_reply_edittext);

        this.news_reply_post = (Button) this.findViewById(R.id.news_reply_post);
        this.news_reply_post.setOnClickListener(this);

        //this.newsdetail_list_rl = (RelativeLayout) this.findViewById(R.id.newsdetail_list_rl);
        this.news_body_flipper = (ViewFlipper) this.findViewById(R.id.news_body_flipper);
        this.newsdetails_titlebar_previous = (Button) this.findViewById(R.id.newsdetails_titlebar_previous);
        this.newsdetails_titlebar_next = (Button) this.findViewById(R.id.newsdetails_titlebar_next);
        this.newsdetails_titlebar_comments = (Button) this.findViewById(R.id.newsdetails_titlebar_comments);
        this.newsdetails_titlebar_title = (TextView) this.findViewById(R.id.newsdetails_titlebar_title);
        this.newsdetails_titlebar_next.setOnClickListener(this);
        this.newsdetails_titlebar_previous.setOnClickListener(this);

        this.news_reply_edit_layout.setOnClickListener(this);
        this.news_reply_img_layout.setOnClickListener(this);
        this.newsdetails_titlebar_comments.setOnClickListener(this);
        news_body_flipper.setOnTouchListener(this);
        init();

        //动画效果
        leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.left_in);
        leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.left_out);
        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.right_in);
        rightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.right_out);

}


    public void init(){
        InitNews();
        HashMap<String,Object> hashMap = getNews(this.position);
        View flipperView = getFlippperView(hashMap);
        news_body_flipper.getChildCount();
        news_body_flipper.addView(flipperView);
    }


    /**
     * 获取新闻
     */
    public void InitNews(){
        //获取传递的数据
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        // 获得标题栏名称
        categoryTitleStr = bundle.getString("categoryTitleStr");
        newsdetails_titlebar_title.setText(categoryTitleStr);
        //获取新闻集合
        newsList = (List<HashMap<String, Object>>) bundle.getSerializable("newsList");
        newsLen = newsList.size();
        //获取nid
        this.nid = bundle.getString("nid");
        //获取点击位置
        this.initPostion = bundle.getInt("position");
        this.position = initPostion;

    }


    /**
     * 根据位置获得新闻
     * @param varPositon
     * @return
     */
    public  HashMap<String,Object> getNews(int varPositon){
        //获取点击新闻基本信息
        if(varPositon >= newsLen){
           // NewDetailActivity.this.position = NewDetailActivity.this.initPostion;
            NewDetailActivity.this.position = 0;
            //varPositon = newsLen-1;
        }
        if(varPositon < 0){
            NewDetailActivity.this.position = this.newsLen;
           // varPositon = 0;
        }
        HashMap<String, Object> hashMap = newsList.get(NewDetailActivity.this.position);
        this.newsdetails_titlebar_comments.setText(hashMap.get("newslist_item_comments").toString()+" 跟帖");

       return hashMap;
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //显示上一条新闻按钮
            case R.id.newsdetails_titlebar_previous:
                showPreviousFipperView();
            break;

            //显示下一条新闻按钮
            case R.id.newsdetails_titlebar_next:
                showNextFipperView();
            break;

            //顶栏的查看全部评论的按钮
            case R.id.newsdetails_titlebar_comments:
                Intent intent = new Intent(NewDetailActivity.this,CommentsActivity.class);
                intent.putExtra("nid",this.nid);
                //this.startActivity(intent);

                ActivityOptionsCompatICS options = ActivityOptionsCompatICS.
                        makeSceneTransitionAnimation(this, this.newsdetails_titlebar_comments,R.id.comments_titlebar_title);

                ActivityCompatICS.startActivity(NewDetailActivity.this, intent, options.toBundle());
            break;

            //点击装饰用的回复布局的图片
            case R.id.news_reply_img_layout:
                news_reply_edit_layout.setVisibility(View.VISIBLE);
                news_reply_img_layout.setVisibility(View.GONE);

                //获得editText的焦点并显示输入法
                news_reply_edittext.requestFocus();
                InputMethodManager m = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                m.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            break;




            //评论按钮
            case R.id.news_reply_post:
                sendCommentThread();
            break;



        }
    }



    private void  showNextFipperView(){
        this.position++;
        View view = news_body_flipper.getChildAt(this.position);
        if(null == view) {
             View new_FipperView = getFlippperView(getNews(this.position));
           // news_body_flipper.addView(new_FipperView, this.position);
            news_body_flipper.addView(new_FipperView);
        }

        //news_body_flipper.setInAnimation(this,R.anim.push_in);
        //news_body_flipper.setOutAnimation(this,R.anim.push_out);
        news_body_flipper.showNext();
    }



    private void  showPreviousFipperView(){
        this.position--;
        View view = news_body_flipper.getChildAt(this.position);
        if(null == view) {
            View new_FipperView = getFlippperView(getNews(this.position));
            news_body_flipper.addView(new_FipperView, 0);
        }

        //news_body_flipper.setInAnimation(this,R.anim.push_in);
        //news_body_flipper.setOutAnimation(this,R.anim.push_out);
        news_body_flipper.showPrevious();
    }


    /**
     * //动态创建新闻视图，并赋值
     * @param hashMap
     * @return
     */
    public View getFlippperView(HashMap<String,Object> hashMap){

        count++;
        View flipperView =  inflater.from(this).inflate(R.layout.news_body,null);

        news_body_title = (TextView) flipperView.findViewById(R.id.news_body_title);
        news_body_title.setText(hashMap.get("newslist_item_title").toString());
        news_body_ptime_source = (TextView) flipperView.findViewById(R.id.news_body_ptime_source);
        news_body_ptime_source.setText(hashMap.get("newslist_item_source").toString()+"      "+hashMap.get("newslist_item_ptime").toString());
        news_body_details = (TextView) flipperView.findViewById(R.id.news_body_details);
         this.nid = String.valueOf(hashMap.get("nid"));
        getNewsDetailThread();
        //getNewsDetailThread("3");

        this.newsdetails_titlebar_comments.setText(hashMap.get("newslist_item_comments").toString()+" 跟帖");
        news_body_details.setOnTouchListener(this);
        return flipperView;
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {


        return this.detector.onTouchEvent(event);//touch事件交给手势处理
    }


    /**
     * 获得新闻详细内容
     * @return
     */
    public String getNewsDetail() throws IOException {
        String url = this.getResources().getString(R.string.newdetail_url);
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("nid",this.nid);
        String trueUrl = NetServices.getURLString(url,hashMap);
        HttpEntity entity = NetServices.sendGetRequest(trueUrl);
        byte[] byteData = NetServices.getByteDate(entity.getContent());
        String newsDetailStr = JsonParser.byte2StringNewsDetail(byteData);
        return newsDetailStr;
    }


    /**
     * 发送评论
     * @return
     */
    public int sendComment() throws IOException {
         String url = this.getResources().getString(R.string.sendcomment_url);
         List<NameValuePair> params = new ArrayList<NameValuePair>();
         params.add(new BasicNameValuePair("nid", this.nid));
         params.add(new BasicNameValuePair("region", "江苏省连云港市"));
         params.add(new BasicNameValuePair("content", news_reply_edittext.getText().toString()));
         HttpEntity entity = NetServices.sendPostRequest(url, params);
         //byte[] byteData = NetServices.getByteDate(entity.getContent());
          String strData = EntityUtils.toString(entity);
         int resCode = JsonParser.btye2StringCommand(strData);
         return resCode ;
    }



    public void sendCommentThread(){
        new Thread(){
            @Override
            public void run() {
                try {
                    Message msg = handler.obtainMessage();
                    msg.what = SENDCOMMAND;
                    msg.obj = sendComment();
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 获得新闻详细信息的线程
     */
    public void getNewsDetailThread(){
        new Thread(){
            @Override
            public void run() {
                try {
                    Message msg = handler.obtainMessage();
                    msg.obj = getNewsDetail();
                    msg.what = LOADNEWS;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }


    @Override
    public boolean onDown(MotionEvent e) {

                //隐藏真实回复低栏
                news_reply_edit_layout.setVisibility(View.GONE);
                news_reply_img_layout.setVisibility(View.VISIBLE);
                //隐藏输入法
                InputMethodManager m = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if(e1.getX()-e2.getX()>120){
            news_body_flipper.setInAnimation(leftInAnimation);
            news_body_flipper.setOutAnimation(leftOutAnimation);
            //news_body_flipper.showNext();//向右滑动
            showNextFipperView();
            return true;
        }else if(e1.getX()-e2.getY()<-120){
            news_body_flipper.setInAnimation(rightInAnimation);
            news_body_flipper.setOutAnimation(rightOutAnimation);
            //news_body_flipper.showPrevious();//向左滑动
            showPreviousFipperView();
            return true;
        }
        return false;
    }
}
