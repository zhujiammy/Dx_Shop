package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.ClearEditText;
import com.example.zhujia.dx_shop.Tools.ImageService;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

//性别修改

public class Sex extends AppCompatActivity {

    private RadioButton M,F;
    private Toolbar toolbar;
    private Drawable drawable;
    private  Intent intent;
    private SharedPreferences sharedPreferences;
    private String LoginState,TOKEN,loginUserId,nickNamestr,sexstr,sex;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sex);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        M=(RadioButton) findViewById(R.id.M);
        F=(RadioButton) findViewById(R.id.F);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        nickNamestr=sharedPreferences.getString("nickName","");
        sexstr=sharedPreferences.getString("sex","");

        if(sexstr.equals("M")){
            M.isChecked();
            drawable = getResources().getDrawable(R.mipmap.select);
            drawable.setBounds(0, 0, 60, 60);
            M.setCompoundDrawables(null,null,drawable,null);
        }
        if(sexstr.equals("F")){
            F.isChecked();
            drawable = getResources().getDrawable(R.mipmap.select);
            drawable.setBounds(0, 0, 60, 60);
            F.setCompoundDrawables(null,null,drawable,null);
        }

        M.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex="M";
                drawable = getResources().getDrawable(R.mipmap.select);
                drawable.setBounds(0, 0, 60, 60);
                M.setCompoundDrawables(null,null,drawable,null);
                F.setCompoundDrawables(null,null,null,null);
                save("sex",sex);
                intent=new Intent();
                intent.putExtra("sex",sex);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex="F";
                drawable = getResources().getDrawable(R.mipmap.select);
                drawable.setBounds(0, 0, 60, 60);
                F.setCompoundDrawables(null,null,drawable,null);
                M.setCompoundDrawables(null,null,null,null);
                save("sex",sex);
                intent=new Intent();
                intent.putExtra("sex",sex);
                setResult(RESULT_OK,intent);
                finish();
            }
        });



    }




    private void save(String parmsname,String parms){

        JSONObject object = new JSONObject();
        try {
            object.put(parmsname,parms);


            String params=object.toString();
            new HttpUtils().postJson(Constant.APPURLS+"account/update",params,TOKEN,loginUserId,new HttpUtils.HttpCallback() {
                @Override
                public void onSuccess(String data) {
                    // TODO Auto-generated method stub
                    com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                    Message msg= Message.obtain(
                            mHandler,2,data
                    );
                    mHandler.sendMessage(msg);
                }

            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what) {
                    case 2:
                        JSONObject header=new JSONObject(msg.obj.toString());
                        if(header.getString("code").equals("200")){
                            SharedPreferences.Editor editors=sharedPreferences.edit();
                            editors.putString("sex",sex);
                            editors.commit();
                        }
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };




}
