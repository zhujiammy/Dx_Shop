package com.example.zhujia.dx_shop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;

/**
 * Created by DXSW5 on 2017/7/14.
 */

public class AppStaractivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.appstartxml);
        //immediately 为 true, 每次强制访问服务器更新
        new Handler().postDelayed(runnable,3000);

    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            Intent intent=new Intent(AppStaractivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
