package com.news.clent.custom.controls;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.List;
import java.util.Map;

/**
 * 为了实现一开始 焦点的item就被选中，所以要获得第一个item的textview 但是
 * 用GridView的getPosition 获取的时候是null，因为即使是设置了adapter程序也没有
 * 绘制完。但是重写GridView是在太麻烦，所以重写SimpleAdapter，因为adapter可以获得
 * 每个item里面的控件View 再转换一下就可以了
 * Created by jake（lian_weijian@126.com）
 * Time: 11:24
 */

public class CustomSimpleAdapter extends SimpleAdapter {
    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    public CustomSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = (TextView) super.getView(position, convertView, parent);
        if( 0 == position){
            tv.setTextColor(Color.WHITE);
        }
        return tv;
    }
}
