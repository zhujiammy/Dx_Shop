package com.example.zhujia.dx_shop.Tools;
/**
 * 用于存放倒计时时间
 * @author bnuzlbs-xuboyu 2017/4/5.
 */
import java.util.Map;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

public class App extends Application {
    // 用于存放倒计时时间
    public static Map<String, Long> map;

    private static Context context;

    public static Context getContext() {
        return context;
    }

    private String companyId ;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        Fresco.initialize(this);
        //YoukuPlayerConfig.setClientIdAndSecret(*//*请修改成你自己的clientId和clientSecret*//*"792b1d08a5348d0d","9a98ce3841ae9f686fbea940a93b8167");

    }
}
