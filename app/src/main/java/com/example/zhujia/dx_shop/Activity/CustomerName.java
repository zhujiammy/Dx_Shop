package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.ClearEditText;
import com.example.zhujia.dx_shop.Tools.ImageService;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomerName extends AppCompatActivity {

    private ClearEditText customerName;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private String LoginState,TOKEN,loginUserId,customerNamestr;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customername);
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
        customerName=(ClearEditText)findViewById(R.id.customerName);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        customerNamestr=sharedPreferences.getString("customerName","");
        customerName.setText(customerNamestr);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
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
                            editors.putString("customerName",customerName.getText().toString());
                            editors.commit();
                        }
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.save_btn){
            save("customerName",customerName.getText().toString());
            Intent intent=new Intent();
            intent.putExtra("customerName",customerName.getText().toString());
            setResult(RESULT_OK,intent);
            finish();

        }



        return super.onOptionsItemSelected(item);
    }


}
