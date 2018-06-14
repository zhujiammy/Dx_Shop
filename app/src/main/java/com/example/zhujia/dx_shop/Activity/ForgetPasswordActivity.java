package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

//忘记密码
public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ClearEditText phonenum,Graphicverificationcode,phoneverificationcode,pass;
    private ImageView verificationCode;
    private Button btnGetVerificationCode;
    private Button register_btn;
    JSONObject object,pager;
    JSONObject reslutJSONObject;
    private String LoginState,TOKEN,loginUserId;
    private SharedPreferences sharedPreferences;
    private String code;
    private TimeCount mTimer;
    SharedPreferences.Editor editor;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fpass);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        editor = sharedPreferences.edit();
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
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
        long endDate = System.currentTimeMillis();
        long curDate = sharedPreferences.getLong("curDate",0);
        long gapdate = endDate -curDate;
        if(gapdate<60000){
            mTimer = new TimeCount(60000-gapdate, 1000);
            mTimer.start();
        }else{
            // 初始化计时器
            mTimer = new TimeCount(60000, 1000);
        }
    }

    private void initUI(){
        phonenum=(ClearEditText)findViewById(R.id.phone_num);
        verificationCode=(ImageView)findViewById(R.id.verificationCode);
        verificationCode.setOnClickListener(this);
        phonenum.addTextChangedListener(watcher);
        Graphicverificationcode=(ClearEditText)findViewById(R.id.Graphicverificationcode);
        phoneverificationcode=(ClearEditText)findViewById(R.id.phoneverificationcode);
        btnGetVerificationCode=(Button) findViewById(R.id.btnGetVerificationCode);
        btnGetVerificationCode.setOnClickListener(this);
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

    private void validate(){
        //判断用户名是否存在
        new HttpUtils().Post(Constant.APPURLS+"customer/validate?userName="+phonenum.getText().toString(),TOKEN,loginUserId,new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                Message msg= Message.obtain(
                        mHandlers,3,data
                );
                mHandlers.sendMessage(msg);
            }

            @Override
            public void onError(String msg) {
                Log.e("TAG", "onError: "+msg );
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
                if (judge) {
                    btnGetVerificationCode.setClickable(true);
                    btnGetVerificationCode.setBackgroundResource(R.drawable.dialog_button2);
                    validate();
                } else {
                    btnGetVerificationCode.setClickable(false);
                    btnGetVerificationCode.setBackgroundResource(R.drawable.dialog_button3);
                    Toast.makeText(getApplicationContext(),"手机号码格式不正确",Toast.LENGTH_SHORT).show();
                }
            }else {
                verificationCode.setVisibility(View.INVISIBLE);
            }



        }
    };

    @Override
    public void onClick(View v) {

        if(v==verificationCode){
            verification();
        }

        if(v==register_btn){
            //提交

            if(!TextUtils.isEmpty(phonenum.getText().toString())){

                if(!TextUtils.isEmpty(Graphicverificationcode.getText().toString())){
                    if(!TextUtils.isEmpty(phoneverificationcode.getText().toString())){
                        if(!TextUtils.isEmpty(pass.getText().toString())){
                            object = new JSONObject();
                            pager=new JSONObject();
                            try {
                                object.put("mobile",phonenum.getText().toString());
                                object.put("password",pass.getText().toString());
                                object.put("shortMessage",phoneverificationcode.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String params=object.toString();
                            new HttpUtils().postJsoncode(Constant.APPURLS+"customer/reset/password",params,new HttpUtils.HttpCallback() {

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
                        }else {
                            Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"手机证码不能为空",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"图形验证码不能为空",Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(getApplicationContext(),"手机号码不能为空",Toast.LENGTH_SHORT).show();
            }

        }
        if(v==btnGetVerificationCode){
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
                        break;

                    case 1:
                        reslutJSONObject=new JSONObject(msg.obj.toString());
                        if(reslutJSONObject.getString("code").equals("200")){

                            editor.putLong("curDate",System.currentTimeMillis());
                            editor.commit();
                            startTimeCount();
                        }

                        break;

                    case 2:

                        reslutJSONObject=new JSONObject(msg.obj.toString());
                        if(reslutJSONObject.getString("code").equals("200")){
                            Toast toast = Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            finish();
                        }

                        break;
                    case 3:
                        reslutJSONObject=new JSONObject(msg.obj.toString());
                        code=reslutJSONObject.getString("code");
                        if(code.equals("500")){
                            verification();
                            verificationCode.setVisibility(View.VISIBLE);
                        }
                        if(code.equals("200")){
                            verificationCode.setVisibility(View.INVISIBLE);
                            Toast toast = Toast.makeText(getApplicationContext(), "用户名不存在，请注册", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_m, menu);
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
        if(id==R.id.register_btn){
            Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 启动计时器
     */
    public void startTimeCount() {
        mTimer.start();
    }

    /**
     * 倒计时器
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            // 参数依次为总时长,和计时的时间间隔
            super(millisInFuture, countDownInterval);
        }

        /**
         * 计时完毕时触发
         */
        @Override
        public void onFinish() {
            btnGetVerificationCode.setClickable(true);
            btnGetVerificationCode.setBackgroundResource(R.drawable.dialog_button2);
            btnGetVerificationCode.setText("获取验证码");
        }

        /**
         * 计时过程显示
         */
        @Override
        public void onTick(long millisUntilFinished) {
            btnGetVerificationCode.setClickable(false);
            btnGetVerificationCode.setBackgroundResource(R.drawable.dialog_button3);
            btnGetVerificationCode.setText(millisUntilFinished / 1000 + "s");
        }
    }
}
