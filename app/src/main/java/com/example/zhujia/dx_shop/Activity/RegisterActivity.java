package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.ClearEditText;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.TimeButton;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.zhujia.dx_shop.Tools.insertComma.isMobile;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ClearEditText phonenum,Graphicverificationcode,phoneverificationcode,pass;
    private ImageView verificationCode;
    private TimeButton Getverify;
    private Button register_btn;
    JSONObject object,pager;
    JSONObject reslutJSONObject;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
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
        phonenum=(ClearEditText)findViewById(R.id.phone_num);
        verificationCode=(ImageView)findViewById(R.id.verificationCode);
        verificationCode.setOnClickListener(this);
        phonenum.addTextChangedListener(watcher);
        Graphicverificationcode=(ClearEditText)findViewById(R.id.Graphicverificationcode);
        phoneverificationcode=(ClearEditText)findViewById(R.id.phoneverificationcode);
        Getverify=(TimeButton)findViewById(R.id.Getverify);
        Getverify.setOnClickListener(this);
        pass=(ClearEditText)findViewById(R.id.pass);
        register_btn=(Button)findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);

    }

    private void verification(){

        new HttpUtils().Get(Constant.APPURLS+"/customer/"+phonenum.getText()+"/verification.png"+"/",new HttpUtils.HttpCallback() {

            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandlers,0,data
                );
                mHandlers.sendMessage(msg);
            }

        });
    }

    TextWatcher watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String number = phonenum.getText().toString();
            boolean judge = isMobile(number);
            if(s.length()==11){
                if (judge == true) {
                    verification();
                } else {
                    Toast.makeText(getApplicationContext(),"手机号码格式不正确",Toast.LENGTH_SHORT).show();
                }
            }



        }
    };

    @Override
    public void onClick(View v) {

        if(v==verificationCode){
            verification();
        }

        if(v==register_btn){
            //注册
            object = new JSONObject();
            pager=new JSONObject();
            try {
                object.put("loginName",phonenum.getText().toString());
                object.put("passWord",pass.getText().toString());
                object.put("shortMessage",phoneverificationcode.getText().toString());
                object.put("verificationCode",Graphicverificationcode.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String params=object.toString();
            new HttpUtils().postJsoncode(Constant.APPURLS+"/customer/register",params,new HttpUtils.HttpCallback() {

                @Override
                public void onSuccess(String data) {
                    // TODO Auto-generated method stub
                    com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                    Message msg= Message.obtain(
                            mHandlers,2,data
                    );
                    mHandlers.sendMessage(msg);
                }

            });
        }
        if(v==Getverify){
            //获取手机验证码
            if(TextUtils.isEmpty(phonenum.getText().toString())){
                Toast.makeText(getApplicationContext(),"手机号码不能为空！",Toast.LENGTH_SHORT).show();
            }
           else if(TextUtils.isEmpty(Graphicverificationcode.getText().toString())){
                Toast.makeText(getApplicationContext(),"图形验证码不能为空！",Toast.LENGTH_SHORT).show();
            } else  {
                object = new JSONObject();
                pager=new JSONObject();
                try {
                    object.put("mobile",phonenum.getText().toString());
                    object.put("verificationCode",Graphicverificationcode.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String params=object.toString();
                new HttpUtils().postJsoncode(Constant.APPURLS+"/customer/register/shortmessage",params,new HttpUtils.HttpCallback() {

                    @Override
                    public void onSuccess(String data) {
                        // TODO Auto-generated method stub
                        com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                        Message msg= Message.obtain(
                                mHandlers,1,data
                        );
                        mHandlers.sendMessage(msg);
                    }

                });
            }

        }

    }




    @SuppressLint("HandlerLeak")
    private Handler mHandlers=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {
                switch (msg.what) {

                    case 0:
                        //返回item类型数据
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // Glide.get(getApplicationContext()).clearDiskCache();   // 必须在子线程中执行
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Glide.get(getApplicationContext()).clearMemory();    // 必须在主线程中执行
                                        Glide.with(getApplicationContext()).load(Constant.APPURLS+"customer/"+phonenum.getText()+"/verification.png").skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(verificationCode);
                                    }
                                });
                            }
                        }).start();
                        Log.e("TAG", "handleMessage: "+ Constant.APPURLS+"customer/"+phonenum.getText()+"/verification.png");

                        break;

                    case 1:

                       reslutJSONObject=new JSONObject(msg.obj.toString());
                        if(reslutJSONObject.getString("code").equals("200")){
                            Getverify.setTextAfter("秒后重新验证").setTextBefore("获取验证码").setLenght(10 * 6000);
                        }

                        break;

                    case 2:

                       reslutJSONObject=new JSONObject(msg.obj.toString());
                        if(reslutJSONObject.getString("code").equals("200")){
                            Toast.makeText(getApplicationContext(),"注册成功！",Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        break;

                    default:
                        Toast.makeText(getApplicationContext(),"网络异常", Toast.LENGTH_SHORT).show();
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }



        }
    };
}
