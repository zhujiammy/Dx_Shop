package com.example.zhujia.dx_shop.Tools;

import android.app.Application;
import android.content.Context;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by DXSW5 on 2017/7/6.
 */

public class DXApp extends Application {

    public int toTabIndex;
    private static DXApp instance;
    public boolean hasNews;


    public static DXApp getInstance() {
        return instance;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        hasNews = false;
        instance = this;
        toTabIndex = -1;

        // 初始化 JPush
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }


}
