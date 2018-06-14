package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.AESUtil;
import com.example.zhujia.dx_shop.Tools.App;
import com.example.zhujia.dx_shop.Tools.ClearEditText;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

//密码修改

public class AccountsecurityActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private String LoginState,TOKEN,loginUserId,password;
    private ClearEditText Oldpass,newpass,confirmnewpass;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountsecurity);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        password= AESUtil.decrypt("dxracer",sharedPreferences.getString("password",""));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
        initUI();

    }

    private void initUI(){
        Oldpass=(ClearEditText)findViewById(R.id.Oldpass);
        newpass=(ClearEditText)findViewById(R.id.newpass);
        confirmnewpass=(ClearEditText)findViewById(R.id.confirmnewpass);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.save_btn){
            if(!TextUtils.isEmpty(Oldpass.getText().toString())){
                if(Oldpass.getText().toString().equals(password)) {
                    if(!TextUtils.isEmpty(newpass.getText().toString())){
                        if(newpass.getText().toString().equals(confirmnewpass.getText().toString())){

                            if(newpass.getText().length()>=6){
                                new HttpUtils().Post(Constant.APPURLS+"customer/update/password?password="+newpass.getText().toString()+"",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
                                    @Override
                                    public void onSuccess(String data) {
                                        // TODO Auto-generated method stub
                                        com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                                        Message msg= Message.obtain(
                                                mHandler,0,data
                                        );
                                        mHandler.sendMessage(msg);
                                    }

                                });
                            }else {
                                Toast.makeText(getApplicationContext(),"密码长度不能小于6位",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"密码输入不一致",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"新密码不能为空",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(),"您的旧密码不正确",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getApplicationContext(),"旧密码不能为空",Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
          try{
              switch (msg.what){
                  case 0:
                      JSONObject object=new JSONObject(msg.obj.toString());
                      if(object.getString("code").equals("200")){
                          Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_SHORT).show();
                          finish();
                      }
                      break;
              }
          }catch (JSONException e){
              e.printStackTrace();
          }
        }
    };
}
