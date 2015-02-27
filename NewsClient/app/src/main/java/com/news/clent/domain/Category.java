package com.news.clent.domain;

/**
 * Created by jake（lian_weijian@126.com）
 * Date: 2015-02-23
 * Time: 15:17
 */

public class Category {

    //类型编号
    private int cid;
    //类型名称
    private String title;
    //类型次数
    private int sequnce;

    public Category() {
    }

    public Category(int cid, String title) {
        this.cid = cid;
        this.title = title;
    }

    public Category(int cid, String title, int sequnce) {
        this.cid = cid;
        this.title = title;
        this.sequnce = sequnce;
    }


    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSequnce() {
        return sequnce;
    }

    public void setSequnce(int sequnce) {
        this.sequnce = sequnce;
    }

    @Override
    public String toString() {
        return title;
    }
}
