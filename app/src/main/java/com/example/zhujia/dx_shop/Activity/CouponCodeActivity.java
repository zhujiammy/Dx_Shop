package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.ClearEditText;
import com.example.zhujia.dx_shop.Tools.ImageService;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//昵称

public class CouponCodeActivity extends AppCompatActivity {

    private EditText couponCode;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private String LoginState,TOKEN,loginUserId,nickNamestr;
    private String addressId,productItemIds;
    private Intent intent;
    private String json;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.couponcode);
        intent=getIntent();
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
        couponCode=(EditText) findViewById(R.id.couponCode);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        addressId=intent.getStringExtra("addressId");
        productItemIds=intent.getStringExtra("productItemIds");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void save(){

        JSONObject object = new JSONObject();
        try {
            String [] stringArr= productItemIds.split(",");
            JSONArray j=new JSONArray(stringArr);
            object.put("addressId",addressId);
            object.put("couponCode",couponCode.getText().toString());
            object.put("productItemIds",j);
            String params=object.toString();
            Log.e("TAG", "save: "+params);
           new HttpUtils().postJson(Constant.APPURLS+"promotion/coupon",params,TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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
                        if(header.getString("code").equals("503")){
                            Toast.makeText(getApplicationContext(),header.getString("object"),Toast.LENGTH_SHORT).show();
                        }
                        if(header.getString("code").equals("404")){
                            Toast.makeText(getApplicationContext(),header.getString("object"),Toast.LENGTH_SHORT).show();
                        }
                        if(header.getString("code").equals("200")){
                            json=header.getString("object");
                            Intent intent=new Intent();
                            intent.putExtra("json",json);
                            intent.putExtra("couponCode",couponCode.getText().toString());
                            Log.e("TAG", "onOptionsItemSelected: "+json );
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.save_btn){
            save();
        }



        return super.onOptionsItemSelected(item);
    }


}
