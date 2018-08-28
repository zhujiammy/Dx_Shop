package com.example.zhujia.dx_shop.Data;

/**
 * Created by ZHUJIA on 2018/3/15.
 */

import java.io.Serializable;

@SuppressWarnings("serial")
public class AllData implements Serializable {
    private String str;
    private String text;
    private String isImg;

    public AllData() {
    }

    public AllData(String str, String text,String isImg) {
        super();
        this.str = str;
        this.text = text;
        this.isImg=isImg;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIsImg() {
        return isImg;
    }

    public void setIsImg(String isImg) {
        this.isImg = isImg;
    }

    /**
     * 为什么要重写toString()呢？
     *
     * 因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
     */
    @Override
    public String toString() {
        return text;

    }

    public String Toimg(){
        return isImg;
    }


}
